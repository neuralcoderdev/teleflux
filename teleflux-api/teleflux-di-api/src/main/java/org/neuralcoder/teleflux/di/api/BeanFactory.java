package org.neuralcoder.teleflux.di.api;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;

/**
 * Минимальный контракт DI-контейнера.
 */
public interface BeanFactory {
    <T> T get(Class<T> type);
    <T> T get(BeanKey<T> key);

    /** Convenience for qualified lookups without BeanKey ceremony. */
    default <T> T getQualified(Class<T> type, String qualifier) {
        return get(BeanKey.of(type, qualifier));
    }

    <T> Optional<T> find(Class<T> type);
    <T> Optional<T> find(BeanKey<T> key);

    <T> List<T> getAll(Class<T> type);
    <T> Provider<T> provider(Class<T> type);

    void start();
    void stop();

    @SuppressWarnings("unchecked")
    default <T> T lazy(Class<T> type) {
        Provider<T> p = provider(type);
        if (!type.isInterface()) {
            throw new IllegalStateException("@Lazy can proxy only interfaces: " + type.getName());
        }
        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{ type },
                (proxy, method, args) -> method.invoke(p.get(), args)
        );
    }

    @SuppressWarnings("unchecked")
    default <T> T lazy(BeanKey<T> key) {
        if (!key.type().isInterface()) {
            throw new IllegalStateException("@Lazy can proxy only interfaces: " + key.type().getName());
        }
        Provider<T> p = () -> get(key);
        return (T) Proxy.newProxyInstance(
                key.type().getClassLoader(),
                new Class<?>[]{ key.type() },
                (proxy, method, args) -> method.invoke(p.get(), args)
        );
    }
}
