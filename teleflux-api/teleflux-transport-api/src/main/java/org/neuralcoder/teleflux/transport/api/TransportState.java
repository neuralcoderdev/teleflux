package org.neuralcoder.teleflux.transport.api;

/** Finite set of transport lifecycle states. */
public enum TransportState {
    NEW, CONNECTING, CONNECTED, DISCONNECTED, CLOSED
}
