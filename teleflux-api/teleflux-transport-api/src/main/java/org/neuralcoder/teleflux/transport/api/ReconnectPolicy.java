package org.neuralcoder.teleflux.transport.api;

import java.time.Duration;

/**
 * Strategy interface for reconnect behavior after disconnects or failures.
 * Implementations should be pure and side-effect-free.
 */
public interface ReconnectPolicy {

    /** Whether a reconnect attempt should be made for the given failure count. */
    boolean shouldReconnect(int attempt);

    /** Delay before the next reconnect attempt. */
    Duration delay(int attempt);

    /** Maximum number of attempts; Integer.MAX_VALUE for unbounded. */
    int maxAttempts();
}
