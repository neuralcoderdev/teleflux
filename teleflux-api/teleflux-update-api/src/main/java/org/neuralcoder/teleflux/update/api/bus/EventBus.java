package org.neuralcoder.teleflux.update.api.bus;


import org.neuralcoder.teleflux.update.api.Event;

public interface EventBus {

    /**
     * Регистрирует подписку и слушателя.
     * Реализация должна валидировать, что указан либо eventType, либо topic.
     */
    SubscriptionId subscribe(Subscription subscription, EventListener<? extends Event> listener);

    /** Отписка по id. */
    void unsubscribe(SubscriptionId id);

    /** Публикация события в шину. */
    void publish(Event event);

    /** Текущий глобальный конвейер (read-only). */
    PipelineDescriptor pipeline();
}
