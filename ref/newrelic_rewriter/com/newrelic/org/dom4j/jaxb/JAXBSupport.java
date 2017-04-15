// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.jaxb;

import javax.xml.transform.Source;
import java.io.Reader;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import javax.xml.bind.JAXBException;
import org.w3c.dom.Node;
import com.newrelic.org.dom4j.dom.DOMDocument;
import javax.xml.bind.Element;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBContext;

abstract class JAXBSupport
{
    private String contextPath;
    private ClassLoader classloader;
    private JAXBContext jaxbContext;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    
    public JAXBSupport(final String contextPath) {
        this.contextPath = contextPath;
    }
    
    public JAXBSupport(final String contextPath, final ClassLoader classloader) {
        this.contextPath = contextPath;
        this.classloader = classloader;
    }
    
    protected com.newrelic.org.dom4j.Element marshal(final Element element) throws JAXBException {
        final DOMDocument doc = new DOMDocument();
        this.getMarshaller().marshal(element, doc);
        return doc.getRootElement();
    }
    
    protected Element unmarshal(final com.newrelic.org.dom4j.Element element) throws JAXBException {
        final Source source = new StreamSource(new StringReader(element.asXML()));
        return (Element)this.getUnmarshaller().unmarshal(source);
    }
    
    private Marshaller getMarshaller() throws JAXBException {
        if (this.marshaller == null) {
            this.marshaller = this.getContext().createMarshaller();
        }
        return this.marshaller;
    }
    
    private Unmarshaller getUnmarshaller() throws JAXBException {
        if (this.unmarshaller == null) {
            this.unmarshaller = this.getContext().createUnmarshaller();
        }
        return this.unmarshaller;
    }
    
    private JAXBContext getContext() throws JAXBException {
        if (this.jaxbContext == null) {
            if (this.classloader == null) {
                this.jaxbContext = JAXBContext.newInstance(this.contextPath);
            }
            else {
                this.jaxbContext = JAXBContext.newInstance(this.contextPath, this.classloader);
            }
        }
        return this.jaxbContext;
    }
}
