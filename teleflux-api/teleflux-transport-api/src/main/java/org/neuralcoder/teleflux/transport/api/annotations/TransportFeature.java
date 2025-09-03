package org.neuralcoder.teleflux.transport.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Declares an optional capability (feature flag) supported by a transport,
 * e.g. "zero-copy", "alpn", "permessage-deflate".
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface TransportFeature {
    String[] value();
}