package org.neuralcoder.teleflux.di.example.qualifiers;

import org.neuralcoder.teleflux.di.api.annotations.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier("channel")
@Retention(RUNTIME)
@Target({TYPE, FIELD, PARAMETER, METHOD})
public @interface Channel {
    String value();
}

