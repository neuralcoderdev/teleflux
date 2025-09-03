package org.neuralcoder.teleflux.di.processor.scan;

import org.neuralcoder.teleflux.di.processor.env.ProcessorEnv;
import org.neuralcoder.teleflux.di.processor.model.BeanModel;
import org.neuralcoder.teleflux.di.processor.model.InjectionPointModel;
import org.neuralcoder.teleflux.di.processor.model.ProvisionMethodModel;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import java.util.*;

/**
 * Scans the compilation round and builds in-memory models
 * for beans, modules and injection points.
 */
public final class AnnotationScanner {
    private final ProcessorEnv env;

    public AnnotationScanner(ProcessorEnv env) {
        this.env = env;
    }

    public ScanResult scan(RoundEnvironment round) {
        Map<TypeElement, BeanModel> beans = new LinkedHashMap<>();
        Map<ExecutableElement, ProvisionMethodModel> provisions = new LinkedHashMap<>();

        for (Element e : round.getRootElements()) {
            if (!(e.getKind().isClass() || e.getKind().isInterface())) continue;
            var type = (TypeElement) e;

            boolean isBean =
                    ElementUtilsEx.hasAnnotation(type, DiAnnotationSet.SERVICE) ||
                            ElementUtilsEx.hasAnnotation(type, DiAnnotationSet.COMPONENT);

            boolean isModule = ElementUtilsEx.hasAnnotation(type, DiAnnotationSet.MODULE);

            if (isBean) {
                var bm = BeanModel.from(env, type);

                // Fields @Inject
                for (Element enclosed : type.getEnclosedElements()) {
                    if (enclosed.getKind() == ElementKind.FIELD &&
                            ElementUtilsEx.hasAnnotation(enclosed, DiAnnotationSet.INJECT)) {
                        bm.addInjectionPoint(InjectionPointModel.forField((VariableElement) enclosed));
                    }
                }
                // Constructor @Inject (prefer one) — if absent, we will validate presence of public no-arg
                for (Element enclosed : type.getEnclosedElements()) {
                    if (enclosed.getKind() == ElementKind.CONSTRUCTOR &&
                            ElementUtilsEx.hasAnnotation(enclosed, DiAnnotationSet.INJECT)) {
                        bm.addConstructorInjection(InjectionPointModel.forConstructor((ExecutableElement) enclosed));
                    }
                }
                // PostConstruct / PreDestroy methods
                for (Element enclosed : type.getEnclosedElements()) {
                    if (enclosed.getKind() != ElementKind.METHOD) continue;
                    var method = (ExecutableElement) enclosed;
                    if (ElementUtilsEx.hasAnnotation(method, DiAnnotationSet.POST_CONSTRUCT)) {
                        bm.addPostConstruct(method);
                    }
                    if (ElementUtilsEx.hasAnnotation(method, DiAnnotationSet.PRE_DESTROY)) {
                        bm.addPreDestroy(method);
                    }
                }

                beans.put(type, bm);
            }

            if (isModule) {
                for (Element enclosed : type.getEnclosedElements()) {
                    if (enclosed.getKind() == ElementKind.METHOD &&
                            ElementUtilsEx.hasAnnotation(enclosed, DiAnnotationSet.PROVIDES)) {
                        provisions.put((ExecutableElement) enclosed,
                                ProvisionMethodModel.from(env, (ExecutableElement) enclosed));
                    }
                }
            }
        }

        return new ScanResult(beans, provisions);
    }

    /** Aggregated scan result. */
    public record ScanResult(Map<TypeElement, BeanModel> beans,
                             Map<ExecutableElement, ProvisionMethodModel> provisions) {}
}
