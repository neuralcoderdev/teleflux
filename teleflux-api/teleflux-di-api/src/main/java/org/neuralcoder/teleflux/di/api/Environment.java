package org.neuralcoder.teleflux.di.api;


import java.util.Optional;

/** Абстракция окружения/конфигурации. */
public interface Environment {
    Optional<String> get(String key);
    default String getOrDefault(String key, String def) { return get(key).orElse(def); }
    void addPropertySource(PropertySource source); // изменяемая часть — в impl
}
