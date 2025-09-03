package org.neuralcoder.teleflux.di.api.annotations;

import org.neuralcoder.teleflux.di.api.Scope;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Provides {
    String qualifier() default "";
    Scope scope() default Scope.SINGLETON;
}