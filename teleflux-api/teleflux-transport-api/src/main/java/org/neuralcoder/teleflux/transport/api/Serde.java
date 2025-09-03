package org.neuralcoder.teleflux.transport.api;

public interface Serde {
    byte[] serialize(Object value);
    <T> T deserialize(byte[] bytes, Class<T> type);
}
