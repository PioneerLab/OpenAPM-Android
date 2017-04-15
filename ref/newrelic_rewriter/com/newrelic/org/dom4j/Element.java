// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

import java.util.Iterator;
import java.util.Map;
import java.util.List;

public interface Element extends Branch
{
    QName getQName();
    
    void setQName(final QName p0);
    
    Namespace getNamespace();
    
    QName getQName(final String p0);
    
    Namespace getNamespaceForPrefix(final String p0);
    
    Namespace getNamespaceForURI(final String p0);
    
    List getNamespacesForURI(final String p0);
    
    String getNamespacePrefix();
    
    String getNamespaceURI();
    
    String getQualifiedName();
    
    List additionalNamespaces();
    
    List declaredNamespaces();
    
    Element addAttribute(final String p0, final String p1);
    
    Element addAttribute(final QName p0, final String p1);
    
    Element addComment(final String p0);
    
    Element addCDATA(final String p0);
    
    Element addEntity(final String p0, final String p1);
    
    Element addNamespace(final String p0, final String p1);
    
    Element addProcessingInstruction(final String p0, final String p1);
    
    Element addProcessingInstruction(final String p0, final Map p1);
    
    Element addText(final String p0);
    
    void add(final Attribute p0);
    
    void add(final CDATA p0);
    
    void add(final Entity p0);
    
    void add(final Text p0);
    
    void add(final Namespace p0);
    
    boolean remove(final Attribute p0);
    
    boolean remove(final CDATA p0);
    
    boolean remove(final Entity p0);
    
    boolean remove(final Namespace p0);
    
    boolean remove(final Text p0);
    
    String getText();
    
    String getTextTrim();
    
    String getStringValue();
    
    Object getData();
    
    void setData(final Object p0);
    
    List attributes();
    
    void setAttributes(final List p0);
    
    int attributeCount();
    
    Iterator attributeIterator();
    
    Attribute attribute(final int p0);
    
    Attribute attribute(final String p0);
    
    Attribute attribute(final QName p0);
    
    String attributeValue(final String p0);
    
    String attributeValue(final String p0, final String p1);
    
    String attributeValue(final QName p0);
    
    String attributeValue(final QName p0, final String p1);
    
    void setAttributeValue(final String p0, final String p1);
    
    void setAttributeValue(final QName p0, final String p1);
    
    Element element(final String p0);
    
    Element element(final QName p0);
    
    List elements();
    
    List elements(final String p0);
    
    List elements(final QName p0);
    
    Iterator elementIterator();
    
    Iterator elementIterator(final String p0);
    
    Iterator elementIterator(final QName p0);
    
    boolean isRootElement();
    
    boolean hasMixedContent();
    
    boolean isTextOnly();
    
    void appendAttributes(final Element p0);
    
    Element createCopy();
    
    Element createCopy(final String p0);
    
    Element createCopy(final QName p0);
    
    String elementText(final String p0);
    
    String elementText(final QName p0);
    
    String elementTextTrim(final String p0);
    
    String elementTextTrim(final QName p0);
    
    Node getXPathResult(final int p0);
}
