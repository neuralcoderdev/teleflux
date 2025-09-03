package org.neuralcoder.teleflux.update.api.bus;

import org.neuralcoder.teleflux.update.api.middleware.Middleware;

import java.util.List;

/** Read-only описание глобального конвейера обработки. */
public interface PipelineDescriptor {
    List<Middleware> middlewares();
}
