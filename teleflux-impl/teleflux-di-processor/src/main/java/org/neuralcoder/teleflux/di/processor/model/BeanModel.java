package org.neuralcoder.teleflux.di.processor.model;

import org.neuralcoder.teleflux.di.processor.scan.ElementUtilsEx;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

/** In-memory representation of a bean to be generated. */
public final class BeanModel {
    public final TypeElement type;
    public final String packageName;
    public final String simpleName;

    private final List<InjectionPointModel> fieldInjections = new ArrayList<>();
    private final List<InjectionPointModel> constructorInjections = new ArrayList<>();
    private final List<ExecutableElement> postConstructMethods = new ArrayList<>();
    private final List<ExecutableElement> preDestroyMethods = new ArrayList<>();

    private BeanModel(TypeElement type, String pkg, String simple) {
        this.type = type;
        this.packageName = pkg;
        this.simpleName = simple;
    }

    public static BeanModel from(Object env, TypeElement type) {
        return new BeanModel(type, ElementUtilsEx.packageName(type), ElementUtilsEx.simpleName(type));
    }

    public void addInjectionPoint(InjectionPointModel ip) { fieldInjections.add(ip); }
    public void addConstructorInjection(InjectionPointModel ip) { constructorInjections.add(ip); }

    public void addPostConstruct(ExecutableElement m) { postConstructMethods.add(m); }
    public void addPreDestroy(ExecutableElement m) { preDestroyMethods.add(m); }

    public List<InjectionPointModel> fieldInjections() { return fieldInjections; }
    public List<InjectionPointModel> constructorInjections() { return constructorInjections; }
    public List<ExecutableElement> postConstructMethods() { return postConstructMethods; }
    public List<ExecutableElement> preDestroyMethods() { return preDestroyMethods; }
}
