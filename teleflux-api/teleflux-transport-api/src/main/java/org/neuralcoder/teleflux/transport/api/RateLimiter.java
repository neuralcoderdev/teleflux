package org.neuralcoder.teleflux.transport.api;

public interface RateLimiter {
    Permit acquire(String key);

    interface Permit {
        void release();
    }
}
