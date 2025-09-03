package org.neuralcoder.teleflux.di.processor.scan;

import javax.lang.model.element.*;
import java.util.Optional;

/** Misc element utilities missing in standard API. */
public final class ElementUtilsEx {
    private ElementUtilsEx() {}

    public static Optional<AnnotationMirror> findAnnotation(Element e, String fqcn) {
        for (var am : e.getAnnotationMirrors()) {
            if (am.getAnnotationType().toString().equals(fqcn)) return Optional.of(am);
        }
        return Optional.empty();
    }

    public static boolean hasAnnotation(Element e, String fqcn) {
        return findAnnotation(e, fqcn).isPresent();
    }

    public static String simpleName(TypeElement type) {
        return type.getSimpleName().toString();
    }

    public static String packageName(Element e) {
        Element t = e;
        while (t != null && t.getKind() != ElementKind.PACKAGE) t = t.getEnclosingElement();
        return (t instanceof PackageElement pe) ? pe.getQualifiedName().toString() : "";
    }
}
