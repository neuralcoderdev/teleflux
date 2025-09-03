package org.neuralcoder.teleflux.di.impl.env;

import org.neuralcoder.teleflux.di.api.PropertySource;

import java.util.Map;
import java.util.Optional;

public record MapPropertySource(String name, Map<String, String> map, int order) implements PropertySource {
    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(map.get(key));
    }
}
