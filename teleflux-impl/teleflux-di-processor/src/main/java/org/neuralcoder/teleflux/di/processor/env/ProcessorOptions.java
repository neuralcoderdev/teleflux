package org.neuralcoder.teleflux.di.processor.env;

import java.util.Map;

/** Compiler options for fine tuning codegen. */
public final class ProcessorOptions {
    public static final String OPTION_DEBUG = "teleflux.di.debug";
    public static final String OPTION_INDEX_CLASS = "teleflux.di.indexClass"; // default: org.neuralcoder.teleflux.di.generated.ComponentIndex
    public static final String OPTION_PACKAGE = "teleflux.di.pkg";            // default: org.neuralcoder.teleflux.di.generated

    public final boolean debug;
    public final String indexClass;
    public final String targetPackage;

    public ProcessorOptions(Map<String, String> opts) {
        this.debug = Boolean.parseBoolean(opts.getOrDefault(OPTION_DEBUG, "false"));
        this.indexClass = opts.getOrDefault(OPTION_INDEX_CLASS, "org.neuralcoder.teleflux.di.generated.ComponentIndex");
        this.targetPackage = opts.getOrDefault(OPTION_PACKAGE, "org.neuralcoder.teleflux.di.generated");
    }
}
