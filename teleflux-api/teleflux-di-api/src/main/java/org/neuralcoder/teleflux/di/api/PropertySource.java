package org.neuralcoder.teleflux.di.api;


import java.util.Optional;

/** Источник свойств (env vars, system props, map, файл и т.д.). */
public interface PropertySource {
    String name();
    Optional<String> get(String key);
    int order(); // порядок разрешения между источниками (меньше — раньше)
}