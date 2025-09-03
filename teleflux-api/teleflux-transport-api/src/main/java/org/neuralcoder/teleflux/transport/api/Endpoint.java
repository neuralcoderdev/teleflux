package org.neuralcoder.teleflux.transport.api;

import lombok.Value;

/** Destination address descriptor resolved by EndpointResolver. */
@Value
public class Endpoint {
    String host;
    int port;
    boolean tls;
    /** Optional datacenter or shard tag for diagnostics. */
    String tag;
}