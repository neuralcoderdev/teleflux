package org.neuralcoder.teleflux.di.api;

import lombok.NonNull;
import lombok.Value;

/** Bean key = Type + optional qualifier. */
@Value
public class BeanKey<T> {
    @NonNull Class<T> type;
    String qualifier; // may be null

    /** Strongly-typed factory. */
    public static <T> BeanKey<T> of(Class<T> type) {
        // Explicit type arg prevents inference surprises.
        return new BeanKey<T>(type, null);
    }

    /** Strongly-typed factory with qualifier. */
    public static <T> BeanKey<T> of(Class<T> type, String qualifier) {
        return new BeanKey<T>(type, qualifier);
    }

    /**
     * Wildcard-friendly factory: accepts Class<?> when T is unknown at compile time.
     * We instantiate BeanKey<Object> and return it as BeanKey<?> (denotable).
     */
    public static BeanKey<?> ofWildcard(Class<?> type) {
        @SuppressWarnings("unchecked")
        Class<Object> t = (Class<Object>) type;
        return new BeanKey<Object>(t, null);
    }

    /** Wildcard-friendly factory with qualifier. */
    public static BeanKey<?> ofWildcard(Class<?> type, String qualifier) {
        @SuppressWarnings("unchecked")
        Class<Object> t = (Class<Object>) type;
        return new BeanKey<Object>(t, qualifier);
    }
}
