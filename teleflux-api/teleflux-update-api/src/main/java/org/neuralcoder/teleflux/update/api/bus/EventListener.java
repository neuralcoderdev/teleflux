package org.neuralcoder.teleflux.update.api.bus;

import org.neuralcoder.teleflux.update.api.Event;
import org.neuralcoder.teleflux.update.api.context.EventContext;

import java.util.concurrent.CompletionStage;

/** Подписчик на события/апдейты. */
@FunctionalInterface
public interface EventListener<T extends Event> {
    CompletionStage<Void> onEvent(EventContext ctx, T event);
}