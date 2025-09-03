package org.neuralcoder.teleflux.di.example.service;

import org.neuralcoder.teleflux.di.api.annotations.Service;
import org.neuralcoder.teleflux.di.api.annotations.Singleton;

import java.time.Instant;

/** Provides current time (singleton for demo). */
@Service
@Singleton
public class TimeService {
    public Instant now() { return Instant.now(); }
}
