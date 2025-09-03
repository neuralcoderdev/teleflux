package org.neuralcoder.teleflux.di.processor.generate;

import com.squareup.javapoet.*;
import org.neuralcoder.teleflux.di.api.BeanFactory;
import org.neuralcoder.teleflux.di.api.Provider;
import org.neuralcoder.teleflux.di.processor.env.ProcessorEnv;
import org.neuralcoder.teleflux.di.processor.model.BeanModel;
import org.neuralcoder.teleflux.di.processor.model.InjectionPointModel;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

/**
 * Emits factory classes for DI-managed beans.
 * These factories encapsulate constructor/field injections and lifecycle hooks.
 */
public final class GeneratedFactoryWriter {
    private final ProcessorEnv env;

    public GeneratedFactoryWriter(ProcessorEnv env) {
        this.env = env;
    }

    public List<JavaFile> writeAll(org.neuralcoder.teleflux.di.processor.scan.AnnotationScanner.ScanResult scan) {
        var out = new ArrayList<JavaFile>();
        for (BeanModel bm : scan.beans().values()) {
            out.add(writeFactoryFor(bm));
        }
        return out;
    }

    private JavaFile writeFactoryFor(BeanModel model) {
        String factorySimple = Names.factorySimple(model.simpleName);
        ClassName beanType = ClassName.get(model.packageName, model.simpleName);
        ClassName factoryIface = ClassName.get(Provider.class);

        FieldSpec bfField = FieldSpec.builder(ClassName.get(BeanFactory.class), "factory",
                Modifier.PRIVATE, Modifier.FINAL).build();

        // ctor
        MethodSpec ctor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ClassName.get(BeanFactory.class), "factory")
                .addStatement("this.factory = factory")
                .build();

        // static of(BeanFactory)
        MethodSpec of = MethodSpec.methodBuilder("of")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get(model.packageName, factorySimple))
                .addParameter(ClassName.get(BeanFactory.class), "factory")
                .addStatement("return new $L(factory)", factorySimple)
                .build();

        // get() -> create()
        MethodSpec get = MethodSpec.methodBuilder("get")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(beanType)
                .addStatement("return create()")
                .build();

        // create(): construct + injectFields + invokePostConstruct
        MethodSpec.Builder create = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .returns(beanType);

        // Construction
        if (!model.constructorInjections().isEmpty()) {
            // Use the single @Inject constructor (validated earlier)
            ExecutableElement ctorEl = model.constructorInjections().get(0).constructor();
            StringBuilder newExpr = new StringBuilder("new " + model.simpleName + "(");
            List<Object> args = new ArrayList<>();
            for (int i = 0; i < ctorEl.getParameters().size(); i++) {
                TypeMirror pt = ctorEl.getParameters().get(i).asType();
                newExpr.append("$T.class");
                args.add(TypeName.get(pt));
                if (i < ctorEl.getParameters().size() - 1) newExpr.append(", ");
            }
            newExpr.append(")");
            // resolve via factory.get(...)
            StringBuilder resolved = new StringBuilder();
            for (int i = 0; i < ctorEl.getParameters().size(); i++) {
                if (i > 0) resolved.append(", ");
                resolved.append("factory.get($T.class)");
            }
            create.addStatement("$T instance = " + newExpr.toString()
                            .replaceAll("\\$T.class(, )?", resolved.toString()),
                    args.toArray());
        } else {
            create.addStatement("$T instance = new $T()", beanType, beanType);
        }

        // injectFields(instance)
        create.addStatement("injectFields(instance)");
        // invokePostConstruct(instance)
        create.addStatement("invokePostConstruct(instance)");
        create.addStatement("return instance");

        // injectFields
        MethodSpec.Builder injectFields = MethodSpec.methodBuilder("injectFields")
                .addModifiers(Modifier.PRIVATE)
                .returns(TypeName.VOID)
                .addParameter(beanType, "instance");

        for (InjectionPointModel ip : model.fieldInjections()) {
            TypeMirror ft = ip.field().asType();
            String name = ip.field().getSimpleName().toString();
            injectFields.addStatement("instance.$L = factory.get($T.class)", name, TypeName.get(ft));
        }

        // invokePostConstruct
        MethodSpec.Builder post = MethodSpec.methodBuilder("invokePostConstruct")
                .addModifiers(Modifier.PRIVATE)
                .returns(TypeName.VOID)
                .addParameter(beanType, "instance");
        for (ExecutableElement m : model.postConstructMethods()) {
            post.addStatement("instance.$L()", m.getSimpleName().toString());
        }

        TypeSpec spec = TypeSpec.classBuilder(factorySimple)
                .addJavadoc("Generated factory for {@code $L}.\n", model.simpleName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(factoryIface, beanType))
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
}
