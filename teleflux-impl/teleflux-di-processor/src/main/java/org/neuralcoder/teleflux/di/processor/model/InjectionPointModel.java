package org.neuralcoder.teleflux.di.processor.model;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

/** Injection point abstraction for fields and constructors. */
public final class InjectionPointModel {
    public enum Kind { FIELD, CONSTRUCTOR }
    private final Kind kind;
    private final VariableElement field;
    private final ExecutableElement constructor;

    private InjectionPointModel(Kind kind, VariableElement field, ExecutableElement constructor) {
        this.kind = kind; this.field = field; this.constructor = constructor;
    }

    public static InjectionPointModel forField(VariableElement field) {
        return new InjectionPointModel(Kind.FIELD, field, null);
    }
    public static InjectionPointModel forConstructor(ExecutableElement ctor) {
        return new InjectionPointModel(Kind.CONSTRUCTOR, null, ctor);
    }

    public Kind kind() { return kind; }
    public VariableElement field() { return field; }
    public ExecutableElement constructor() { return constructor; }
}
