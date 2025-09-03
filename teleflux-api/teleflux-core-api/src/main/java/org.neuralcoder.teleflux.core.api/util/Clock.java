package org.neuralcoder.teleflux.core.api.util;

import java.time.Instant;

public interface Clock {
    Instant now();

    long monotonicNanos();
}