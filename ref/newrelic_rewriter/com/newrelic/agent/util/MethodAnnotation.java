// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.util;

import java.util.Map;

public interface MethodAnnotation
{
    String getMethodName();
    
    String getMethodDesc();
    
    String getClassName();
    
    String getName();
    
    Map<String, Object> getAttributes();
}
