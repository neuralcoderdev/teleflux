package org.neuralcoder.teleflux.di.example.service;

import org.neuralcoder.teleflux.di.api.Lifecycle;
import org.neuralcoder.teleflux.di.api.annotations.Inject;
import org.neuralcoder.teleflux.di.api.annotations.PostConstruct;
import org.neuralcoder.teleflux.di.api.annotations.PreDestroy;
import org.neuralcoder.teleflux.di.api.annotations.Service;

/** Demo service showing constructor @Inject and lifecycle hooks. */
@Service
public class AppService implements Lifecycle {

    private final GreetingService greeter;

    @Inject
    public AppService(GreetingService greeter) {
        this.greeter = greeter;
    }

    @PostConstruct
    void init() {
        System.out.println("[AppService] postConstruct invoked");
    }

    @PreDestroy
    void cleanup() {
        System.out.println("[AppService] preDestroy invoked");
    }

    @Override
    public void stop() {
        System.out.println("[AppService] lifecycle stop()");
    }

    public void runDemo() {
        System.out.println(greeter.greet("Teleflux User"));
    }
}
