package org.neuralcoder.teleflux.update.api.annotations;

import org.neuralcoder.teleflux.update.api.context.OrderingMode;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Гарантии порядка обработки и ключ упорядочивания. */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Ordering {
    OrderingMode value();
    /** Шаблон ключа (например "${event.chatId}"). Пусто — авто по режиму. */
    String key() default "";
}
