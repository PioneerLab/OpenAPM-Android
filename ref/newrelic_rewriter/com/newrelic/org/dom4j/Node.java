// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

import java.util.List;
import java.io.IOException;
import java.io.Writer;

public interface Node extends Cloneable
{
    public static final short ANY_NODE = 0;
    public static final short ELEMENT_NODE = 1;
    public static final short ATTRIBUTE_NODE = 2;
    public static final short TEXT_NODE = 3;
    public static final short CDATA_SECTION_NODE = 4;
    public static final short ENTITY_REFERENCE_NODE = 5;
    public static final short PROCESSING_INSTRUCTION_NODE = 7;
    public static final short COMMENT_NODE = 8;
    public static final short DOCUMENT_NODE = 9;
    public static final short DOCUMENT_TYPE_NODE = 10;
    public static final short NAMESPACE_NODE = 13;
    public static final short UNKNOWN_NODE = 14;
    public static final short MAX_NODE_TYPE = 14;
    
    boolean supportsParent();
    
    Element getParent();
    
    void setParent(final Element p0);
    
    Document getDocument();
    
    void setDocument(final Document p0);
    
    boolean isReadOnly();
    
    boolean hasContent();
    
    String getName();
    
    void setName(final String p0);
    
    String getText();
    
    void setText(final String p0);
    
    String getStringValue();
    
    String getPath();
    
    String getPath(final Element p0);
    
    String getUniquePath();
    
    String getUniquePath(final Element p0);
    
    String asXML();
    
    void write(final Writer p0) throws IOException;
    
    short getNodeType();
    
    String getNodeTypeName();
    
    Node detach();
    
    List selectNodes(final String p0);
    
    Object selectObject(final String p0);
    
    List selectNodes(final String p0, final String p1);
    
    List selectNodes(final String p0, final String p1, final boolean p2);
    
    Node selectSingleNode(final String p0);
    
    String valueOf(final String p0);
    
    Number numberValueOf(final String p0);
    
    boolean matches(final String p0);
    
    XPath createXPath(final String p0) throws InvalidXPathException;
    
    Node asXPathResult(final Element p0);
    
    void accept(final Visitor p0);
    
    Object clone();
}
