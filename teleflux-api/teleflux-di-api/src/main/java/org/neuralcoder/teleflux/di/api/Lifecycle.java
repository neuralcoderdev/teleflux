package org.neuralcoder.teleflux.di.api;

/** Управляемый жизненный цикл бина (опционально реализуется самим бином). */
public interface Lifecycle {
    default void start() {}
    default void stop() {}
}
