package org.neuralcoder.teleflux.core.api.session;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Value DTO implementing Session via Lombok-generated getters.
 */
@Value
@Builder
public class SessionData implements Session {
    Long userId;
    int dcId;
    long authKeyId;
    Instant expiresAt;
    @Singular
    Map<String, String> attributes;

    @Override
    public Map<String, String> attributes() {
        return attributes;
    }

    @Override
    public Optional<Long> getUserId() {
        return Optional.ofNullable(userId);
    }

    @Override
    public int getDcId() {
        return dcId;
    }

    @Override
    public long getAuthKeyId() {
        return authKeyId;
    }

    @Override
    public Optional<Instant> getExpiresAt() {
        return Optional.ofNullable(expiresAt);
    }
}
