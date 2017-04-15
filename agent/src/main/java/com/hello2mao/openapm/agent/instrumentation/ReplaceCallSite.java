package com.hello2mao.openapm.agent.instrumentation;

public @interface ReplaceCallSite {

    boolean isStatic() default false;
    
    String scope() default "";
}
