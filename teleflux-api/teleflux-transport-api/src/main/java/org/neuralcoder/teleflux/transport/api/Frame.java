package org.neuralcoder.teleflux.transport.api;

public interface Frame {
    /** Returns the raw, framed payload ready to go on the wire. */
    byte[] payload();
}
