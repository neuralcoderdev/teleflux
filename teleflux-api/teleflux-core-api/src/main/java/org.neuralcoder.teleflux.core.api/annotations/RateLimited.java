package org.neuralcoder.teleflux.core.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Декларирует классификацию и параметры rate limit.
 * key поддерживает шаблоны (например "chat:${chatId}").
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface RateLimited {
    String key();
    int permitsPerSecond() default -1; // -1 = использовать глобальные дефолты
    int burst() default -1;            // -1 = использовать глобальные дефолты
}
