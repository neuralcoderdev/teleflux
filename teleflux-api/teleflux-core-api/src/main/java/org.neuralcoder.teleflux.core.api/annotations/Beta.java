package org.neuralcoder.teleflux.core.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/** Маркер бета-API. */
@Retention(CLASS)
@Target({TYPE, METHOD})
public @interface Beta {}
