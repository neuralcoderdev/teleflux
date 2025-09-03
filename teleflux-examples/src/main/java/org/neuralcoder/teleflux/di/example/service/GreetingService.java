package org.neuralcoder.teleflux.di.example.service;

import org.neuralcoder.teleflux.di.api.annotations.Inject;
import org.neuralcoder.teleflux.di.api.annotations.Service;

/** Builds a greeting message using TimeService. */
@Service
public class GreetingService {

    @Inject
    TimeService time; // field injection (non-private, non-final)

    public String greet(String name) {
        return "[TelefluxDI] Hello, " + name + "! It is " + time.now() + ".";
    }
}
