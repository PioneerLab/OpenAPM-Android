// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.util;

import java.util.Map;

public interface ClassAnnotation
{
    String getClassName();
    
    String getName();
    
    Map<String, Object> getAttributes();
}
