package org.neuralcoder.teleflux.update.api.middleware;


import org.neuralcoder.teleflux.update.api.context.EventContext;

import java.util.concurrent.CompletionStage;

/** Ссылка на следующий элемент цепочки. */
public interface Next {
    CompletionStage<Void> proceed(EventContext ctx);
}