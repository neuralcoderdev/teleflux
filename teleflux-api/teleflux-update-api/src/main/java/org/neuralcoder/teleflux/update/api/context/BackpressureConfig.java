package org.neuralcoder.teleflux.update.api.context;

public record BackpressureConfig(BackpressureMode mode, int bufferCapacity, long timeoutMillis) {

    public static BackpressureConfig of(BackpressureMode mode) {
        return new BackpressureConfig(mode, 0, 0);
    }
}