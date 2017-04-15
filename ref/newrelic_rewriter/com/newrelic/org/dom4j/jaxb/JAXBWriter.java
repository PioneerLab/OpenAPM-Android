// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.jaxb;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Element;
import org.xml.sax.SAXException;
import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import com.newrelic.org.dom4j.io.OutputFormat;
import com.newrelic.org.dom4j.io.XMLWriter;

public class JAXBWriter extends JAXBSupport
{
    private XMLWriter xmlWriter;
    private OutputFormat outputFormat;
    
    public JAXBWriter(final String contextPath) {
        super(contextPath);
        this.outputFormat = new OutputFormat();
    }
    
    public JAXBWriter(final String contextPath, final OutputFormat outputFormat) {
        super(contextPath);
        this.outputFormat = outputFormat;
    }
    
    public JAXBWriter(final String contextPath, final ClassLoader classloader) {
        super(contextPath, classloader);
    }
    
    public JAXBWriter(final String contextPath, final ClassLoader classloader, final OutputFormat outputFormat) {
        super(contextPath, classloader);
        this.outputFormat = outputFormat;
    }
    
    public OutputFormat getOutputFormat() {
        return this.outputFormat;
    }
    
    public void setOutput(final File file) throws IOException {
        this.getWriter().setOutputStream(new FileOutputStream(file));
    }
    
    public void setOutput(final OutputStream outputStream) throws IOException {
        this.getWriter().setOutputStream(outputStream);
    }
    
    public void setOutput(final Writer writer) throws IOException {
        this.getWriter().setWriter(writer);
    }
    
    public void startDocument() throws IOException, SAXException {
        this.getWriter().startDocument();
    }
    
    public void endDocument() throws IOException, SAXException {
        this.getWriter().endDocument();
    }
    
    public void write(final Element jaxbObject) throws IOException, JAXBException {
        this.getWriter().write(this.marshal(jaxbObject));
    }
    
    public void writeClose(final Element jaxbObject) throws IOException, JAXBException {
        this.getWriter().writeClose(this.marshal(jaxbObject));
    }
    
    public void writeOpen(final Element jaxbObject) throws IOException, JAXBException {
        this.getWriter().writeOpen(this.marshal(jaxbObject));
    }
    
    public void writeElement(final com.newrelic.org.dom4j.Element element) throws IOException {
        this.getWriter().write(element);
    }
    
    public void writeCloseElement(final com.newrelic.org.dom4j.Element element) throws IOException {
        this.getWriter().writeClose(element);
    }
    
    public void writeOpenElement(final com.newrelic.org.dom4j.Element element) throws IOException {
        this.getWriter().writeOpen(element);
    }
    
    private XMLWriter getWriter() throws IOException {
        if (this.xmlWriter == null) {
            if (this.outputFormat != null) {
                this.xmlWriter = new XMLWriter(this.outputFormat);
            }
            else {
                this.xmlWriter = new XMLWriter();
            }
        }
        return this.xmlWriter;
    }
}
