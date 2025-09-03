package org.neuralcoder.teleflux.di.api.errors;


public class CyclicDependencyException extends InjectionException {
    public CyclicDependencyException(String message) { super(message); }
}
