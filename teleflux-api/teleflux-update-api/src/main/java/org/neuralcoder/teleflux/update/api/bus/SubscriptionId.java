package org.neuralcoder.teleflux.update.api.bus;

import lombok.NonNull;
import lombok.Value;

/**
 * Идентификатор подписки.
 */
@Value
public class SubscriptionId {
    @NonNull
    String value;

    public static SubscriptionId of(String value) {
        return new SubscriptionId(value);
    }
}
