package org.neuralcoder.teleflux.di.impl;

import org.neuralcoder.teleflux.di.api.*;
import org.neuralcoder.teleflux.di.api.annotations.*;
import org.neuralcoder.teleflux.di.api.annotations.Module;
import org.neuralcoder.teleflux.di.api.errors.AmbiguousBeanException;
import org.neuralcoder.teleflux.di.api.errors.CyclicDependencyException;
import org.neuralcoder.teleflux.di.impl.env.MapPropertySource;
import org.neuralcoder.teleflux.di.impl.env.SimpleEnvironment;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class DefaultBeanFactory implements BeanFactory, Environment {

    private final SimpleEnvironment env = new SimpleEnvironment();
    private final Set<String> activeProfiles = new HashSet<>();

    private final Map<Class<?>, Provider<?>> byType = new ConcurrentHashMap<>();
    private final Map<Class<?>, Map<String, Provider<?>>> byQualifier = new ConcurrentHashMap<>();

    private final Set<Class<?>> concrete = ConcurrentHashMap.newKeySet();

    private final Map<String, Object> singletons = new ConcurrentHashMap<>();
    private final ShutdownManager shutdown = new ShutdownManager();

    private String[] components = new String[0];
    private boolean started = false;

    private final ThreadLocal<Deque<String>> resolving = ThreadLocal.withInitial(ArrayDeque::new);

    public DefaultBeanFactory() {
        String p = System.getProperty("teleflux.profiles", "");
        if (!p.isBlank())
            Arrays.stream(p.split(",")).map(String::trim).filter(s -> !s.isEmpty()).forEach(activeProfiles::add);

        Properties sys = System.getProperties();
        Map<String, String> sysMap = sys.entrySet().stream()
                .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue())));
        env.addPropertySource(new MapPropertySource("system", sysMap, 200));
        env.addPropertySource(new MapPropertySource("env", System.getenv(), 150));

        byType.put(BeanFactory.class, (Provider<BeanFactory>) () -> this);
        byType.put(Environment.class, (Provider<Environment>) () -> this);
    }

    @Override
    public void start() {
        if (started) return;
        started = true;
        loadIndex();
        System.out.println("[Teleflux DI] ComponentIndex loaded: " + components.length + " entries");
        registerComponents();
        registerModulesProvides();
        eagerPreinstantiate();
    }

    @Override
    public void stop() {
        if (!started) return;
        started = false;
        shutdown.runPreDestroy();
        byType.clear();
        byQualifier.clear();
        concrete.clear();
        singletons.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        return (T) doGet(BeanKey.of(type));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(BeanKey<T> key) {
        return (T) doGet(key);
    }

    private Object doGet(BeanKey<?> key) {
        String marker = key.type().getName() + (key.qualifier() == null ? "" : "#" + key.qualifier());
        Deque<String> stack = resolving.get();
        if (stack.contains(marker)) {
            List<String> cycle = new ArrayList<>(stack);
            cycle.add(marker);
            throw new CyclicDependencyException("Cyclic dependency: " + String.join(" -> ", cycle));
        }
        stack.push(marker);
        try {
            Provider<?> p = resolveProvider(key.type(), key.qualifier());
            String cacheKey = marker;
            return singletons.computeIfAbsent(cacheKey, k -> {
                Object inst = p.get();
                shutdown.capture(inst.getClass(), inst);
                return inst;
            });
        } finally {
            stack.pop();
        }
    }

    public Object getAny(BeanKey<?> key) {
        return key.qualifier() == null ? get(key.type()) : get((BeanKey) key);
    }

    @Override
    public <T> Optional<T> find(Class<T> type) {
        try {
            return Optional.of(get(type));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<T> find(BeanKey<T> key) {
        try {
            return Optional.of(get(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Object> findAny(BeanKey<?> key) {
        try {
            return Optional.of(getAny(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> contract) {
        List<Object> out = new ArrayList<>();
        for (Class<?> beanClass : concrete) {
            if (contract.isAssignableFrom(beanClass)) {
                out.add(get(beanClass));
            }
        }
        out.sort(Comparator
                .comparingInt(o -> orderOf(o.getClass()))
                .thenComparing(o -> o.getClass().getName()));
        return (List<T>) out;
    }

    @Override
    public <T> Provider<T> provider(Class<T> type) {
        return () -> get(type);
    }

    @Override
    public Optional<String> get(String key) {
        return env.get(key);
    }

    @Override
    public void addPropertySource(PropertySource source) {
        env.addPropertySource(source);
    }

    private void loadIndex() {
        try {
            Class<?> idx = Class.forName("org.neuralcoder.teleflux.di.generated.ComponentIndex");
            components = (String[]) idx.getField("COMPONENTS").get(null);
        } catch (ClassNotFoundException e) {
            components = new String[0];
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to read ComponentIndex", e);
        }
    }

    private void registerComponents() {
        int registered = 0, registeredConfigs = 0;
        for (String fqcn : components) {
            try {
                Class<?> beanClass = Class.forName(fqcn);
                if (!isProfileActive(beanClass)) continue;

                if (beanClass.isAnnotationPresent(Module.class)) continue;

                if (beanClass.isAnnotationPresent(Config.class)) {
                    Provider<?> cfgProv = () -> {
                        Object inst = newInstance(beanClass);
                        bindConfig(inst, beanClass.getAnnotation(Config.class));
                        return inst;
                    };
                    byType.put(beanClass, cfgProv);
                    concrete.add(beanClass);
                    registeredConfigs++;
                    continue;
                }

                Class<?> factoryClass = Class.forName(beanClass.getName() + "_Factory");
                var of = factoryClass.getMethod("of", BeanFactory.class);
                Object factory = of.invoke(null, this);
                if (!(factory instanceof Provider<?> p)) {
                    throw new IllegalStateException("Factory doesn't implement Provider: " + factoryClass.getName());
                }

                byType.put(beanClass, p);
                concrete.add(beanClass);

                String q = qualifierOf(beanClass);
                for (Class<?> itf : beanClass.getInterfaces()) {
                    byType.putIfAbsent(itf, p);
                    if (q != null) byQualifier.computeIfAbsent(itf, k -> new ConcurrentHashMap<>()).putIfAbsent(q, p);
                }
                if (q != null) byQualifier.computeIfAbsent(beanClass, k -> new ConcurrentHashMap<>()).putIfAbsent(q, p);

                registered++;
            } catch (ClassNotFoundException e) {
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to register component: " + fqcn, e);
            }
        }
        System.out.println("[Teleflux DI] Registered bean factories: " + registered + " (configs: " + registeredConfigs + ")");
    }

    private void registerModulesProvides() {
        int modCount = 0, providesCount = 0;
        for (String fqcn : components) {
            try {
                Class<?> cls = Class.forName(fqcn);
                if (!cls.isAnnotationPresent(Module.class)) continue;
                if (!isProfileActive(cls)) continue;

                Object module = newInstance(cls);
                for (Method m : cls.getDeclaredMethods()) {
                    if (!m.isAnnotationPresent(Provides.class)) continue;
                    m.setAccessible(true);

                    Class<?> returnType = m.getReturnType();
                    Provider<?> prov = () -> invokeProvides(module, m);

                    byType.put(returnType, prov);
                    concrete.add(returnType);

                    String q = qualifierOn(m);
                    if (q != null)
                        byQualifier.computeIfAbsent(returnType, k -> new ConcurrentHashMap<>()).putIfAbsent(q, prov);
                    providesCount++;
                }
                modCount++;
            } catch (ClassNotFoundException ignore) {
            }
        }
        if (modCount > 0)
            System.out.println("[Teleflux DI] Modules: " + modCount + ", provided beans: " + providesCount);
    }

    private Provider<?> resolveProvider(Class<?> contract, String qualifier) {
        if (qualifier != null && !qualifier.isBlank()) {
            Map<String, Provider<?>> qmap = byQualifier.get(contract);
            if (qmap == null || !qmap.containsKey(qualifier))
                throw new org.neuralcoder.teleflux.di.api.errors.NoSuchBeanException("No provider for " + contract.getName() + " with qualifier '" + qualifier + "'");
            return qmap.get(qualifier);
        }

        Provider<?> direct = byType.get(contract);
        if (direct != null && concrete.contains(contract)) return direct;

        List<Class<?>> candidates = concrete.stream()
                .filter(contract::isAssignableFrom)
                .collect(Collectors.toCollection(ArrayList::new));

        if (candidates.isEmpty()) {
            Provider<?> p = byType.get(contract);
            if (p == null)
                throw new org.neuralcoder.teleflux.di.api.errors.NoSuchBeanException("No provider for " + contract.getName());
            return p;
        }
        if (candidates.size() == 1) return byType.get(candidates.get(0));

        List<Class<?>> primaries = candidates.stream().filter(this::primaryOf).toList();
        if (primaries.size() == 1) return byType.get(primaries.get(0));
        if (primaries.size() > 1)
            throw new AmbiguousBeanException("Multiple @Primary for " + contract.getName() + ": " + primaries);

        candidates.sort(Comparator.comparingInt(this::orderOf).thenComparing(Class::getName));
        int best = orderOf(candidates.get(0));
        List<Class<?>> top = candidates.stream().filter(c -> orderOf(c) == best).toList();
        if (top.size() > 1) {
            throw new AmbiguousBeanException("Ambiguous candidates for " + contract.getName() + " with order " + best + ": " + top);
        }
        return byType.get(candidates.get(0));
    }

    private Object invokeProvides(Object module, Method m) {
        try {
            Object[] args = Arrays.stream(m.getParameters()).map(this::resolveProvidesParam).toArray();
            Object result = m.invoke(module, args);
            if (result != null) shutdown.capture(result.getClass(), result);
            return result;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            if (cause instanceof RuntimeException re) throw re;
            throw new IllegalStateException("Error in @Provides method: " + m, cause);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to invoke @Provides: " + m, e);
        }
    }

    private Object resolveProvidesParam(Parameter p) {
        String q = qualifierOn(p);
        Class<?> raw = rawTypeOf(p.getParameterizedType());

        if (Provider.class.equals(raw)) {
            Class<?> arg = typeArg(p.getParameterizedType(), 0);
            return (q != null) ? (Provider<?>) () -> get(BeanKey.of(arg, q)) : (Provider<?>) () -> get(arg);
        }
        if (List.class.equals(raw)) {
            Class<?> arg = typeArg(p.getParameterizedType(), 0);
            return getAll(arg);
        }
        if (Set.class.equals(raw)) {
            Class<?> arg = typeArg(p.getParameterizedType(), 0);
            return new HashSet<>(getAll(arg));
        }

        if (p.isAnnotationPresent(Lazy.class)) {
            return (q != null) ? lazy(BeanKey.of(raw, q)) : lazy(raw);
        }
        return (q != null) ? get(BeanKey.of(raw, q)) : get(raw);
    }

    private static Class<?> rawTypeOf(Type t) {
        if (t instanceof Class<?> c) return c;
        if (t instanceof ParameterizedType pt) return (Class<?>) pt.getRawType();
        throw new IllegalArgumentException("Unsupported type: " + t);
    }

    private static Class<?> typeArg(Type t, int idx) {
        if (!(t instanceof ParameterizedType pt)) throw new IllegalArgumentException("Not parameterized: " + t);
        Type a = pt.getActualTypeArguments()[idx];
        if (a instanceof Class<?> c) return c;
        if (a instanceof ParameterizedType p) return (Class<?>) p.getRawType();
        throw new IllegalArgumentException("Unsupported type arg: " + a);
    }

    private static String qualifierOf(AnnotatedElement e) {
        Service s = e.getAnnotation(Service.class);
        if (s != null && !s.value().isBlank()) return s.value();
        Component c = e.getAnnotation(Component.class);
        if (c != null && !c.value().isBlank()) return c.value();
        return null;
    }

    private boolean primaryOf(Class<?> cls) {
        return cls.isAnnotationPresent(Primary.class);
    }

    private boolean eagerOf(Class<?> cls) {
        return cls.isAnnotationPresent(Eager.class);
    }

    private int orderOf(Class<?> cls) {
        Order o = cls.getAnnotation(Order.class);
        return (o != null) ? o.value() : Integer.MAX_VALUE;
    }

    private boolean isProfileActive(Class<?> cls) {
        Profile p = cls.getAnnotation(Profile.class);
        if (p == null) return true;
        if (activeProfiles.isEmpty()) return false;
        for (String req : p.value()) if (activeProfiles.contains(req)) return true;
        return false;
    }

    private static String qualifierOn(AnnotatedElement element) {
        for (Annotation ann : element.getAnnotations()) {
            if (ann.annotationType().isAnnotationPresent(Qualifier.class)) {
                String v = readValue(ann);
                if (v != null && !v.isBlank()) return v;
            }
            for (Annotation metaAnn : ann.annotationType().getAnnotations()) {
                if (metaAnn.annotationType().isAnnotationPresent(Qualifier.class)) {
                    String v = readValue(metaAnn);
                    if (v != null && !v.isBlank()) return v;
                }
            }
        }
        return null;
    }

    private static String readValue(Annotation a) {
        try {
            Method m = a.annotationType().getDeclaredMethod("value");
            Object v = m.invoke(a);
            return v == null ? null : v.toString();
        } catch (Exception ignore) {
            return null;
        }
    }

    private void bindConfig(Object instance, Config cfg) {
        String prefix = cfg.prefix();
        Class<?> cls = instance.getClass();
        for (Field f : cls.getDeclaredFields()) {
            ConfigProperty cp = f.getAnnotation(ConfigProperty.class);
            if (cp == null) continue;
            String key = cp.value();
            if (key == null || key.isBlank())
                key = (prefix == null || prefix.isBlank()) ? f.getName() : prefix + "." + f.getName();
            String def = cp.defaultValue();
            Optional<String> v = get(key);
            f.setAccessible(true);
            try {
                if (f.getType().equals(String.class)) f.set(instance, v.orElse(def));
                else if (f.getType().equals(int.class) || f.getType().equals(Integer.class))
                    f.set(instance, Integer.parseInt(v.orElse(def)));
                else if (f.getType().equals(long.class) || f.getType().equals(Long.class))
                    f.set(instance, Long.parseLong(v.orElse(def)));
                else if (f.getType().equals(boolean.class) || f.getType().equals(Boolean.class))
                    f.set(instance, Boolean.parseBoolean(v.orElse(def)));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to bind config field: " + cls.getName() + "." + f.getName(), e);
            }
        }
    }

    private void eagerPreinstantiate() {
        for (Class<?> cls : concrete) {
            if (eagerOf(cls) && isProfileActive(cls)) get(cls);
        }
    }

    private static Object newInstance(Class<?> cls) {
        try {
            Constructor<?> c = cls.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot instantiate " + cls.getName(), e);
        }
    }
}
