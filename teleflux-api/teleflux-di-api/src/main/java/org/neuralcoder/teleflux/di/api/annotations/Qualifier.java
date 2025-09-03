package org.neuralcoder.teleflux.di.api.annotations;

import java.lang.annotation.*;

/** Метa-аннотация для объявления пользовательских квалификаторов. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface Qualifier {
    String value();
}