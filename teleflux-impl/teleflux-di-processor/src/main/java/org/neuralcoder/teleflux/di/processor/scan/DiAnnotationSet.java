package org.neuralcoder.teleflux.di.processor.scan;

import javax.lang.model.element.Name;

/** Canonical names of DI annotations used by the processor. */
public final class DiAnnotationSet {
    private DiAnnotationSet() {}

    public static final String SERVICE = "org.neuralcoder.teleflux.di.api.annotations.Service";
    public static final String COMPONENT = "org.neuralcoder.teleflux.di.api.annotations.Component";
    public static final String MODULE = "org.neuralcoder.teleflux.di.api.annotations.Module";
    public static final String PROVIDES = "org.neuralcoder.teleflux.di.api.annotations.Provides";
    public static final String INJECT = "org.neuralcoder.teleflux.di.api.annotations.Inject";
    public static final String QUALIFIER = "org.neuralcoder.teleflux.di.api.annotations.Qualifier";
    public static final String SINGLETON = "org.neuralcoder.teleflux.di.api.annotations.Singleton";
    public static final String SCOPE_TYPE = "org.neuralcoder.teleflux.di.api.annotations.ScopeType";
    public static final String EAGER = "org.neuralcoder.teleflux.di.api.annotations.Eager";
    public static final String LAZY = "org.neuralcoder.teleflux.di.api.annotations.Lazy";
    public static final String PRIMARY = "org.neuralcoder.teleflux.di.api.annotations.Primary";
    public static final String ORDER = "org.neuralcoder.teleflux.di.api.annotations.Order";
    public static final String PROFILE = "org.neuralcoder.teleflux.di.api.annotations.Profile";
    public static final String POST_CONSTRUCT = "org.neuralcoder.teleflux.di.api.annotations.PostConstruct";
    public static final String PRE_DESTROY = "org.neuralcoder.teleflux.di.api.annotations.PreDestroy";

    // Optional (cross-module)
    public static final String EVENT_LISTENER_COMPONENT = "org.neuralcoder.teleflux.update.api.annotations.EventListenerComponent";
    public static final String ON_EVENT = "org.neuralcoder.teleflux.update.api.annotations.OnEvent";

    /** Helper for safe comparisons. */
    public static boolean is(Name annotationName, String fqcn) {
        return annotationName != null && fqcn.contentEquals(annotationName);
    }
}
