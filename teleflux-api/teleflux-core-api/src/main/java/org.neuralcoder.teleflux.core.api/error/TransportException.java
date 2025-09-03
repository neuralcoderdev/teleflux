package org.neuralcoder.teleflux.core.api.error;

public class TransportException extends TelefluxException {
    public TransportException(String message) { super(message); }
    public TransportException(String message, Throwable cause) { super(message, cause); }
}