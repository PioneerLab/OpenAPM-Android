// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import com.newrelic.org.dom4j.ElementHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import com.newrelic.org.dom4j.DocumentFactory;

class SAXModifyReader extends SAXReader
{
    private XMLWriter xmlWriter;
    private boolean pruneElements;
    
    public SAXModifyReader() {
    }
    
    public SAXModifyReader(final boolean validating) {
        super(validating);
    }
    
    public SAXModifyReader(final DocumentFactory factory) {
        super(factory);
    }
    
    public SAXModifyReader(final DocumentFactory factory, final boolean validating) {
        super(factory, validating);
    }
    
    public SAXModifyReader(final XMLReader xmlReader) {
        super(xmlReader);
    }
    
    public SAXModifyReader(final XMLReader xmlReader, final boolean validating) {
        super(xmlReader, validating);
    }
    
    public SAXModifyReader(final String xmlReaderClassName) throws SAXException {
        super(xmlReaderClassName);
    }
    
    public SAXModifyReader(final String xmlReaderClassName, final boolean validating) throws SAXException {
        super(xmlReaderClassName, validating);
    }
    
    public void setXMLWriter(final XMLWriter writer) {
        this.xmlWriter = writer;
    }
    
    public boolean isPruneElements() {
        return this.pruneElements;
    }
    
    public void setPruneElements(final boolean pruneElements) {
        this.pruneElements = pruneElements;
    }
    
    protected SAXContentHandler createContentHandler(final XMLReader reader) {
        final SAXModifyContentHandler handler = new SAXModifyContentHandler(this.getDocumentFactory(), this.getDispatchHandler());
        handler.setXMLWriter(this.xmlWriter);
        return handler;
    }
    
    protected XMLWriter getXMLWriter() {
        return this.xmlWriter;
    }
}
