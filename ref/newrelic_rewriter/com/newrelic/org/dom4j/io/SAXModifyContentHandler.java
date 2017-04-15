// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import com.newrelic.org.dom4j.Element;
import java.io.IOException;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.newrelic.org.dom4j.ElementHandler;
import com.newrelic.org.dom4j.DocumentFactory;

class SAXModifyContentHandler extends SAXContentHandler
{
    private XMLWriter xmlWriter;
    
    public SAXModifyContentHandler() {
    }
    
    public SAXModifyContentHandler(final DocumentFactory documentFactory) {
        super(documentFactory);
    }
    
    public SAXModifyContentHandler(final DocumentFactory documentFactory, final ElementHandler elementHandler) {
        super(documentFactory, elementHandler);
    }
    
    public SAXModifyContentHandler(final DocumentFactory documentFactory, final ElementHandler elementHandler, final ElementStack elementStack) {
        super(documentFactory, elementHandler, elementStack);
    }
    
    public void setXMLWriter(final XMLWriter writer) {
        this.xmlWriter = writer;
    }
    
    public void startCDATA() throws SAXException {
        super.startCDATA();
        if (!this.activeHandlers() && this.xmlWriter != null) {
            this.xmlWriter.startCDATA();
        }
    }
    
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        super.startDTD(name, publicId, systemId);
        if (this.xmlWriter != null) {
            this.xmlWriter.startDTD(name, publicId, systemId);
        }
    }
    
    public void endDTD() throws SAXException {
        super.endDTD();
        if (this.xmlWriter != null) {
            this.xmlWriter.endDTD();
        }
    }
    
    public void comment(final char[] characters, final int parm2, final int parm3) throws SAXException {
        super.comment(characters, parm2, parm3);
        if (!this.activeHandlers() && this.xmlWriter != null) {
            this.xmlWriter.comment(characters, parm2, parm3);
        }
    }
    
    public void startEntity(final String name) throws SAXException {
        super.startEntity(name);
        if (this.xmlWriter != null) {
            this.xmlWriter.startEntity(name);
        }
    }
    
    public void endCDATA() throws SAXException {
        super.endCDATA();
        if (!this.activeHandlers() && this.xmlWriter != null) {
            this.xmlWriter.endCDATA();
        }
    }
    
    public void endEntity(final String name) throws SAXException {
        super.endEntity(name);
        if (this.xmlWriter != null) {
            this.xmlWriter.endEntity(name);
        }
    }
    
    public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notation) throws SAXException {
        super.unparsedEntityDecl(name, publicId, systemId, notation);
        if (!this.activeHandlers() && this.xmlWriter != null) {
            this.xmlWriter.unparsedEntityDecl(name, publicId, systemId, notation);
        }
    }
    
    public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {
        super.notationDecl(name, publicId, systemId);
        if (this.xmlWriter != null) {
            this.xmlWriter.notationDecl(name, publicId, systemId);
        }
    }
    
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);
        if (!this.activeHandlers() && this.xmlWriter != null) {
            this.xmlWriter.startElement(uri, localName, qName, atts);
        }
    }
    
    public void startDocument() throws SAXException {
        super.startDocument();
        if (this.xmlWriter != null) {
            this.xmlWriter.startDocument();
        }
    }
    
    public void ignorableWhitespace(final char[] parm1, final int parm2, final int parm3) throws SAXException {
        super.ignorableWhitespace(parm1, parm2, parm3);
        if (!this.activeHandlers() && this.xmlWriter != null) {
            this.xmlWriter.ignorableWhitespace(parm1, parm2, parm3);
        }
    }
    
    public void processingInstruction(final String target, final String data) throws SAXException {
        super.processingInstruction(target, data);
        if (!this.activeHandlers() && this.xmlWriter != null) {
            this.xmlWriter.processingInstruction(target, data);
        }
    }
    
    public void setDocumentLocator(final Locator locator) {
        super.setDocumentLocator(locator);
        if (this.xmlWriter != null) {
            this.xmlWriter.setDocumentLocator(locator);
        }
    }
    
    public void skippedEntity(final String name) throws SAXException {
        super.skippedEntity(name);
        if (!this.activeHandlers() && this.xmlWriter != null) {
            this.xmlWriter.skippedEntity(name);
        }
    }
    
    public void endDocument() throws SAXException {
        super.endDocument();
        if (this.xmlWriter != null) {
            this.xmlWriter.endDocument();
        }
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        super.startPrefixMapping(prefix, uri);
        if (this.xmlWriter != null) {
            this.xmlWriter.startPrefixMapping(prefix, uri);
        }
    }
    
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        final ElementHandler currentHandler = this.getElementStack().getDispatchHandler().getHandler(this.getElementStack().getPath());
        super.endElement(uri, localName, qName);
        if (!this.activeHandlers() && this.xmlWriter != null) {
            if (currentHandler == null) {
                this.xmlWriter.endElement(uri, localName, qName);
            }
            else if (currentHandler instanceof SAXModifyElementHandler) {
                final SAXModifyElementHandler modifyHandler = (SAXModifyElementHandler)currentHandler;
                final Element modifiedElement = modifyHandler.getModifiedElement();
                try {
                    this.xmlWriter.write(modifiedElement);
                }
                catch (IOException ex) {
                    throw new SAXModifyException(ex);
                }
            }
        }
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        super.endPrefixMapping(prefix);
        if (this.xmlWriter != null) {
            this.xmlWriter.endPrefixMapping(prefix);
        }
    }
    
    public void characters(final char[] parm1, final int parm2, final int parm3) throws SAXException {
        super.characters(parm1, parm2, parm3);
        if (!this.activeHandlers() && this.xmlWriter != null) {
            this.xmlWriter.characters(parm1, parm2, parm3);
        }
    }
    
    protected XMLWriter getXMLWriter() {
        return this.xmlWriter;
    }
    
    private boolean activeHandlers() {
        final DispatchHandler handler = this.getElementStack().getDispatchHandler();
        return handler.getActiveHandlerCount() > 0;
    }
}
