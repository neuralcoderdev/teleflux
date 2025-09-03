package org.neuralcoder.teleflux.di.example.service;

import org.neuralcoder.teleflux.di.api.annotations.Service;

@Service("primary")
public class PrimaryNotifier implements Notifier {

    @Override public String notify(String payload) { return "[PRIMARY] " + payload; }
}
