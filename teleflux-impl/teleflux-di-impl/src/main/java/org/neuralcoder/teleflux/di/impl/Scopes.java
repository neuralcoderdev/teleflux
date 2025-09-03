package org.neuralcoder.teleflux.di.impl;


import org.neuralcoder.teleflux.di.api.Scope;

/** Scope helpers (reserved for custom scopes in future). */
final class Scopes {
    private Scopes() {}
    static boolean isSingleton(Scope s) { return s == Scope.SINGLETON; }
}
