package org.neuralcoder.teleflux.di.api.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Profile {
    String[] value(); // какие профили должны быть активны
}