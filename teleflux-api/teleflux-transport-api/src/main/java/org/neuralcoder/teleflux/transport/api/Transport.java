package org.neuralcoder.teleflux.transport.api;

import java.util.concurrent.CompletionStage;

/**
 * Low-level, connection-oriented transport abstraction.
 * Implementations MUST be thread-safe and non-blocking where possible.
 */
public interface Transport {

    /**
     * Establishes a connection to the given endpoint.
     * This method is idempotent; repeated calls should be safe.
     */
    void connect(Endpoint endpoint);

    /** Closes the connection and releases resources. Idempotent. */
    void close();

    /** Current state of this transport. */
    TransportState state();

    /** Attach a listener for state changes and inbound frames. */
    void setListener(TransportListener listener);

    /**
     * Sends a fully framed payload.
     * Implementations MUST honor backpressure and return a completion
     * that completes when the frame is flushed to the OS buffers.
     */
    CompletionStage<Void> send(Frame frame);

    /** Returns current connection statistics snapshot. */
    ConnectionStats stats();
}