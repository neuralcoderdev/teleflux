package org.neuralcoder.teleflux.di.example.service;

import org.neuralcoder.teleflux.di.api.annotations.Service;
import org.neuralcoder.teleflux.di.api.annotations.Singleton;

import java.time.Instant;

@Service
@Singleton
public class TimeService {
    public Instant now() { return Instant.now(); }
    public static String fmt(Instant t) { return t.toString(); }
}
