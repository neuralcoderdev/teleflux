package org.neuralcoder.teleflux.di.processor.generate;

import com.squareup.javapoet.*;
import org.neuralcoder.teleflux.di.api.BeanFactory;
import org.neuralcoder.teleflux.di.api.BeanKey;
import org.neuralcoder.teleflux.di.api.Provider;
import org.neuralcoder.teleflux.di.processor.env.ProcessorEnv;
import org.neuralcoder.teleflux.di.processor.model.BeanModel;
import org.neuralcoder.teleflux.di.processor.model.InjectionPointModel;
import org.neuralcoder.teleflux.di.processor.scan.QualifierUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Emits factory classes for DI-managed beans.
 * Supports:
 *  - Constructor and field @Inject
 *  - Provider<T> parameters
 *  - List<T> / Set<T> collection injection
 *  - Qualifiers on parameters/fields (meta-qualifier aware)
 *  - @Lazy on parameters/fields (bf.lazy(...) for interfaces)
 *  - @PostConstruct invocation
 */
public final class GeneratedFactoryWriter {
    private final ProcessorEnv env;

    public GeneratedFactoryWriter(ProcessorEnv env) { this.env = env; }

    public List<JavaFile> writeAll(org.neuralcoder.teleflux.di.processor.scan.AnnotationScanner.ScanResult scan) {
        var out = new ArrayList<JavaFile>();
        for (BeanModel bm : scan.beans().values()) {
            out.add(writeFactoryFor(bm));
        }
        return out;
    }

    private JavaFile writeFactoryFor(BeanModel model) {
        String factorySimple = model.simpleName + "_Factory";
        ClassName beanType = ClassName.get(model.packageName, model.simpleName);
        ClassName providerIface = ClassName.get(Provider.class);

        FieldSpec bfField = FieldSpec.builder(ClassName.get(BeanFactory.class), "factory",
                Modifier.PRIVATE, Modifier.FINAL).build();

        MethodSpec ctor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ClassName.get(BeanFactory.class), "factory")
                .addStatement("this.factory = factory")
                .build();

