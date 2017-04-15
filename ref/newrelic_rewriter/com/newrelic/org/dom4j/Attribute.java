// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

public interface Attribute extends Node
{
    QName getQName();
    
    Namespace getNamespace();
    
    void setNamespace(final Namespace p0);
    
    String getNamespacePrefix();
    
    String getNamespaceURI();
    
    String getQualifiedName();
    
    String getValue();
    
    void setValue(final String p0);
    
    Object getData();
    
    void setData(final Object p0);
}
