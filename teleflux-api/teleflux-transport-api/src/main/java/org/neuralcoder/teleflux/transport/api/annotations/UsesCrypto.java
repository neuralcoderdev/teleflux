package org.neuralcoder.teleflux.transport.api.annotations;

import org.neuralcoder.teleflux.transport.api.CryptoProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Declares the CryptoProvider dependency expected by this Transport. */
@Retention(RUNTIME)
@Target(TYPE)
public @interface UsesCrypto {
    Class<? extends CryptoProvider> value();
}