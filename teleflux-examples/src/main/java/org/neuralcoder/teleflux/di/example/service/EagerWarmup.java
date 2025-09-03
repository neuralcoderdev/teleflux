package org.neuralcoder.teleflux.di.example.service;

import org.neuralcoder.teleflux.di.api.Lifecycle;
import org.neuralcoder.teleflux.di.api.annotations.Eager;
import org.neuralcoder.teleflux.di.api.annotations.PostConstruct;
import org.neuralcoder.teleflux.di.api.annotations.Service;

@Service
@Eager
public class EagerWarmup implements Lifecycle {

    @PostConstruct
    void init() {
        System.out.println("[EagerWarmup] cache preloaded");
    }

    @Override
    public void stop() {
        System.out.println("[EagerWarmup] lifecycle stop()");
    }
}
