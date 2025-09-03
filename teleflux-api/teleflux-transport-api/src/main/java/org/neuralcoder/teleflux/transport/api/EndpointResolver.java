package org.neuralcoder.teleflux.transport.api;

public interface EndpointResolver {
    Endpoint resolve(int dcId, int shard);
}
