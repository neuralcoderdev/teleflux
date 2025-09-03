package org.neuralcoder.teleflux.di.example.config;

import org.neuralcoder.teleflux.di.api.annotations.Config;
import org.neuralcoder.teleflux.di.api.annotations.ConfigProperty;

@Config(prefix = "teleflux.example")
public class AppConfig {

    @ConfigProperty(value = "teleflux.example.greetingPrefix", defaultValue = "Hi")
    String greetingPrefix;

    @ConfigProperty(value = "teleflux.example.maxRetries", defaultValue = "3")
    int maxRetries;

    public String greetingPrefix() { return greetingPrefix; }
    public int maxRetries() { return maxRetries; }
}
