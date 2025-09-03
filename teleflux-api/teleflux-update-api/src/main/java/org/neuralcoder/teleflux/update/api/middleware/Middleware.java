package org.neuralcoder.teleflux.update.api.middleware;

import org.neuralcoder.teleflux.update.api.context.EventContext;

import java.util.concurrent.CompletionStage;

/** Стадия конвейера обработки событий. */
@FunctionalInterface
public interface Middleware {
    CompletionStage<Void> apply(EventContext ctx, Next next);
}