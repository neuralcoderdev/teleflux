package org.neuralcoder.teleflux.di.impl;

import org.neuralcoder.teleflux.di.api.Lifecycle;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Collects @PreDestroy and Lifecycle.stop() for graceful shutdown.
 */
final class ShutdownManager {
    private final Map<Object, List<Method>> preDestroy = new ConcurrentHashMap<>();
    private final List<Lifecycle> lifecycles = Collections.synchronizedList(new ArrayList<>());

    void capture(Class<?> type, Object instance) {
        for (Method m : type.getDeclaredMethods()) {
            if (m.isAnnotationPresent(org.neuralcoder.teleflux.di.api.annotations.PreDestroy.class)) {
                m.setAccessible(true);
                preDestroy.computeIfAbsent(instance, k -> new ArrayList<>()).add(m);
            }
        }
        if (instance instanceof Lifecycle lc) {
            lifecycles.add(lc);
        }
    }

    void maybeRegisterLifecycle(Class<?> beanClass, Object factoryInstance) {
        // Factories may not expose lifecycle; actual instances are captured in capture()
    }

    void runPreDestroy() {
        preDestroy.forEach((inst, methods) -> methods.forEach(m -> {
            try { m.invoke(inst); } catch (Exception ignore) {}
        }));
        lifecycles.forEach(lc -> {
            try { lc.stop(); } catch (Exception ignore) {}
        });
        preDestroy.clear();
        lifecycles.clear();
    }
}