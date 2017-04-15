// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

import java.util.Map;

public interface ProcessingInstruction extends Node
{
    String getTarget();
    
    void setTarget(final String p0);
    
    String getText();
    
    String getValue(final String p0);
    
    Map getValues();
    
    void setValue(final String p0, final String p1);
    
    void setValues(final Map p0);
    
    boolean removeValue(final String p0);
}
