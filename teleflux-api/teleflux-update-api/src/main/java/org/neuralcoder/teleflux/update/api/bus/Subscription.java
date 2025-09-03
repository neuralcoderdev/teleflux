package org.neuralcoder.teleflux.update.api.bus;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.neuralcoder.teleflux.core.api.policy.retry.RetryPolicy;
import org.neuralcoder.teleflux.update.api.Event;
import org.neuralcoder.teleflux.update.api.UpdateType;
import org.neuralcoder.teleflux.update.api.context.BackpressureConfig;
import org.neuralcoder.teleflux.update.api.context.BackpressureMode;
import org.neuralcoder.teleflux.update.api.context.OrderingMode;
import org.neuralcoder.teleflux.update.api.filter.Filter;
import org.neuralcoder.teleflux.update.api.middleware.Middleware;

import java.util.List;
import java.util.Optional;

/** Описание подписки: критерии, политика обработки и качества сервиса. */
@Value
@Builder
public class Subscription {

    /** Имя подписки для логов/метрик. */
    String name;

    /** Класс события (альтернатива topic). */
    Class<? extends Event> eventType;

    /** Тип обновления Telegram (альтернатива eventType). */
    UpdateType topic;

    /** Фильтры перед обработкой. */
    @Singular("filter")
    List<Filter> filters;

    /** Приоритет доставки (больше — раньше). */
    @Builder.Default int priority = 0;

    /** Параллелизм обработчиков для этой подписки. */
    @Builder.Default int concurrency = 1;

    /** Гарантии порядка. */
    @Builder.Default OrderingMode ordering = OrderingMode.NONE;

    /** Политика backpressure. */
    @Builder.Default BackpressureConfig backpressure = BackpressureConfig
            .of(BackpressureMode.BUFFER);

    /** Политика повторов (at-least-once семантика). */
    RetryPolicy retryPolicy;

    /** Локальные middleware для этой подписки. */
    @Singular("middleware")
    List<Middleware> middleware;

    public Optional<Class<? extends Event>> eventType() { return Optional.ofNullable(eventType); }
    public Optional<UpdateType> topic() { return Optional.ofNullable(topic); }
}
