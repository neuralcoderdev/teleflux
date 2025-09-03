package org.neuralcoder.teleflux.core.api.session;

import java.util.Optional;

public interface SessionStore {
    Optional<SessionData> load();

    void save(SessionData data);

    void clear();
}