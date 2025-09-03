package org.neuralcoder.teleflux.core.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Декларативный таймаут запроса. Приоритетнее дефолта из options. */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Timeout {
    long value();
    ChronoUnit unit() default ChronoUnit.SECONDS;
}
