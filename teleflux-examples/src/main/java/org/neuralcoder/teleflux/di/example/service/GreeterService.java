package org.neuralcoder.teleflux.di.example.service;

import org.neuralcoder.teleflux.di.api.BeanFactory;
import org.neuralcoder.teleflux.di.api.BeanKey;
import org.neuralcoder.teleflux.di.api.Provider;
import org.neuralcoder.teleflux.di.api.annotations.Inject;
import org.neuralcoder.teleflux.di.api.annotations.Lazy;
import org.neuralcoder.teleflux.di.api.annotations.Service;
import org.neuralcoder.teleflux.di.example.config.AppConfig;
import org.neuralcoder.teleflux.di.example.qualifiers.PrimaryChannel;

import java.util.function.Function;

@Service
public class GreeterService {

    private final Provider<TimeService> timeProvider;
    private final Notifier primaryNotifier;

    @Inject
    AppConfig config;
    @Inject BeanFactory beans;

    @Inject
    public GreeterService(Provider<TimeService> timeProvider,
                          @Lazy @PrimaryChannel Notifier notifier) {
        this.timeProvider = timeProvider;
        this.primaryNotifier = notifier;
    }

    public String greetDecorated(String name) {
        String ts = TimeService.fmt(timeProvider.get().now());
        String banner = beans.get(BeanKey.of(String.class, "banner"));

        @SuppressWarnings("unchecked")
        Function<String, String> decorator = beans.get(Function.class);
        return banner + " " + decorator.apply(name) + " @ " + ts;
    }

    public String greetPlain(String name) {
        String ts = TimeService.fmt(timeProvider.get().now());
        Greeter plain = beans.get(BeanKey.of(Greeter.class, "plain"));
        return primaryNotifier.notify(config.greetingPrefix() + " (plain) @ " + ts + " | " + plain.greet(name));
    }
}
