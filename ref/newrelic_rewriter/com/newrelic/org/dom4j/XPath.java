// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

import org.jaxen.VariableContext;
import java.util.Map;
import org.jaxen.NamespaceContext;
import org.jaxen.FunctionContext;
import java.util.List;

public interface XPath extends NodeFilter
{
    String getText();
    
    boolean matches(final Node p0);
    
    Object evaluate(final Object p0);
    
    Object selectObject(final Object p0);
    
    List selectNodes(final Object p0);
    
    List selectNodes(final Object p0, final XPath p1);
    
    List selectNodes(final Object p0, final XPath p1, final boolean p2);
    
    Node selectSingleNode(final Object p0);
    
    String valueOf(final Object p0);
    
    Number numberValueOf(final Object p0);
    
    boolean booleanValueOf(final Object p0);
    
    void sort(final List p0);
    
    void sort(final List p0, final boolean p1);
    
    FunctionContext getFunctionContext();
    
    void setFunctionContext(final FunctionContext p0);
    
    NamespaceContext getNamespaceContext();
    
    void setNamespaceContext(final NamespaceContext p0);
    
    void setNamespaceURIs(final Map p0);
    
    VariableContext getVariableContext();
    
    void setVariableContext(final VariableContext p0);
}
