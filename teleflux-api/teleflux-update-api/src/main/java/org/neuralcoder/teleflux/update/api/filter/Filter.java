package org.neuralcoder.teleflux.update.api.filter;

import org.neuralcoder.teleflux.update.api.Event;
import org.neuralcoder.teleflux.update.api.context.EventContext;

/** Предикат фильтрации событий/апдейтов. */
@FunctionalInterface
public interface Filter {
    boolean test(EventContext ctx, Event event);
}
