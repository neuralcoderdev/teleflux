package org.neuralcoder.teleflux.di.api.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface ConfigProperty {
    String value();          // ключ: "teleflux.client.timeout"
    String defaultValue() default "";
    boolean required() default false;
}
