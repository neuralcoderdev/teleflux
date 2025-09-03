package org.neuralcoder.teleflux.update.api.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Степень параллелизма воркеров на подписку. */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Concurrency {
    int value() default 1;
}
