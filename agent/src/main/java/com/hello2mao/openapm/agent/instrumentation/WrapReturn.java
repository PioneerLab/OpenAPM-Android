package com.hello2mao.openapm.agent.instrumentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
public @interface WrapReturn {
    String className();
    
    String methodName();
    
    String methodDesc();
}
