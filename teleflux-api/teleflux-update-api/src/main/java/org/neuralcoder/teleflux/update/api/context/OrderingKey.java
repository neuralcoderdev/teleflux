package org.neuralcoder.teleflux.update.api.context;

import lombok.NonNull;
import lombok.Value;

/** Ключ упорядочивания (пер-чат/пер-юзер/кастом). */
@Value
public class OrderingKey {
    @NonNull OrderingMode mode;
    @NonNull String value; // строковое представление chatId/userId/и т.п.
}