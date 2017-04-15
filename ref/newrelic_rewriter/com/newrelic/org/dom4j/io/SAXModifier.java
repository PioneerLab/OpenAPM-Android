// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import java.util.Iterator;
import org.xml.sax.SAXException;
import com.newrelic.org.dom4j.ElementHandler;
import java.util.Map;
import com.newrelic.org.dom4j.DocumentFactory;
import java.net.URL;
import java.io.Reader;
import java.io.InputStream;
import org.xml.sax.InputSource;
import com.newrelic.org.dom4j.DocumentException;
import com.newrelic.org.dom4j.Document;
import java.io.File;
import java.util.HashMap;
import org.xml.sax.XMLReader;

public class SAXModifier
{
    private XMLWriter xmlWriter;
    private XMLReader xmlReader;
    private boolean pruneElements;
    private SAXModifyReader modifyReader;
    private HashMap modifiers;
    
    public SAXModifier() {
        this.modifiers = new HashMap();
    }
    
    public SAXModifier(final boolean pruneElements) {
        this.modifiers = new HashMap();
        this.pruneElements = pruneElements;
    }
    
    public SAXModifier(final XMLReader xmlReader) {
        this.modifiers = new HashMap();
        this.xmlReader = xmlReader;
    }
    
    public SAXModifier(final XMLReader xmlReader, final boolean pruneElements) {
        this.modifiers = new HashMap();
        this.xmlReader = xmlReader;
    }
    
    public Document modify(final File source) throws DocumentException {
        try {
            return this.installModifyReader().read(source);
        }
        catch (SAXModifyException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document modify(final InputSource source) throws DocumentException {
        try {
            return this.installModifyReader().read(source);
        }
        catch (SAXModifyException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document modify(final InputStream source) throws DocumentException {
        try {
            return this.installModifyReader().read(source);
        }
        catch (SAXModifyException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document modify(final InputStream source, final String systemId) throws DocumentException {
        try {
            return this.installModifyReader().read(source);
        }
        catch (SAXModifyException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document modify(final Reader source) throws DocumentException {
        try {
            return this.installModifyReader().read(source);
        }
        catch (SAXModifyException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document modify(final Reader source, final String systemId) throws DocumentException {
        try {
            return this.installModifyReader().read(source);
        }
        catch (SAXModifyException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document modify(final URL source) throws DocumentException {
        try {
            return this.installModifyReader().read(source);
        }
        catch (SAXModifyException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document modify(final String source) throws DocumentException {
        try {
            return this.installModifyReader().read(source);
        }
        catch (SAXModifyException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public void addModifier(final String path, final ElementModifier modifier) {
        this.modifiers.put(path, modifier);
    }
    
    public void resetModifiers() {
        this.modifiers.clear();
        this.getSAXModifyReader().resetHandlers();
    }
    
    public void removeModifier(final String path) {
        this.modifiers.remove(path);
        this.getSAXModifyReader().removeHandler(path);
    }
    
    public DocumentFactory getDocumentFactory() {
        return this.getSAXModifyReader().getDocumentFactory();
    }
    
    public void setDocumentFactory(final DocumentFactory factory) {
        this.getSAXModifyReader().setDocumentFactory(factory);
    }
    
    public XMLWriter getXMLWriter() {
        return this.xmlWriter;
    }
    
    public void setXMLWriter(final XMLWriter writer) {
        this.xmlWriter = writer;
    }
    
    public boolean isPruneElements() {
        return this.pruneElements;
    }
    
    private SAXReader installModifyReader() throws DocumentException {
        try {
            final SAXModifyReader reader = this.getSAXModifyReader();
            if (this.isPruneElements()) {
                this.modifyReader.setDispatchHandler(new PruningDispatchHandler());
            }
            reader.resetHandlers();
            for (final Map.Entry entry : this.modifiers.entrySet()) {
                final SAXModifyElementHandler handler = new SAXModifyElementHandler(entry.getValue());
                reader.addHandler(entry.getKey(), handler);
            }
            reader.setXMLWriter(this.getXMLWriter());
            reader.setXMLReader(this.getXMLReader());
            return reader;
        }
        catch (SAXException ex) {
            throw new DocumentException(ex.getMessage(), ex);
        }
    }
    
    private XMLReader getXMLReader() throws SAXException {
        if (this.xmlReader == null) {
            this.xmlReader = SAXHelper.createXMLReader(false);
        }
        return this.xmlReader;
    }
    
    private SAXModifyReader getSAXModifyReader() {
        if (this.modifyReader == null) {
            this.modifyReader = new SAXModifyReader();
        }
        return this.modifyReader;
    }
}
