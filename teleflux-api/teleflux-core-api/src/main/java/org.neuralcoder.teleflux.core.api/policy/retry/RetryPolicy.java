package org.neuralcoder.teleflux.core.api.policy.retry;

import org.neuralcoder.teleflux.core.api.requests.Request;

import java.time.Duration;

public interface RetryPolicy {
    boolean shouldRetry(Throwable error, Attempt attempt, Request<?> request);
    Duration backoff(Attempt attempt, Throwable error, Request<?> request);

    interface Attempt {
        int number(); // 1..N
    }
}
