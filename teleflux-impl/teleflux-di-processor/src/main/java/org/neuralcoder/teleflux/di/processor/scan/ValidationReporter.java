package org.neuralcoder.teleflux.di.processor.scan;

import org.neuralcoder.teleflux.di.processor.env.ProcessorEnv;
import org.neuralcoder.teleflux.di.processor.model.BeanModel;

import javax.lang.model.element.*;
import java.util.List;
import java.util.Map;

/**
 * Validates models and reports errors/warnings with helpful messages.
 */
public final class ValidationReporter {
    private final ProcessorEnv env;

    public ValidationReporter(ProcessorEnv env) {
        this.env = env;
    }

    public void validate(AnnotationScanner.ScanResult result) {
        for (Map.Entry<TypeElement, BeanModel> entry : result.beans().entrySet()) {
            TypeElement type = entry.getKey();
            BeanModel bm = entry.getValue();

            // Type must be public and not abstract
            if (!type.getModifiers().contains(Modifier.PUBLIC)) {
                error(type, "Bean type must be public.");
            }
            if (type.getModifiers().contains(Modifier.ABSTRACT)) {
                error(type, "Bean type must not be abstract.");
            }

            // @Inject constructor: at most one
            if (bm.constructorInjections().size() > 1) {
                error(type, "Multiple @Inject constructors found. Only one is allowed.");
            }

            // If no @Inject constructor, require public no-args constructor
            if (bm.constructorInjections().isEmpty()) {
                boolean hasPublicNoArgs = type.getEnclosedElements().stream()
                        .filter(e -> e.getKind() == ElementKind.CONSTRUCTOR)
                        .map(e -> (ExecutableElement) e)
                        .anyMatch(m -> m.getParameters().isEmpty() && m.getModifiers().contains(Modifier.PUBLIC));
                if (!hasPublicNoArgs) {
                    error(type, "No @Inject constructor and no public no-args constructor found.");
                }
            }

            // @Inject fields must not be private or final
            bm.fieldInjections().forEach(ip -> {
                VariableElement f = ip.field();
                if (f.getModifiers().contains(Modifier.PRIVATE)) {
                    error(type, "Field @" + DiAnnotationSet.INJECT + " must not be private: " + f.getSimpleName());
                }
                if (f.getModifiers().contains(Modifier.FINAL)) {
                    error(type, "Field @" + DiAnnotationSet.INJECT + " must not be final: " + f.getSimpleName());
                }
            });

            // @PostConstruct: void, no params, not static
            checkLifecycleMethods(type, bm.postConstructMethods(), "@PostConstruct");

            // @PreDestroy: void, no params, not static
            checkLifecycleMethods(type, bm.preDestroyMethods(), "@PreDestroy");
        }

        // NOTE: Additional checks (qualifiers, primary conflicts, cycles) can be added
        // incrementally once we collect contract graphs.
    }

    private void checkLifecycleMethods(TypeElement type, List<ExecutableElement> methods, String label) {
        for (ExecutableElement m : methods) {
            if (!m.getParameters().isEmpty()) {
                error(type, label + " method must have no parameters: " + m.getSimpleName());
            }
            if (m.getModifiers().contains(Modifier.STATIC)) {
                error(type, label + " method must not be static: " + m.getSimpleName());
            }
            if (m.getReturnType().getKind() != javax.lang.model.type.TypeKind.VOID) {
                error(type, label + " method must return void: " + m.getSimpleName());
            }
        }
    }

    private void error(TypeElement type, String msg) {
        env.error(type.getQualifiedName() + ": " + msg);
    }
}
