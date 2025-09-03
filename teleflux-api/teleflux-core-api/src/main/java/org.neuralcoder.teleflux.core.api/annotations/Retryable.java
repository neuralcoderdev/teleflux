package org.neuralcoder.teleflux.core.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Локальная политика ретраев для запроса. */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Retryable {

    int maxAttempts() default 3;

    /** Базовая задержка (в миллисекундах). */
    long baseMs() default 200;

    /** Тип backoff. */
    Backoff backoff() default Backoff.EXPONENTIAL;

    /** Добавлять ли джиттер. */
    boolean jitter() default true;

    /** Классы ошибок, которые считаем ретраебельными. */
    Class<? extends Throwable>[] retryOn() default {};

    enum Backoff { FIXED, EXPONENTIAL, EXPONENTIAL_FULL_JITTER }
}