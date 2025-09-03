package org.neuralcoder.teleflux.core.api.requests;

import org.neuralcoder.teleflux.core.api.responses.Response;
import org.neuralcoder.teleflux.core.api.types.RequestId;

import java.time.Duration;

public interface Request<T extends Response> {
    RequestId id();

    Duration timeout();
}