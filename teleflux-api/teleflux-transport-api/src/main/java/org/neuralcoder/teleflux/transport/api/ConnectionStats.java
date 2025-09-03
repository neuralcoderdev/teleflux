package org.neuralcoder.teleflux.transport.api;

import lombok.Value;

/** Immutable snapshot of connection statistics. */
@Value
public class ConnectionStats {
    long bytesSent;
    long bytesReceived;
    long reconnects;
}
