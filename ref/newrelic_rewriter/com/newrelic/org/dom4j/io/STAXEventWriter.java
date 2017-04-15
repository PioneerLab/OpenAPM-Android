// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.StartDocument;
import java.io.StringWriter;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.namespace.QName;
import java.util.Iterator;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import com.newrelic.org.dom4j.Branch;
import com.newrelic.org.dom4j.DocumentType;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Entity;
import com.newrelic.org.dom4j.ProcessingInstruction;
import com.newrelic.org.dom4j.CDATA;
import com.newrelic.org.dom4j.Comment;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.Attribute;
import com.newrelic.org.dom4j.Text;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Node;
import java.io.OutputStream;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.File;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.util.XMLEventConsumer;

public class STAXEventWriter
{
    private XMLEventConsumer consumer;
    private XMLEventFactory factory;
    private XMLOutputFactory outputFactory;
    
    public STAXEventWriter() {
        this.factory = XMLEventFactory.newInstance();
        this.outputFactory = XMLOutputFactory.newInstance();
    }
    
    public STAXEventWriter(final File file) throws XMLStreamException, IOException {
        this.factory = XMLEventFactory.newInstance();
        this.outputFactory = XMLOutputFactory.newInstance();
        this.consumer = this.outputFactory.createXMLEventWriter(new FileWriter(file));
    }
    
    public STAXEventWriter(final Writer writer) throws XMLStreamException {
        this.factory = XMLEventFactory.newInstance();
        this.outputFactory = XMLOutputFactory.newInstance();
        this.consumer = this.outputFactory.createXMLEventWriter(writer);
    }
    
    public STAXEventWriter(final OutputStream stream) throws XMLStreamException {
        this.factory = XMLEventFactory.newInstance();
        this.outputFactory = XMLOutputFactory.newInstance();
        this.consumer = this.outputFactory.createXMLEventWriter(stream);
    }
    
    public STAXEventWriter(final XMLEventConsumer consumer) {
        this.factory = XMLEventFactory.newInstance();
        this.outputFactory = XMLOutputFactory.newInstance();
        this.consumer = consumer;
    }
    
    public XMLEventConsumer getConsumer() {
        return this.consumer;
    }
    
    public void setConsumer(final XMLEventConsumer consumer) {
        this.consumer = consumer;
    }
    
    public XMLEventFactory getEventFactory() {
        return this.factory;
    }
    
    public void setEventFactory(final XMLEventFactory eventFactory) {
        this.factory = eventFactory;
    }
    
    public void writeNode(final Node n) throws XMLStreamException {
        switch (n.getNodeType()) {
            case 1: {
                this.writeElement((Element)n);
                break;
            }
            case 3: {
                this.writeText((Text)n);
                break;
            }
            case 2: {
                this.writeAttribute((Attribute)n);
                break;
            }
            case 13: {
                this.writeNamespace((Namespace)n);
                break;
            }
            case 8: {
                this.writeComment((Comment)n);
                break;
            }
            case 4: {
                this.writeCDATA((CDATA)n);
                break;
            }
            case 7: {
                this.writeProcessingInstruction((ProcessingInstruction)n);
                break;
            }
            case 5: {
                this.writeEntity((Entity)n);
                break;
            }
            case 9: {
                this.writeDocument((Document)n);
                break;
            }
            case 10: {
                this.writeDocumentType((DocumentType)n);
                break;
            }
            default: {
                throw new XMLStreamException("Unsupported DOM4J Node: " + n);
            }
        }
    }
    
    public void writeChildNodes(final Branch branch) throws XMLStreamException {
        for (int i = 0, s = branch.nodeCount(); i < s; ++i) {
            final Node n = branch.node(i);
            this.writeNode(n);
        }
    }
    
    public void writeElement(final Element elem) throws XMLStreamException {
        this.consumer.add(this.createStartElement(elem));
        this.writeChildNodes(elem);
        this.consumer.add(this.createEndElement(elem));
    }
    
    public StartElement createStartElement(final Element elem) {
        final QName tagName = this.createQName(elem.getQName());
        final Iterator attrIter = new AttributeIterator(elem.attributeIterator());
        final Iterator nsIter = new NamespaceIterator(elem.declaredNamespaces().iterator());
        return this.factory.createStartElement(tagName, attrIter, nsIter);
    }
    
    public EndElement createEndElement(final Element elem) {
        final QName tagName = this.createQName(elem.getQName());
        final Iterator nsIter = new NamespaceIterator(elem.declaredNamespaces().iterator());
        return this.factory.createEndElement(tagName, nsIter);
    }
    
