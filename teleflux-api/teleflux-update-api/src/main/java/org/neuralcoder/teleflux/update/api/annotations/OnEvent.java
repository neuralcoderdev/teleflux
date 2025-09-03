package org.neuralcoder.teleflux.update.api.annotations;

import org.neuralcoder.teleflux.update.api.Event;
import org.neuralcoder.teleflux.update.api.UpdateType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Помечает метод как обработчик событий.
 * Должен быть указан либо event(), либо topic().
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface OnEvent {
    Class<? extends Event>[] event() default {};
    UpdateType[] topic() default {};
}
