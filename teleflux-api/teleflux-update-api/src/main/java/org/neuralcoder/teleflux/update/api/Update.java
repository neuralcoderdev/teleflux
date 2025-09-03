package org.neuralcoder.teleflux.update.api;

public interface Update extends Event {
    UpdateType updateType();
}