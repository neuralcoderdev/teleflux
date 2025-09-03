package org.neuralcoder.teleflux.update.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Класс, содержащий методы-обработчики событий. Автоиндексируется процессором. */
@Retention(RUNTIME)
@Target(TYPE)
public @interface EventListenerComponent {
    /** Человеческое имя для логов/метрик (опционально). */
    String value() default "";
}
