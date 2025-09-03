package org.neuralcoder.teleflux.update.api.bus;

import lombok.NonNull;

public record SubscriptionId(@NonNull String value) {
    public static SubscriptionId of(String value) {
        return new SubscriptionId(value);
    }
}
