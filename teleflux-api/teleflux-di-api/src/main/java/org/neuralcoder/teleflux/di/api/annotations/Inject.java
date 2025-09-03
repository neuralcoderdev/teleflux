package org.neuralcoder.teleflux.di.api.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Inject {
    /** Помечает зависимость как необязательную (Optional). */
    boolean optional() default false;
}
