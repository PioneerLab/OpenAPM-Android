// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.jaxb;

import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.io.ElementModifier;
import java.util.Iterator;
import java.util.Map;
import java.io.Writer;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.URL;
import org.xml.sax.InputSource;
import java.io.Reader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.io.IOException;
import com.newrelic.org.dom4j.DocumentException;
import com.newrelic.org.dom4j.Document;
import java.io.File;
import java.util.HashMap;
import com.newrelic.org.dom4j.io.OutputFormat;
import com.newrelic.org.dom4j.io.XMLWriter;
import com.newrelic.org.dom4j.io.SAXModifier;

public class JAXBModifier extends JAXBSupport
{
    private SAXModifier modifier;
    private XMLWriter xmlWriter;
    private boolean pruneElements;
    private OutputFormat outputFormat;
    private HashMap modifiers;
    
    public JAXBModifier(final String contextPath) {
        super(contextPath);
        this.modifiers = new HashMap();
        this.outputFormat = new OutputFormat();
    }
    
    public JAXBModifier(final String contextPath, final ClassLoader classloader) {
        super(contextPath, classloader);
        this.modifiers = new HashMap();
        this.outputFormat = new OutputFormat();
    }
    
    public JAXBModifier(final String contextPath, final OutputFormat outputFormat) {
        super(contextPath);
        this.modifiers = new HashMap();
        this.outputFormat = outputFormat;
    }
    
    public JAXBModifier(final String contextPath, final ClassLoader classloader, final OutputFormat outputFormat) {
        super(contextPath, classloader);
        this.modifiers = new HashMap();
        this.outputFormat = outputFormat;
    }
    
    public Document modify(final File source) throws DocumentException, IOException {
        return this.installModifier().modify(source);
    }
    
    public Document modify(final File source, final Charset charset) throws DocumentException, IOException {
        try {
            final Reader reader = new InputStreamReader(new FileInputStream(source), charset);
            return this.installModifier().modify(reader);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
        catch (FileNotFoundException ex2) {
            throw new DocumentException(ex2.getMessage(), ex2);
        }
    }
    
    public Document modify(final InputSource source) throws DocumentException, IOException {
        try {
            return this.installModifier().modify(source);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document modify(final InputStream source) throws DocumentException, IOException {
        try {
            return this.installModifier().modify(source);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document modify(final InputStream source, final String systemId) throws DocumentException, IOException {
        try {
            return this.installModifier().modify(source);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document modify(final Reader r) throws DocumentException, IOException {
        try {
            return this.installModifier().modify(r);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document modify(final Reader source, final String systemId) throws DocumentException, IOException {
        try {
            return this.installModifier().modify(source);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document modify(final String url) throws DocumentException, IOException {
        try {
            return this.installModifier().modify(url);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document modify(final URL source) throws DocumentException, IOException {
        try {
            return this.installModifier().modify(source);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public void setOutput(final File file) throws IOException {
        this.createXMLWriter().setOutputStream(new FileOutputStream(file));
    }
    
    public void setOutput(final OutputStream outputStream) throws IOException {
        this.createXMLWriter().setOutputStream(outputStream);
    }
    
    public void setOutput(final Writer writer) throws IOException {
        this.createXMLWriter().setWriter(writer);
    }
    
    public void addObjectModifier(final String path, final JAXBObjectModifier mod) {
        this.modifiers.put(path, mod);
    }
    
    public void removeObjectModifier(final String path) {
        this.modifiers.remove(path);
        this.getModifier().removeModifier(path);
    }
    
    public void resetObjectModifiers() {
        this.modifiers.clear();
        this.getModifier().resetModifiers();
    }
    
    public boolean isPruneElements() {
        return this.pruneElements;
    }
    
    public void setPruneElements(final boolean pruneElements) {
        this.pruneElements = pruneElements;
    }
    
    private SAXModifier installModifier() throws IOException {
        (this.modifier = new SAXModifier(this.isPruneElements())).resetModifiers();
        for (final Map.Entry entry : this.modifiers.entrySet()) {
            final ElementModifier mod = new JAXBElementModifier(this, entry.getValue());
            this.getModifier().addModifier(entry.getKey(), mod);
        }
        this.modifier.setXMLWriter(this.getXMLWriter());
        return this.modifier;
    }
    
    private SAXModifier getModifier() {
        if (this.modifier == null) {
            this.modifier = new SAXModifier(this.isPruneElements());
        }
        return this.modifier;
    }
    
    private XMLWriter getXMLWriter() {
        return this.xmlWriter;
    }
    
    private XMLWriter createXMLWriter() throws IOException {
        if (this.xmlWriter == null) {
            this.xmlWriter = new XMLWriter(this.outputFormat);
        }
        return this.xmlWriter;
    }
    
    private class JAXBElementModifier implements ElementModifier
    {
        private JAXBModifier jaxbModifier;
        private JAXBObjectModifier objectModifier;
        
        public JAXBElementModifier(final JAXBModifier jaxbModifier, final JAXBObjectModifier objectModifier) {
            this.jaxbModifier = jaxbModifier;
            this.objectModifier = objectModifier;
        }
        
        public Element modifyElement(final Element element) throws Exception {
            final javax.xml.bind.Element originalObject = this.jaxbModifier.unmarshal(element);
            final javax.xml.bind.Element modifiedObject = this.objectModifier.modifyObject(originalObject);
            return this.jaxbModifier.marshal(modifiedObject);
        }
    }
}
