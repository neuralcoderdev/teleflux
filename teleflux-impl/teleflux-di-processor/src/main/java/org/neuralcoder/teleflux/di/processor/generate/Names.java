package org.neuralcoder.teleflux.di.processor.generate;

/** Centralizes naming conventions for generated classes. */
public final class Names {
    private Names() {}

    public static String factorySimple(String simpleName) {
        return simpleName + "_Factory";
    }

    public static String moduleAdapterSimple(String simpleName) {
        return simpleName + "_ModuleAdapter";
    }
}