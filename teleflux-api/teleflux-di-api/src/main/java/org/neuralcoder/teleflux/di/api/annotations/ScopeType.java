package org.neuralcoder.teleflux.di.api.annotations;

import java.lang.annotation.*;

/** Пользовательский scope (для расширения базовых). */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ScopeType {
    String value(); // имя scope, будет сопоставлено в impl
}