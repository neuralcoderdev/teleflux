package org.neuralcoder.teleflux.update.api.context;

public enum BackpressureMode {
    DROP,          // отвергать новые
    DROP_OLDEST,   // вытеснять старые
    BLOCK,         // блокировать паблишер
    TIMEOUT,       // ждать ограниченное время
    BUFFER         // буфер фиксированного размера
}