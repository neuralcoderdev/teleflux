package org.neuralcoder.teleflux.di.example;

import org.neuralcoder.teleflux.di.api.BeanFactory;
import org.neuralcoder.teleflux.di.example.config.AppConfig;
import org.neuralcoder.teleflux.di.example.service.AppService;
import org.neuralcoder.teleflux.di.example.service.CompositeService;
import org.neuralcoder.teleflux.di.example.service.DevOnlyService;
import org.neuralcoder.teleflux.di.impl.DefaultBeanFactory;

public class DIExampleMain {
    public static void main(String[] args) {
        BeanFactory di = new DefaultBeanFactory();
        di.start();

        AppConfig cfg = di.get(AppConfig.class);
        System.out.println("[Bootstrap] Config: greetingPrefix=" + cfg.greetingPrefix() + ", maxRetries=" + cfg.maxRetries());

        di.find(DevOnlyService.class).ifPresentOrElse(
                d -> System.out.println("[Bootstrap] DevOnlyService says: " + d.msg()),
                () -> System.out.println("[Bootstrap] DevOnlyService is NOT active (no 'dev' profile)")
        );

        di.get(AppService.class).run();
        di.get(CompositeService.class).runAll("Teleflux User");

        di.stop();
    }
}
