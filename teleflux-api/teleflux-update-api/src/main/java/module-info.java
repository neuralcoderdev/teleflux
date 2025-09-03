module org.neuralcoder.teleflux.update.api {
    exports org.neuralcoder.teleflux.update.api;
    exports org.neuralcoder.teleflux.update.api.context;
    exports org.neuralcoder.teleflux.update.api.filter;
    exports org.neuralcoder.teleflux.update.api.middleware;
    exports org.neuralcoder.teleflux.update.api.bus;
    exports org.neuralcoder.teleflux.update.api.annotations;

    requires org.neuralcoder.teleflux.core.api;
    requires static lombok;
}