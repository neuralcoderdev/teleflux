package org.neuralcoder.teleflux.di.example.service;

import org.neuralcoder.teleflux.di.api.annotations.Inject;
import org.neuralcoder.teleflux.di.api.annotations.Service;

import java.util.List;
import java.util.Set;

@Service
public class CompositeService {

    @Inject List<Greeter> greeters;
    @Inject Set<Notifier> notifiers;

    public void runAll(String name) {
        System.out.println("[Composite] Greeters (ordered): " + greeters.size());
        greeters.forEach(g -> System.out.println(" - " + g.getClass().getSimpleName() + ": " + g.greet(name)));

        System.out.println("[Composite] Notifiers (set): " + notifiers.size());
        notifiers.forEach(n -> System.out.println(" - " + n.getClass().getSimpleName() + ": " + n.notify("ping")));
    }
}
