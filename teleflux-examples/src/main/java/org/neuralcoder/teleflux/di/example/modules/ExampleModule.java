package org.neuralcoder.teleflux.di.example.modules;

import org.neuralcoder.teleflux.di.api.annotations.Module;
import org.neuralcoder.teleflux.di.api.annotations.Provides;
import org.neuralcoder.teleflux.di.example.qualifiers.Channel;
import org.neuralcoder.teleflux.di.example.service.Greeter;

import java.util.function.Function;

@Module
public class ExampleModule {

    @Provides
    @Channel("banner")
    public String banner() {
        return "<<< TELEFLUX >>>";
    }

    @Provides
    @Channel("banner2")
    public String banner2() {
        return "<<< TELEFLUX BANNER 2 >>>";
    }

    @Provides
    public Function<String, String> greeterDecorator(Greeter g) {
        return name -> "[decorated] " + g.greet(name);
    }
}
