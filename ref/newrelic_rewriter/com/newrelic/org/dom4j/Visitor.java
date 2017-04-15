// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

public interface Visitor
{
    void visit(final Document p0);
    
    void visit(final DocumentType p0);
    
    void visit(final Element p0);
    
    void visit(final Attribute p0);
    
    void visit(final CDATA p0);
    
    void visit(final Comment p0);
    
    void visit(final Entity p0);
    
    void visit(final Namespace p0);
    
    void visit(final ProcessingInstruction p0);
    
    void visit(final Text p0);
}
