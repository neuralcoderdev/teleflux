package org.neuralcoder.teleflux.core.api.util;

public interface Logger {
    void trace(String msg, Object... args);

    void debug(String msg, Object... args);

    void info(String msg, Object... args);

    void warn(String msg, Object... args);

    void error(String msg, Throwable t);
}
