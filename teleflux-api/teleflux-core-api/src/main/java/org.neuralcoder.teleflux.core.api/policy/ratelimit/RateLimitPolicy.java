package org.neuralcoder.teleflux.core.api.policy.ratelimit;

import org.neuralcoder.teleflux.core.api.requests.Request;

public interface RateLimitPolicy {
    Permit acquire(PermitKey key);
    PermitKey classify(Request<?> request);

    interface Permit {
        void release();
        long acquiredAtNanos();
    }
}
