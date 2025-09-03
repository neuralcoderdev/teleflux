package org.neuralcoder.teleflux.update.api.context;

import org.neuralcoder.teleflux.update.api.Event;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/** Обертка над событием с метаданными конвейера. */
public interface EventContext {
    Event event();
    Instant timestamp();
    Map<String, String> metadata(); // traceId, source, attempt, etc.
    Optional<OrderingKey> orderingKey(); // chatId/userId/custom
}