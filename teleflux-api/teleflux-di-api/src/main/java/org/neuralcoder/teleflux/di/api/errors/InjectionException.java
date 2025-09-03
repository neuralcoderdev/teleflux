package org.neuralcoder.teleflux.di.api.errors;

public class InjectionException extends RuntimeException {
    public InjectionException() {}
    public InjectionException(String message) { super(message); }
    public InjectionException(String message, Throwable cause) { super(message, cause); }
}