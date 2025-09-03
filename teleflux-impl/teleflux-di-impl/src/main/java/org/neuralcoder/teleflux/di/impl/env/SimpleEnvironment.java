package org.neuralcoder.teleflux.di.impl.env;

import org.neuralcoder.teleflux.di.api.Environment;
import org.neuralcoder.teleflux.di.api.PropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SimpleEnvironment implements Environment {
    private final List<PropertySource> sources = new ArrayList<>();

    @Override
    public Optional<String> get(String key) {
        return sources.stream()
                .sorted((a, b) -> Integer.compare(b.order(), a.order()))
                .map(ps -> ps.get(key))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    @Override
    public void addPropertySource(PropertySource source) {
        sources.add(source);
    }
}