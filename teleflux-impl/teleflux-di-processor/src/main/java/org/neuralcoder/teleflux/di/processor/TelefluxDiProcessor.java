package org.neuralcoder.teleflux.di.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import org.neuralcoder.teleflux.di.api.annotations.Component;
import org.neuralcoder.teleflux.di.api.annotations.Config;
import org.neuralcoder.teleflux.di.api.annotations.Module;
import org.neuralcoder.teleflux.di.api.annotations.Service;
import org.neuralcoder.teleflux.di.processor.env.ProcessorEnv;
import org.neuralcoder.teleflux.di.processor.generate.GeneratedFactoryWriter;
import org.neuralcoder.teleflux.di.processor.generate.GeneratedIndexWriter;
import org.neuralcoder.teleflux.di.processor.scan.AnnotationScanner;
import org.neuralcoder.teleflux.di.processor.scan.ValidationReporter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Teleflux DI annotation processor (stable baseline).
 * - Generates *_Factory per @Service/@Component
 * - Generates ComponentIndex exactly once (final round)
 * - Index includes @Service, @Component, @Config, @Module classes
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@SupportedAnnotationTypes({"*"})
public final class TelefluxDiProcessor extends AbstractProcessor {
    private ProcessorEnv env;
    private AnnotationScanner scanner;
    private ValidationReporter reporter;
    private GeneratedFactoryWriter factoryWriter;
    private GeneratedIndexWriter indexWriter;

    private final Set<String> componentNames = new LinkedHashSet<>();
    private boolean indexWritten = false;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.env = new ProcessorEnv(processingEnv);
        this.scanner = new AnnotationScanner(env);
        this.reporter = new ValidationReporter(env);
        this.factoryWriter = new GeneratedFactoryWriter(env);
        this.indexWriter = new GeneratedIndexWriter(env);

        // Try to preload previous index (from SOURCE_OUTPUT) for incremental builds
        mergePreviousIndexIfExists();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var scan = scanner.scan(roundEnv);

        // Collect beans discovered this round
        scan.beans().keySet().forEach(t -> componentNames.add(t.getQualifiedName().toString()));

        // Also include directly annotated types (these might not be covered by scan.beans())
        addAllAnnotated(roundEnv, Config.class);
        addAllAnnotated(roundEnv, Module.class);
        addAllAnnotated(roundEnv, Service.class);
        addAllAnnotated(roundEnv, Component.class);

        reporter.validate(scan);

        // Generate factories for beans in this round
        for (JavaFile jf : factoryWriter.writeAll(scan)) {
            env.write(jf);
        }

        // Write merged index once at the end
        if (roundEnv.processingOver() && !indexWritten) {
            env.write(indexWriter.write(componentNames));
            indexWritten = true;
        }
        return false;
    }

    private void addAllAnnotated(RoundEnvironment roundEnv, Class<?> ann) {
        for (Object e : roundEnv.getElementsAnnotatedWith((Class) ann)) {
            if (e instanceof TypeElement te) {
                componentNames.add(te.getQualifiedName().toString());
            }
        }
    }

    /** Merge previously generated ComponentIndex.java into componentNames (best-effort). */
    private void mergePreviousIndexIfExists() {
        try {
            FileObject fo = processingEnv.getFiler().getResource(
                    StandardLocation.SOURCE_OUTPUT,
                    "org.neuralcoder.teleflux.di.generated",
                    "ComponentIndex.java");
            // If it exists, parse the array literal: new String[] { "a","b", ... }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(fo.openInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    extractQuotedStrings(line, componentNames);
                }
            }
        } catch (IOException ignored) {
            // No previous index in SOURCE_OUTPUT (first build or clean) — skip
        }
    }

    private static void extractQuotedStrings(String line, Set<String> sink) {
        int i = 0;
        while (i < line.length()) {
            int start = line.indexOf('"', i);
            if (start < 0) break;
            int end = line.indexOf('"', start + 1);
            if (end < 0) break;
            if (end > start + 1) {
                String val = line.substring(start + 1, end);
                if (isFqcn(val)) sink.add(val);
            }
            i = end + 1;
        }
    }

    private static boolean isFqcn(String s) {
        // naive but safe enough for our generated content
        return s.contains(".") && !s.contains(" ") && !s.endsWith(".java");
    }
}
