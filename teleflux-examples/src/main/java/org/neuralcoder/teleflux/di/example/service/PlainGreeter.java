package org.neuralcoder.teleflux.di.example.service;

import org.neuralcoder.teleflux.di.api.annotations.Component;
import org.neuralcoder.teleflux.di.api.annotations.Order;

@Component("plain")
@Order(20)
public class PlainGreeter implements Greeter {
    @Override public String greet(String name) { return "Hello, " + name + "!"; }
}
