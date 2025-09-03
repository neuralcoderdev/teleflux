package org.neuralcoder.teleflux.di.processor.index;


import com.squareup.javapoet.CodeBlock;
import org.neuralcoder.teleflux.di.processor.scan.AnnotationScanner;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

/** Holds discovered components to generate a static index. */
public final class ComponentIndexModel {
    private final List<String> components = new ArrayList<>();

    public static ComponentIndexModel from(AnnotationScanner.ScanResult scan) {
        var m = new ComponentIndexModel();
        for (TypeElement type : scan.beans().keySet()) {
            m.components.add(type.getQualifiedName().toString());
        }
        return m;
    }

    public CodeBlock toArrayInitializer() {
        CodeBlock.Builder b = CodeBlock.builder().add("{");
        for (int i = 0; i < components.size(); i++) {
            b.add("$S", components.get(i));
            if (i < components.size() - 1) b.add(", ");
        }
        b.add("}");
        return b.build();
    }
}
