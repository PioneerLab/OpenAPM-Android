// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

import org.xml.sax.EntityResolver;
import java.util.Map;

public interface Document extends Branch
{
    Element getRootElement();
    
    void setRootElement(final Element p0);
    
    Document addComment(final String p0);
    
    Document addProcessingInstruction(final String p0, final String p1);
    
    Document addProcessingInstruction(final String p0, final Map p1);
    
    Document addDocType(final String p0, final String p1, final String p2);
    
    DocumentType getDocType();
    
    void setDocType(final DocumentType p0);
    
    EntityResolver getEntityResolver();
    
    void setEntityResolver(final EntityResolver p0);
    
    String getXMLEncoding();
    
    void setXMLEncoding(final String p0);
}
