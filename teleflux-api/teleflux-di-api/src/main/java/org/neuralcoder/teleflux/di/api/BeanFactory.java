package org.neuralcoder.teleflux.di.api;

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
}
