package org.neuralcoder.teleflux.transport.api;

public interface FrameCodec {
    Frame encode(byte[] bytes);
    byte[] decode(Frame frame);
}
