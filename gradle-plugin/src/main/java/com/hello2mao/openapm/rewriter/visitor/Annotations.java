package com.hello2mao.openapm.rewriter.visitor;

public final class Annotations {

    public static final String INSTRUMENTED = "Lcom/hello2mao/openapm/agent/instrumentation/Instrumented;";
    
    public static boolean isOpenAPMAnnotation(String descriptor) {
        return descriptor.startsWith("Lcom/hello2mao/openapm/agent/instrumentation/");
    }
}
