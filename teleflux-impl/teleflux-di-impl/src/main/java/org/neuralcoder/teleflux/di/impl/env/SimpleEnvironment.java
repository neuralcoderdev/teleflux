package org.neuralcoder.teleflux.di.impl.env;

import org.neuralcoder.teleflux.di.api.Environment;
import org.neuralcoder.teleflux.di.api.PropertySource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Minimal Environment implementation for config injection (future use).
 */
public final class SimpleEnvironment implements Environment {
    private final List<PropertySource> sources = new ArrayList<>();

    @Override
    public Optional<String> get(String key) {
        return sources.stream()
                .sorted(Comparator.comparingInt(PropertySource::order))
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