package org.neuralcoder.teleflux.update.api.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Локальная политика ретраев для обработчика. */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Retry {
    int maxAttempts() default 3;
    long baseMs() default 200;
    boolean jitter() default true;
    Class<? extends Throwable>[] retryOn() default {};
}
