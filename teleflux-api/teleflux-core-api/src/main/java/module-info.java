module org.neuralcoder.teleflux.core.api {
    exports org.neuralcoder.teleflux.core.api;
    exports org.neuralcoder.teleflux.core.api.error;
    exports org.neuralcoder.teleflux.core.api.options;
    exports org.neuralcoder.teleflux.core.api.policy.retry;
    exports org.neuralcoder.teleflux.core.api.policy.ratelimit;
    exports org.neuralcoder.teleflux.core.api.requests;
    exports org.neuralcoder.teleflux.core.api.responses;
    exports org.neuralcoder.teleflux.core.api.session;
    exports org.neuralcoder.teleflux.core.api.types;
    exports org.neuralcoder.teleflux.core.api.util;
    exports org.neuralcoder.teleflux.core.api.annotations;

    requires static lombok;
}