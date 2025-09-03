package org.neuralcoder.teleflux.core.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Помечает контракт запроса как Telegram-операцию. */
@Retention(RUNTIME)
@Target(TYPE)
public @interface TelegramRequest {
    /** Логическое имя/операция протокола (напр. "messages.sendMessage"). */
    String method();

    /** Идемпотентен ли запрос (для дедуп/ретраев). */
    boolean idempotent() default true;
}
