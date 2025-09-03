package org.neuralcoder.teleflux.di.example.service;

import org.neuralcoder.teleflux.di.api.annotations.Component;
import org.neuralcoder.teleflux.di.api.annotations.Order;
import org.neuralcoder.teleflux.di.api.annotations.Primary;

@Component("fancy")
@Order(10)
@Primary
public class FancyGreeter implements Greeter {
    @Override public String greet(String name) { return "✨ Hello, " + name + " ✨"; }
}
