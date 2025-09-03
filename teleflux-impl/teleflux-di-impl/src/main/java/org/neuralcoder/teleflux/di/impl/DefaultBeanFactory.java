package org.neuralcoder.teleflux.di.impl;

import org.neuralcoder.teleflux.di.api.*;
import org.neuralcoder.teleflux.di.api.annotations.*;

import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Default Teleflux BeanFactory implementation.
 * It bootstraps from the generated ComponentIndex and generated *_Factory classes.
 *
 * Design goals:
 * - Zero reflection for member access (factories do field injections directly).
 * - Minimal reflection used only to load generated classes by name.
 * - Respect @Singleton / @Prototype scopes and lifecycle hooks.
 */
public final class DefaultBeanFactory implements BeanFactory {

    private final Map<Class<?>, Supplier<?>> providers = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    private final Map<Class<?>, Scope> scopes = new ConcurrentHashMap<>();
    private final ShutdownManager shutdown = new ShutdownManager();
    private volatile boolean started = false;

    public DefaultBeanFactory() {}

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        Supplier<?> prov = providers.get(type);
        if (prov == null) {
            throw new org.neuralcoder.teleflux.di.api.errors.NoSuchBeanException(
                    "No provider registered for type: " + type.getName());
        }
        Scope scope = scopes.getOrDefault(type, Scope.SINGLETON);
        if (scope == Scope.SINGLETON) {
            return (T) singletons.computeIfAbsent(type, k -> createScoped(prov, type));
        }
        // PROTOTYPE or others
        return (T) prov.get();
    }

    @Override
    public <T> T get(BeanKey<T> key) {
        // Qualifiers not yet modelled; basic version uses type only
        return get(key.getType());
    }

    @Override
    public <T> Optional<T> find(Class<T> type) {
        try { return Optional.of(get(type)); } catch (Exception e) { return Optional.empty(); }
    }

    @Override
    public <T> Optional<T> find(BeanKey<T> key) {
        try { return Optional.of(get(key)); } catch (Exception e) { return Optional.empty(); }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> type) {
        List<T> out = new ArrayList<>();
        // naive: include exact match
        if (providers.containsKey(type)) out.add((T) get(type));
        // include subtypes registered directly
        providers.keySet().stream()
                .filter(k -> type.isAssignableFrom(k) && k != type)
                .forEach(k -> out.add((T) get((Class<?>) k)));
        // Ordering by class name to be deterministic (same as ResolutionRules)
        out.sort(Comparator.comparing(o -> o.getClass().getName()));
        return out;
    }

    @Override
    public <T> Provider<T> provider(Class<T> type) {
        return () -> get(type);
    }

    @Override
    public void start() {
        if (started) return;
        started = true;
        // Initialize from generated index
        registerFromGeneratedIndex();
    }

    @Override
    public void stop() {
        if (!started) return;
        started = false;
        shutdown.runPreDestroy();
        singletons.clear();
        providers.clear();
        scopes.clear();
    }

    /* ---------- bootstrap ---------- */

    @SuppressWarnings("unchecked")
    private void registerFromGeneratedIndex() {
        String idx = "org.neuralcoder.teleflux.di.generated.ComponentIndex";
        String[] components;
        try {
            Class<?> indexClass = Class.forName(idx);
            components = (String[]) indexClass.getField("COMPONENTS").get(null);
        } catch (ClassNotFoundException e) {
            // No generated components — that's fine for minimal demos.
            components = new String[0];
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to read ComponentIndex", e);
        }

        for (String fqcn : components) {
            try {
                Class<?> beanClass = Class.forName(fqcn);
                Class<?> factoryClass = Class.forName(beanClass.getName() + "_Factory");
                var of = factoryClass.getMethod("of", BeanFactory.class);
                Object factoryInstance = of.invoke(null, this);

                // Provider is the factory itself (implements Provider<T>)
                providers.put(beanClass, (Supplier<?>) factoryInstance);

                // Register contracts (interfaces) for lookups
                for (Class<?> itf : beanClass.getInterfaces()) {
                    providers.putIfAbsent(itf, (Supplier<?>) factoryInstance);
                    scopes.putIfAbsent(itf, scopeOf(beanClass));
                }

                scopes.putIfAbsent(beanClass, scopeOf(beanClass));

                // Register shutdown hooks
                shutdown.maybeRegisterLifecycle(beanClass, factoryInstance);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Missing generated factory for " + fqcn, e);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to instantiate factory for " + fqcn, e);
            }
        }
    }

    private Object createScoped(Supplier<?> prov, Class<?> type) {
        Object instance = prov.get();
        // Track @PreDestroy/Lifecycle for singletons
        shutdown.capture(type, instance);
        return instance;
    }

    private Scope scopeOf(AnnotatedElement element) {
        if (element.isAnnotationPresent(Singleton.class)) return Scope.SINGLETON;
        // default scope
        return Scope.SINGLETON;
    }
}
