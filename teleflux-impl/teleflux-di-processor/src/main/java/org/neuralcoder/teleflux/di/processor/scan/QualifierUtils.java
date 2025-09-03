package org.neuralcoder.teleflux.di.processor.scan;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import java.util.Optional;

/** Finds qualifier value on an element, supporting composed meta-qualifiers. */
public final class QualifierUtils {
    private static final String QUALIFIER = "org.neuralcoder.teleflux.di.api.annotations.Qualifier";
    private QualifierUtils() {}

    public static Optional<String> findQualifierOn(Element element) {
        // direct qualifier on the element
        for (AnnotationMirror am : element.getAnnotationMirrors()) {
            if (isQualifierAnnotation(am)) {
                return valueOf(am);
            }
            // composed: annotation has meta-annotation which is itself annotated with @Qualifier
            for (AnnotationMirror meta : am.getAnnotationType().asElement().getAnnotationMirrors()) {
                if (isQualifierAnnotation(meta)) {
                    return valueOf(meta);
                }
            }
        }
        return Optional.empty();
    }

    private static boolean isQualifierAnnotation(AnnotationMirror am) {
        var annType = (DeclaredType) am.getAnnotationType();
        var annEl = annType.asElement();
        for (AnnotationMirror meta : annEl.getAnnotationMirrors()) {
            if (meta.getAnnotationType().toString().equals(QUALIFIER)) return true;
        }
        // if it's exactly @Qualifier on the annotation type itself
        return annType.toString().equals(QUALIFIER);
    }

    private static Optional<String> valueOf(AnnotationMirror am) {
        for (var e : am.getElementValues().entrySet()) {
            if (e.getKey().getSimpleName().contentEquals("value")) {
                return Optional.ofNullable(String.valueOf(e.getValue().getValue()));
            }
        }
        return Optional.empty();
    }
}