    public void writeAttribute(final Attribute attr) throws XMLStreamException {
        this.consumer.add(this.createAttribute(attr));
    }
    
    public javax.xml.stream.events.Attribute createAttribute(final Attribute attr) {
        final QName attrName = this.createQName(attr.getQName());
        final String value = attr.getValue();
        return this.factory.createAttribute(attrName, value);
    }
    
    public void writeNamespace(final Namespace ns) throws XMLStreamException {
        this.consumer.add(this.createNamespace(ns));
    }
    
    public javax.xml.stream.events.Namespace createNamespace(final Namespace ns) {
        final String prefix = ns.getPrefix();
        final String uri = ns.getURI();
        return this.factory.createNamespace(prefix, uri);
    }
    
    public void writeText(final Text text) throws XMLStreamException {
        this.consumer.add(this.createCharacters(text));
    }
    
    public Characters createCharacters(final Text text) {
        return this.factory.createCharacters(text.getText());
    }
    
    public void writeCDATA(final CDATA cdata) throws XMLStreamException {
        this.consumer.add(this.createCharacters(cdata));
    }
    
    public Characters createCharacters(final CDATA cdata) {
        return this.factory.createCData(cdata.getText());
    }
    
    public void writeComment(final Comment comment) throws XMLStreamException {
        this.consumer.add(this.createComment(comment));
    }
    
    public javax.xml.stream.events.Comment createComment(final Comment comment) {
        return this.factory.createComment(comment.getText());
    }
    
    public void writeProcessingInstruction(final ProcessingInstruction pi) throws XMLStreamException {
        this.consumer.add(this.createProcessingInstruction(pi));
    }
    
    public javax.xml.stream.events.ProcessingInstruction createProcessingInstruction(final ProcessingInstruction pi) {
        final String target = pi.getTarget();
        final String data = pi.getText();
        return this.factory.createProcessingInstruction(target, data);
    }
    
    public void writeEntity(final Entity entity) throws XMLStreamException {
        this.consumer.add(this.createEntityReference(entity));
    }
    
    private EntityReference createEntityReference(final Entity entity) {
        return this.factory.createEntityReference(entity.getName(), null);
    }
    
    public void writeDocumentType(final DocumentType docType) throws XMLStreamException {
        this.consumer.add(this.createDTD(docType));
    }
    
    public DTD createDTD(final DocumentType docType) {
        final StringWriter decl = new StringWriter();
        try {
            docType.write(decl);
        }
        catch (IOException e) {
            throw new RuntimeException("Error writing DTD", e);
        }
        return this.factory.createDTD(decl.toString());
    }
    
    public void writeDocument(final Document doc) throws XMLStreamException {
        this.consumer.add(this.createStartDocument(doc));
        this.writeChildNodes(doc);
        this.consumer.add(this.createEndDocument(doc));
    }
    
    public StartDocument createStartDocument(final Document doc) {
        final String encoding = doc.getXMLEncoding();
        if (encoding != null) {
            return this.factory.createStartDocument(encoding);
        }
        return this.factory.createStartDocument();
    }
    
    public EndDocument createEndDocument(final Document doc) {
        return this.factory.createEndDocument();
    }
    
    public QName createQName(final com.newrelic.org.dom4j.QName qname) {
        return new QName(qname.getNamespaceURI(), qname.getName(), qname.getNamespacePrefix());
    }
    
    private class AttributeIterator implements Iterator
    {
        private Iterator iter;
        
        public AttributeIterator(final Iterator iter) {
            this.iter = iter;
        }
        
        public boolean hasNext() {
            return this.iter.hasNext();
        }
        
        public Object next() {
            final Attribute attr = this.iter.next();
            final QName attrName = STAXEventWriter.this.createQName(attr.getQName());
            final String value = attr.getValue();
            return STAXEventWriter.this.factory.createAttribute(attrName, value);
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private class NamespaceIterator implements Iterator
    {
        private Iterator iter;
        
        public NamespaceIterator(final Iterator iter) {
            this.iter = iter;
        }
        
        public boolean hasNext() {
            return this.iter.hasNext();
        }
        
        public Object next() {
            final Namespace ns = this.iter.next();
            final String prefix = ns.getPrefix();
            final String nsURI = ns.getURI();
            return STAXEventWriter.this.factory.createNamespace(prefix, nsURI);
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
