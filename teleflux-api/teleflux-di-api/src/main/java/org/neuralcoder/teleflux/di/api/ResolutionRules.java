package org.neuralcoder.teleflux.di.api;

public record ResolutionRules(AmbiguityPolicy ambiguityPolicy) {
    public enum AmbiguityPolicy {THROW, LOWEST_ORDER_WINS}

    public static ResolutionRules strict() {
        return new ResolutionRules(AmbiguityPolicy.THROW);
    }

    public static ResolutionRules lowestOrderWins() {
        return new ResolutionRules(AmbiguityPolicy.LOWEST_ORDER_WINS);
    }

    public static final String DOC = """
            Bean resolution rules:

            1) Exact match on @Qualifier (if present).
               - If a qualifier is declared on the injection point, only beans with
                 the same qualifier are considered candidates.

            2) If multiple candidates remain, @Primary is preferred.
               - At most one @Primary bean should exist per type + qualifier.
               - If multiple @Primary beans are found, this is an error
                 (AmbiguousBeanException).

            3) If there are still multiple candidates and no @Primary,
               an AmbiguousBeanException is thrown.

            4) If no candidates are found, a NoSuchBeanException is thrown.

            5) When injecting collections (List/Set/Map):
               - All matching beans are injected.
               - Ordering is determined by the @Order annotation.
               - If no @Order is specified, beans are sorted by their class name.

            6) @Lazy and Provider<T> are respected for deferred resolution.

            7) @Profile annotations restrict beans to specific active profiles.
               Beans with non-matching profiles are ignored during resolution.

            8) Scopes:
               - @Singleton beans are created once per container.
               - @Prototype beans are created on every injection.
               - Custom scopes can be defined with @ScopeType.
            """;
}