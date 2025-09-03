package org.neuralcoder.teleflux.core.api.error;

public class ProtocolException extends TelefluxException {
    public ProtocolException(String message) { super(message); }
    public ProtocolException(String message, Throwable cause) { super(message, cause); }
}