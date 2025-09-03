package org.neuralcoder.teleflux.transport.api.annotations;

import org.neuralcoder.teleflux.transport.api.ProtocolFamily;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a class as a Transport implementation.
 * Used by code generators to register available transports.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface TransportImpl {
    ProtocolFamily value();
    /** Human-readable name (e.g., "netty-tcp"). */
    String name() default "";
}
