package org.neuralcoder.teleflux.core.api.policy.ratelimit;

import lombok.NonNull;

/**
 * Classification key for rate limiting buckets.
 */
public record PermitKey(@NonNull String value) {
    public static PermitKey of(String value) { return new PermitKey(value); }
}
