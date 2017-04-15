// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

public interface ElementPath
{
    int size();
    
    Element getElement(final int p0);
    
    String getPath();
    
    Element getCurrent();
    
    void addHandler(final String p0, final ElementHandler p1);
    
    void removeHandler(final String p0);
}
