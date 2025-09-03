package org.neuralcoder.teleflux.di.api;

/**
 * Bean key = Type + optional qualifier.
 * Using a Java record guarantees a canonical constructor is present at compile time,
 * avoiding Lombok/AP issues and generic inference pitfalls.
 */
public record BeanKey<T>(Class<T> type, String qualifier) {

    /** Strongly-typed factory. */
    public static <T> BeanKey<T> of(Class<T> type) {
        return new BeanKey<>(type, null);
    }

    /** Strongly-typed factory with qualifier. */
    public static <T> BeanKey<T> of(Class<T> type, String qualifier) {
        return new BeanKey<>(type, qualifier);
    }

    /** Wildcard-friendly factory: accepts Class<?> when T is unknown at compile time. */
    public static BeanKey<?> ofWildcard(Class<?> type) {
        @SuppressWarnings("unchecked")
        Class<Object> t = (Class<Object>) type;
        return new BeanKey<>(t, null);
    }

    /** Wildcard-friendly factory with qualifier. */
    public static BeanKey<?> ofWildcard(Class<?> type, String qualifier) {
        @SuppressWarnings("unchecked")
        Class<Object> t = (Class<Object>) type;
        return new BeanKey<>(t, qualifier);
    }
}