        MethodSpec of = MethodSpec.methodBuilder("of")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get(model.packageName, factorySimple))
                .addParameter(ClassName.get(BeanFactory.class), "factory")
                .addStatement("return new $L(factory)", factorySimple)
                .build();

        MethodSpec get = MethodSpec.methodBuilder("get")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(beanType)
                .addStatement("return create()")
                .build();

        // create()
        MethodSpec.Builder create = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .returns(beanType);

        if (!model.constructorInjections().isEmpty()) {
            ExecutableElement ctorEl = model.constructorInjections().get(0).constructor();
            List<CodeBlock> args = new ArrayList<>();
            for (VariableElement p : ctorEl.getParameters()) {
                var q = QualifierUtils.findQualifierOn(p);
                boolean lazy = hasLazy(p);
                args.add(resolveExpr(p.asType(), q, lazy));
            }
            create.addStatement("$T instance = new $T($L)", beanType, beanType, joinArgs(args));
        } else {
            create.addStatement("$T instance = new $T()", beanType, beanType);
        }

        // injectFields(instance)
        MethodSpec.Builder injectFields = MethodSpec.methodBuilder("injectFields")
                .addModifiers(Modifier.PRIVATE)
                .returns(TypeName.VOID)
                .addParameter(beanType, "instance");

        for (InjectionPointModel ip : model.fieldInjections()) {
            TypeMirror ft = ip.field().asType();
            String name = ip.field().getSimpleName().toString();
            var q = QualifierUtils.findQualifierOn(ip.field());
            boolean lazy = hasLazy(ip.field());
            injectFields.addStatement("instance.$L = $L", name, resolveExpr(ft, q, lazy));
        }

        // invokePostConstruct(instance)
        MethodSpec.Builder post = MethodSpec.methodBuilder("invokePostConstruct")
                .addModifiers(Modifier.PRIVATE)
                .returns(TypeName.VOID)
                .addParameter(beanType, "instance");
        for (ExecutableElement m : model.postConstructMethods()) {
            post.addStatement("instance.$L()", m.getSimpleName().toString());
        }

        create.addStatement("injectFields(instance)")
                .addStatement("invokePostConstruct(instance)")
                .addStatement("return instance");

        TypeSpec spec = TypeSpec.classBuilder(factorySimple)
                .addJavadoc("Generated factory for {@code $L}.\n", model.simpleName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(providerIface, beanType))
                .addField(bfField)
                .addMethod(ctor)
                .addMethod(of)
                .addMethod(get)
                .addMethod(create.build())
                .addMethod(injectFields.build())
                .addMethod(post.build())
                .build();

        return JavaFile.builder(model.packageName, spec).build();
    }

    private CodeBlock resolveExpr(TypeMirror tm, Optional<String> qualifier) {
        return resolveExpr(tm, qualifier, false);
    }

    /**
     * Build expression for injection, considering Provider/List/Set/@Lazy/qualifier.
     */
    private CodeBlock resolveExpr(TypeMirror tm, Optional<String> qualifier, boolean lazy) {
        if (isProvider(tm)) {
            TypeMirror arg = typeArg((DeclaredType) tm, 0);
            // Note: qualifier is ignored at provider level (kept as before)
            return CodeBlock.of("factory.provider($T.class)", TypeName.get(arg));
        }
        if (isList(tm)) {
            TypeMirror arg = typeArg((DeclaredType) tm, 0);
            return CodeBlock.of("factory.getAll($T.class)", TypeName.get(arg));
        }
        if (isSet(tm)) {
            TypeMirror arg = typeArg((DeclaredType) tm, 0);
            return CodeBlock.of("new java.util.HashSet<>(factory.getAll($T.class))", TypeName.get(arg));
        }

        // plain T (maybe @Lazy, maybe qualified)
        if (lazy) {
            // генерируем bf.lazy(...) — работает только для интерфейсов; иначе взорвется в рантайме (так и задумано)
            return qualifier
                    .map(q -> CodeBlock.of("factory.lazy($T.of($T.class, $S))", ClassName.get(BeanKey.class), TypeName.get(tm), q))
                    .orElse(CodeBlock.of("factory.lazy($T.class)", TypeName.get(tm)));
        }

        return qualifier
                .map(q -> CodeBlock.of("factory.get($T.of($T.class, $S))", ClassName.get(BeanKey.class), TypeName.get(tm), q))
                .orElse(CodeBlock.of("factory.get($T.class)", TypeName.get(tm)));
    }

    private static boolean isProvider(TypeMirror tm) {
        return tm instanceof DeclaredType dt && dt.asElement().toString().equals(Provider.class.getCanonicalName());
    }
    private static boolean isList(TypeMirror tm) {
        return tm instanceof DeclaredType dt && dt.asElement().toString().equals(List.class.getCanonicalName());
    }
    private static boolean isSet(TypeMirror tm) {
        return tm instanceof DeclaredType dt && dt.asElement().toString().equals(Set.class.getCanonicalName());
    }
    private static TypeMirror typeArg(DeclaredType t, int idx) {
        return (TypeMirror) t.getTypeArguments().get(idx);
    }

    private static CodeBlock joinArgs(List<CodeBlock> args) {
        CodeBlock.Builder b = CodeBlock.builder();
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) b.add(", ");
            b.add(args.get(i));
        }
        return b.build();
    }

    /** True if element has @Lazy annotation (by canonical name). */
    private boolean hasLazy(javax.lang.model.element.Element el) {
        return el.getAnnotationMirrors().stream()
                .anyMatch(am -> am.getAnnotationType().toString()
                        .equals("org.neuralcoder.teleflux.di.api.annotations.Lazy"));
    }
}
