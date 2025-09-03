package org.neuralcoder.teleflux.update.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Приоритет подписки (больше — раньше). */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Priority {
    int value() default 0;
}
