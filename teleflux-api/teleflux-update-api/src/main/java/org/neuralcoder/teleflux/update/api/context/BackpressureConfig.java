package org.neuralcoder.teleflux.update.api.context;

import lombok.Builder;
import lombok.Value;

/** Конфигурация backpressure для подписки. */
@Value
@Builder
public class BackpressureConfig {
    BackpressureMode mode;
    int bufferCapacity;     // используется, если mode == BUFFER
    long timeoutMillis;     // используется, если mode == TIMEOUT

    public static BackpressureConfig of(BackpressureMode mode) {
        return BackpressureConfig.builder().mode(mode).build();
    }
}