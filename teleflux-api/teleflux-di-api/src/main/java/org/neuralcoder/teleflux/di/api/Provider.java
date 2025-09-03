package org.neuralcoder.teleflux.di.api;

@FunctionalInterface
public interface Provider<T> {
    T get();
}
