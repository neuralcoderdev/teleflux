package org.neuralcoder.teleflux.di.processor.spi;

/**
 * Extension point for out-of-tree processors to tap into Teleflux DI codegen.
 * Not used yet; reserved for future customization.
 */
public interface ProcessorHook {
    void onBeforeWrite();
    void onAfterWrite();
}
