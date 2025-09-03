package org.neuralcoder.teleflux.core.api.types;

import lombok.NonNull;

/**
 * Strongly-typed request identifier.
 */
public record RequestId(@NonNull String value) {
    public static RequestId of(String value) { return new RequestId(value); }
}
