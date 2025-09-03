package org.neuralcoder.teleflux.core.api.session;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Immutable session descriptor. JavaBean-style getters to play well with tools.
 */
public interface Session {
    Optional<Long> getUserId();
    int getDcId();
    long getAuthKeyId();
    Optional<Instant> getExpiresAt();
    Map<String, String> attributes();
}
