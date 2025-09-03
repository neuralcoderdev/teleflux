package org.neuralcoder.teleflux.core.api.error;

public class TelefluxException extends RuntimeException {

    public TelefluxException() {
    }

    public TelefluxException(String message) {
        super(message);
    }

    public TelefluxException(String message, Throwable cause) {
        super(message, cause);
    }

    public TelefluxException(Throwable cause) {
        super(cause);
    }

}