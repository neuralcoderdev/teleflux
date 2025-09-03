package org.neuralcoder.teleflux.di.processor;

import com.squareup.javapoet.JavaFile;
import org.neuralcoder.teleflux.di.processor.env.ProcessorEnv;
import org.neuralcoder.teleflux.di.processor.env.ProcessorOptions;
import org.neuralcoder.teleflux.di.processor.generate.GeneratedFactoryWriter;
import org.neuralcoder.teleflux.di.processor.generate.GeneratedIndexWriter;
import org.neuralcoder.teleflux.di.processor.index.ComponentIndexModel;
import org.neuralcoder.teleflux.di.processor.scan.AnnotationScanner;
import org.neuralcoder.teleflux.di.processor.scan.ValidationReporter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Teleflux DI annotation processor.
 * Scans DI annotations and generates:
 *  - Component index (fast runtime bootstrap)
 *  - Bean factories for @Service/@Component classes
 *  - Provision modules for @Module/@Provides methods
 */
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@SupportedOptions({
        ProcessorOptions.OPTION_DEBUG,
        ProcessorOptions.OPTION_INDEX_CLASS,
        ProcessorOptions.OPTION_PACKAGE
})
@SupportedAnnotationTypes({
        // DI annotations (core)
        "org.neuralcoder.teleflux.di.api.annotations.Inject",
        "org.neuralcoder.teleflux.di.api.annotations.Service",
        "org.neuralcoder.teleflux.di.api.annotations.Component",
        "org.neuralcoder.teleflux.di.api.annotations.Module",
        "org.neuralcoder.teleflux.di.api.annotations.Provides",
        "org.neuralcoder.teleflux.di.api.annotations.Qualifier",
        "org.neuralcoder.teleflux.di.api.annotations.Singleton",
        "org.neuralcoder.teleflux.di.api.annotations.ScopeType",
        "org.neuralcoder.teleflux.di.api.annotations.Eager",
        "org.neuralcoder.teleflux.di.api.annotations.Lazy",
        "org.neuralcoder.teleflux.di.api.annotations.Primary",
        "org.neuralcoder.teleflux.di.api.annotations.Order",
        "org.neuralcoder.teleflux.di.api.annotations.PostConstruct",
        "org.neuralcoder.teleflux.di.api.annotations.PreDestroy",
        "org.neuralcoder.teleflux.di.api.annotations.Profile",
        "org.neuralcoder.teleflux.di.api.annotations.Config",
        "org.neuralcoder.teleflux.di.api.annotations.ConfigProperty",

        // Bonus: index update handlers for auto-registration (optional)
        "org.neuralcoder.teleflux.update.api.annotations.EventListenerComponent",
        "org.neuralcoder.teleflux.update.api.annotations.OnEvent"
})
public final class TelefluxDiProcessor extends AbstractProcessor {

    private ProcessorEnv env;
    private AnnotationScanner scanner;
    private ValidationReporter reporter;
    private GeneratedIndexWriter indexWriter;
    private GeneratedFactoryWriter factoryWriter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.env = new ProcessorEnv(processingEnv);
        this.scanner = new AnnotationScanner(env);
        this.reporter = new ValidationReporter(env);
        this.indexWriter = new GeneratedIndexWriter(env);
        this.factoryWriter = new GeneratedFactoryWriter(env);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        // 1) Scan source elements for DI annotations and build models
        var scanResult = scanner.scan(roundEnv);

        // 2) Validate the collected models (conflicts, cycles, qualifiers)
        reporter.validate(scanResult);

        // 3) Generate index (ComponentIndex) for fast runtime bootstrap
        ComponentIndexModel indexModel = ComponentIndexModel.from(scanResult);
        JavaFile indexFile = indexWriter.write(indexModel);
        env.write(indexFile);

        // 4) Generate per-bean factories (constructor/method/field injection)
        factoryWriter.writeAll(scanResult).forEach(env::write);

        // 5) Optionally generate module adapters for @Module/@Provides
        // (kept in a separate writer class for clarity; invoked from factoryWriter if needed)

        return false; // allow other processors to run as well
    }
}
