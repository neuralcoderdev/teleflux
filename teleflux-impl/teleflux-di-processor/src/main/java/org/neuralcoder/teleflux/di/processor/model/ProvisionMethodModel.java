package org.neuralcoder.teleflux.di.processor.model;


import org.neuralcoder.teleflux.di.processor.env.ProcessorEnv;

import javax.lang.model.element.ExecutableElement;

/** Model for @Provides methods. */
public final class ProvisionMethodModel {
    public final ExecutableElement method;

    private ProvisionMethodModel(ExecutableElement method) {
        this.method = method;
    }

    public static ProvisionMethodModel from(ProcessorEnv env, ExecutableElement method) {
        return new ProvisionMethodModel(method);
    }
}
