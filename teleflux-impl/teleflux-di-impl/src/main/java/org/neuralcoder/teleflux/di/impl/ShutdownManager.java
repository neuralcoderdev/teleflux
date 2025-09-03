package org.neuralcoder.teleflux.di.impl;

import org.neuralcoder.teleflux.di.api.Lifecycle;
import org.neuralcoder.teleflux.di.api.annotations.PreDestroy;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

final class ShutdownManager {
    private final Map<Object, List<Method>> preDestroy = new ConcurrentHashMap<>();
    private final List<Lifecycle> lifecycles = Collections.synchronizedList(new ArrayList<>());

    void capture(Class<?> declaredType, Object instance) {
        for (Method m : declaredType.getDeclaredMethods()) {
            if (m.isAnnotationPresent(PreDestroy.class)) {
                m.setAccessible(true);
                preDestroy.computeIfAbsent(instance, k -> new ArrayList<>()).add(m);
            }
        }
        if (instance instanceof Lifecycle lc) lifecycles.add(lc);
    }

    void runPreDestroy() {
        preDestroy.forEach((inst, methods) -> methods.forEach(m -> {
            try {
                m.invoke(inst);
            } catch (Exception ignore) {
            }
        }));
        preDestroy.clear();
        lifecycles.forEach(lc -> {
            try {
                lc.stop();
            } catch (Exception ignore) {
            }
        });
        lifecycles.clear();
    }
}
