// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

import java.util.Iterator;
import java.util.List;

public interface Branch extends Node
{
    Node node(final int p0) throws IndexOutOfBoundsException;
    
    int indexOf(final Node p0);
    
    int nodeCount();
    
    Element elementByID(final String p0);
    
    List content();
    
    Iterator nodeIterator();
    
    void setContent(final List p0);
    
    void appendContent(final Branch p0);
    
    void clearContent();
    
    List processingInstructions();
    
    List processingInstructions(final String p0);
    
    ProcessingInstruction processingInstruction(final String p0);
    
    void setProcessingInstructions(final List p0);
    
    Element addElement(final String p0);
    
    Element addElement(final QName p0);
    
    Element addElement(final String p0, final String p1);
    
    boolean removeProcessingInstruction(final String p0);
    
    void add(final Node p0);
    
    void add(final Comment p0);
    
    void add(final Element p0);
    
    void add(final ProcessingInstruction p0);
    
    boolean remove(final Node p0);
    
    boolean remove(final Comment p0);
    
    boolean remove(final Element p0);
    
    boolean remove(final ProcessingInstruction p0);
    
    void normalize();
}
