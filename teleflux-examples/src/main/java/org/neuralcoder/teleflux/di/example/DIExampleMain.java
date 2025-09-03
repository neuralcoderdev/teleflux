package org.neuralcoder.teleflux.di.example;

import org.neuralcoder.teleflux.di.api.BeanFactory;
import org.neuralcoder.teleflux.di.example.service.AppService;
import org.neuralcoder.teleflux.di.impl.DefaultBeanFactory;

/**
 * Minimal bootstrap to validate DI end-to-end:
 * - annotation processor generates factories and index
 * - runtime loads them and instantiates beans
 * - field and constructor injections work
 * - lifecycle hooks are invoked on stop()
 */
public class DIExampleMain {
    public static void main(String[] args) {
        BeanFactory di = new DefaultBeanFactory();
        di.start();

        AppService app = di.get(AppService.class);
        app.runDemo();

        di.stop();
    }
}
