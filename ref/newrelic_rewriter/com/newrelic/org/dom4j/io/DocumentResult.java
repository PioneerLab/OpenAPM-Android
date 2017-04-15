// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import com.newrelic.org.dom4j.Document;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import javax.xml.transform.sax.SAXResult;

public class DocumentResult extends SAXResult
{
    private SAXContentHandler contentHandler;
    
    public DocumentResult() {
        this(new SAXContentHandler());
    }
    
    public DocumentResult(final SAXContentHandler contentHandler) {
        super.setHandler(this.contentHandler = contentHandler);
        super.setLexicalHandler(this.contentHandler);
    }
    
    public Document getDocument() {
        return this.contentHandler.getDocument();
    }
    
    public void setHandler(final ContentHandler handler) {
        if (handler instanceof SAXContentHandler) {
            super.setHandler(this.contentHandler = (SAXContentHandler)handler);
        }
    }
    
    public void setLexicalHandler(final LexicalHandler handler) {
        if (handler instanceof SAXContentHandler) {
            super.setLexicalHandler(this.contentHandler = (SAXContentHandler)handler);
        }
    }
}
