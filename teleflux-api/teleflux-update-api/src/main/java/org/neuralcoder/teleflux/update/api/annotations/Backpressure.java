package org.neuralcoder.teleflux.update.api.annotations;

import org.neuralcoder.teleflux.update.api.context.BackpressureMode;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Политика backpressure для подписки/обработчика. */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Backpressure {
    BackpressureMode mode() default BackpressureMode.BUFFER;
    int buffer() default 1000;     // при mode=BUFFER
    long timeoutMs() default 0L;   // при mode=TIMEOUT
}
