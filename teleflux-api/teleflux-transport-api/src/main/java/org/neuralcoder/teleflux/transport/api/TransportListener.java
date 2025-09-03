package org.neuralcoder.teleflux.transport.api;

/**
 * Callback interface for transport lifecycle and inbound frames.
 * All callbacks MUST be non-blocking and fast; dispatch heavy work elsewhere.
 */
public interface TransportListener {
    /** Called once the transport transitions to CONNECTED. */
    void onConnected(Endpoint endpoint);

    /** Called when the transport transitions to DISCONNECTED or encounters an error. */
    void onDisconnected(Throwable cause);

    /** Called for each inbound frame received from the wire. */
    void onFrame(Frame frame);
}
