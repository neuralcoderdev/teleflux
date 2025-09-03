package org.neuralcoder.teleflux.di.processor.generate;

import com.squareup.javapoet.*;
import org.neuralcoder.teleflux.di.processor.env.ProcessorEnv;

import javax.lang.model.element.Modifier;
import java.util.Collection;

/** Writes ComponentIndex with all discovered component-like classes. */
public final class GeneratedIndexWriter {
    private final ProcessorEnv env;
    public GeneratedIndexWriter(ProcessorEnv env) { this.env = env; }

    public JavaFile write(Collection<String> componentFqcns) {
        ArrayTypeName arr = ArrayTypeName.of(String.class);
        FieldSpec.Builder f = FieldSpec.builder(arr, "COMPONENTS", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        CodeBlock.Builder init = CodeBlock.builder().add("new String[] {");
        boolean first = true;
        for (String s : componentFqcns) {
            if (!first) init.add(", ");
            init.add("$S", s);
            first = false;
        }
        init.add("}");

        f.initializer(init.build());

        TypeSpec idx = TypeSpec.classBuilder("ComponentIndex")
                .addJavadoc("Generated DI component index.\n")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(f.build())
                .build();

        return JavaFile.builder("org.neuralcoder.teleflux.di.generated", idx).build();
    }
}
