package com.hello2mao.openapm.agent.instrumentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
public @interface Trace {

    public static final String NULL = "";
    
    String metricName() default "";
    
    boolean skipTransactionTrace() default false;
    
    MetricCategory category() default MetricCategory.NONE;
}
