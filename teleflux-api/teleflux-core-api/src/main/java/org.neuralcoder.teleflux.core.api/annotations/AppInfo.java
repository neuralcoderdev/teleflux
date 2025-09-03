package org.neuralcoder.teleflux.core.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Конфигурация приложения (apiId/apiHash и пр.) на уровне класса-конфига. */
@Retention(RUNTIME)
@Target(TYPE)
public @interface AppInfo {
    int apiId();
    String apiHash();
    String device() default "Teleflux";
    String systemLangCode() default "en";
    String appVersion() default "0.1.0";
}
