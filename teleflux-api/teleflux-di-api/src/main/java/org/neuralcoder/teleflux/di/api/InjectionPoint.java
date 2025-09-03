package org.neuralcoder.teleflux.di.api;

import java.lang.reflect.Type;
import java.util.Optional;

/** Точка внедрения (поле/параметр/конструктор). */
public interface InjectionPoint {
    enum Kind { FIELD, PARAMETER, CONSTRUCTOR, METHOD }
    Kind kind();
    Type type();
    Optional<String> qualifier();
    boolean optional();
}
