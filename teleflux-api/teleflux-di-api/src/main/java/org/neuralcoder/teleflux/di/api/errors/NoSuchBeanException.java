package org.neuralcoder.teleflux.di.api.errors;

public class NoSuchBeanException extends InjectionException {
    public NoSuchBeanException(String message) { super(message); }
}
