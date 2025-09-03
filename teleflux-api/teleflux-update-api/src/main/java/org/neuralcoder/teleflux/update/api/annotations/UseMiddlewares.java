package org.neuralcoder.teleflux.update.api.annotations;

import org.neuralcoder.teleflux.update.api.middleware.Middleware;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Подключение локальных middleware к конкретной подписке/классу. */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface UseMiddlewares {
    Class<? extends Middleware>[] value();
}
