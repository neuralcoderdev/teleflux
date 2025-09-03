package org.neuralcoder.teleflux.di.api;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BeanDefinition {
    Class<?> beanClass;
    String qualifier;
    boolean primary;
    int order;
    boolean eager;
    boolean lazy;
    Scope scope;
    Source source;

    public enum Source { FACTORY, CONFIG, MODULE_PROVIDES }
}
