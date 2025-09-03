package org.neuralcoder.teleflux.di.processor.env;

import com.squareup.javapoet.JavaFile;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Lightweight facade over ProcessingEnvironment.
 */
public final class ProcessorEnv {
    private final ProcessingEnvironment pe;
    public final Messager messager;
    public final Filer filer;
    public final Elements elements;
    public final Types types;
    public final ProcessorOptions options;

    public ProcessorEnv(ProcessingEnvironment pe) {
        this.pe = pe;
        this.messager = pe.getMessager();
        this.filer = pe.getFiler();
        this.elements = pe.getElementUtils();
        this.types = pe.getTypeUtils();
        this.options = new ProcessorOptions(pe.getOptions());
    }

    public void note(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    public void warn(String msg) {
        messager.printMessage(Diagnostic.Kind.WARNING, msg);
    }

    public void error(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

    public void write(JavaFile file) {
        try {
            file.writeTo(filer);
        } catch (Exception e) {
            error("Failed to write file: " + e.getMessage());
        }
    }
}
