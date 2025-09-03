package org.neuralcoder.teleflux.di.example.service;

import org.neuralcoder.teleflux.di.api.BeanFactory;
import org.neuralcoder.teleflux.di.api.annotations.Inject;
import org.neuralcoder.teleflux.di.api.annotations.PostConstruct;
import org.neuralcoder.teleflux.di.api.annotations.PreDestroy;
import org.neuralcoder.teleflux.di.api.annotations.Service;

@Service
public class AppService {

    private final GreeterService greeter;
    @Inject BeanFactory beans;

    @Inject
    public AppService(GreeterService greeter) {
        this.greeter = greeter;
    }

    @PostConstruct
    void init() { System.out.println("[AppService] @PostConstruct"); }

    @PreDestroy
    void cleanup() { System.out.println("[AppService] @PreDestroy"); }

    public void run() {
        System.out.println(greeter.greetDecorated("Teleflux User"));
        System.out.println(greeter.greetPlain("Teleflux User"));
    }
}