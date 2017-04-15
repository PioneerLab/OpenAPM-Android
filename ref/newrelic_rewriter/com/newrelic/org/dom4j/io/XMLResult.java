// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.transform.sax.SAXResult;

public class XMLResult extends SAXResult
{
    private XMLWriter xmlWriter;
    
    public XMLResult() {
        this(new XMLWriter());
    }
    
    public XMLResult(final Writer writer) {
        this(new XMLWriter(writer));
    }
    
    public XMLResult(final Writer writer, final OutputFormat format) {
        this(new XMLWriter(writer, format));
    }
    
    public XMLResult(final OutputStream out) throws UnsupportedEncodingException {
        this(new XMLWriter(out));
    }
    
    public XMLResult(final OutputStream out, final OutputFormat format) throws UnsupportedEncodingException {
        this(new XMLWriter(out, format));
    }
    
    public XMLResult(final XMLWriter xmlWriter) {
        super(xmlWriter);
        this.setLexicalHandler(this.xmlWriter = xmlWriter);
    }
    
    public XMLWriter getXMLWriter() {
        return this.xmlWriter;
    }
    
    public void setXMLWriter(final XMLWriter writer) {
        this.setHandler(this.xmlWriter = writer);
        this.setLexicalHandler(this.xmlWriter);
    }
    
    public ContentHandler getHandler() {
        return this.xmlWriter;
    }
    
    public LexicalHandler getLexicalHandler() {
        return this.xmlWriter;
    }
}
