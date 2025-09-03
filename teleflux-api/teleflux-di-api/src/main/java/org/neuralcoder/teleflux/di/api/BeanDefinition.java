package org.neuralcoder.teleflux.di.api;

import org.neuralcoder.teleflux.di.api.annotations.ScopeType;

import java.util.List;
import java.util.Optional;

/** Read-only SPI-описание зарегистрированного бина (для диагностики/метаданных). */
public interface BeanDefinition {
    Class<?> beanClass();
    String name();
    Optional<String> qualifier();
    Scope scope();
    boolean primary();
    boolean eager();
    boolean lazy();
    List<Class<?>> contracts(); // какие интерфейсы реализует
    List<String> profiles();    // активные/требуемые профили
    List<InjectionPoint> injectionPoints();
    Optional<ScopeType> declaredScopeType(); // если использована @ScopeType
}
