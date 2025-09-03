package org.neuralcoder.teleflux.di.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Simple registry for manual programmatic registrations (optional).
 * Not used by default; left for future extensions.
 */
final class InternalRegistry {
    final Map<Class<?>, Supplier<?>> providers = new ConcurrentHashMap<>();
}
