package org.neuralcoder.teleflux.transport.api.annotations;


import org.neuralcoder.teleflux.transport.api.FrameCodec;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Declares the FrameCodec type used by this Transport. */
@Retention(RUNTIME)
@Target(TYPE)
public @interface UsesCodec {
    Class<? extends FrameCodec> value();
}
