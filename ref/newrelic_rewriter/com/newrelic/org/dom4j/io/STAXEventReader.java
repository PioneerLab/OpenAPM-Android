// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import java.util.Iterator;
import javax.xml.namespace.QName;
import com.newrelic.org.dom4j.ProcessingInstruction;
import javax.xml.stream.events.EntityReference;
import com.newrelic.org.dom4j.Entity;
import com.newrelic.org.dom4j.Comment;
import javax.xml.stream.events.Characters;
import com.newrelic.org.dom4j.CharacterData;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import com.newrelic.org.dom4j.Element;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import com.newrelic.org.dom4j.Node;
import javax.xml.stream.XMLEventReader;
import java.io.Reader;
import javax.xml.stream.XMLStreamException;
import com.newrelic.org.dom4j.Document;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import com.newrelic.org.dom4j.DocumentFactory;

public class STAXEventReader
{
    private DocumentFactory factory;
    private XMLInputFactory inputFactory;
    
    public STAXEventReader() {
        this.inputFactory = XMLInputFactory.newInstance();
        this.factory = DocumentFactory.getInstance();
    }
    
    public STAXEventReader(final DocumentFactory factory) {
        this.inputFactory = XMLInputFactory.newInstance();
        if (factory != null) {
            this.factory = factory;
        }
        else {
            this.factory = DocumentFactory.getInstance();
        }
    }
    
    public void setDocumentFactory(final DocumentFactory documentFactory) {
        if (documentFactory != null) {
            this.factory = documentFactory;
        }
        else {
            this.factory = DocumentFactory.getInstance();
        }
    }
    
    public Document readDocument(final InputStream is) throws XMLStreamException {
        return this.readDocument(is, null);
    }
    
    public Document readDocument(final Reader reader) throws XMLStreamException {
        return this.readDocument(reader, null);
    }
    
    public Document readDocument(final InputStream is, final String systemId) throws XMLStreamException {
        final XMLEventReader eventReader = this.inputFactory.createXMLEventReader(systemId, is);
        try {
            return this.readDocument(eventReader);
        }
        finally {
            eventReader.close();
        }
    }
    
    public Document readDocument(final Reader reader, final String systemId) throws XMLStreamException {
        final XMLEventReader eventReader = this.inputFactory.createXMLEventReader(systemId, reader);
        try {
            return this.readDocument(eventReader);
        }
        finally {
            eventReader.close();
        }
    }
    
    public Node readNode(final XMLEventReader reader) throws XMLStreamException {
        final XMLEvent event = reader.peek();
        if (event.isStartElement()) {
            return this.readElement(reader);
        }
        if (event.isCharacters()) {
            return this.readCharacters(reader);
        }
        if (event.isStartDocument()) {
            return this.readDocument(reader);
        }
        if (event.isProcessingInstruction()) {
            return this.readProcessingInstruction(reader);
        }
        if (event.isEntityReference()) {
            return this.readEntityReference(reader);
        }
        if (event.isAttribute()) {
            return this.readAttribute(reader);
        }
        if (event.isNamespace()) {
            return this.readNamespace(reader);
        }
        throw new XMLStreamException("Unsupported event: " + event);
    }
    
    public Document readDocument(final XMLEventReader reader) throws XMLStreamException {
        Document doc = null;
        while (reader.hasNext()) {
            final XMLEvent nextEvent = reader.peek();
            final int type = nextEvent.getEventType();
            switch (type) {
                case 7: {
                    final StartDocument event = (StartDocument)reader.nextEvent();
                    if (doc != null) {
                        final String msg = "Unexpected StartDocument event";
                        throw new XMLStreamException(msg, event.getLocation());
                    }
                    if (event.encodingSet()) {
                        final String encodingScheme = event.getCharacterEncodingScheme();
                        doc = this.factory.createDocument(encodingScheme);
                        continue;
                    }
                    doc = this.factory.createDocument();
                    continue;
                }
                case 4:
                case 6:
                case 8: {
                    reader.nextEvent();
                    continue;
                }
                default: {
                    if (doc == null) {
                        doc = this.factory.createDocument();
                    }
                    final Node n = this.readNode(reader);
                    doc.add(n);
                    continue;
                }
            }
        }
        return doc;
    }
    
    public Element readElement(final XMLEventReader eventReader) throws XMLStreamException {
        final XMLEvent event = eventReader.peek();
        if (event.isStartElement()) {
            final StartElement startTag = eventReader.nextEvent().asStartElement();
            final Element elem = this.createElement(startTag);
            while (eventReader.hasNext()) {
                final XMLEvent nextEvent = eventReader.peek();
                if (nextEvent.isEndElement()) {
                    final EndElement endElem = eventReader.nextEvent().asEndElement();
                    if (!endElem.getName().equals(startTag.getName())) {
                        throw new XMLStreamException("Expected " + startTag.getName() + " end-tag, but found" + endElem.getName());
                    }
                    return elem;
                }
                else {
                    final Node child = this.readNode(eventReader);
                    elem.add(child);
                }
            }
            final String msg = "Unexpected end of stream while reading element content";
            throw new XMLStreamException(msg);
        }
        throw new XMLStreamException("Expected Element event, found: " + event);
    }
    
    public Attribute readAttribute(final XMLEventReader reader) throws XMLStreamException {
        final XMLEvent event = reader.peek();
        if (event.isAttribute()) {
            final javax.xml.stream.events.Attribute attr = (javax.xml.stream.events.Attribute)reader.nextEvent();
            return this.createAttribute(null, attr);
        }
        throw new XMLStreamException("Expected Attribute event, found: " + event);
    }
    
    public Namespace readNamespace(final XMLEventReader reader) throws XMLStreamException {
        final XMLEvent event = reader.peek();
        if (event.isNamespace()) {
            final javax.xml.stream.events.Namespace ns = (javax.xml.stream.events.Namespace)reader.nextEvent();
            return this.createNamespace(ns);
        }
        throw new XMLStreamException("Expected Namespace event, found: " + event);
    }
    
    public CharacterData readCharacters(final XMLEventReader reader) throws XMLStreamException {
        final XMLEvent event = reader.peek();
        if (event.isCharacters()) {
            final Characters characters = reader.nextEvent().asCharacters();
            return this.createCharacterData(characters);
        }
        throw new XMLStreamException("Expected Characters event, found: " + event);
    }
    
    public Comment readComment(final XMLEventReader reader) throws XMLStreamException {
        final XMLEvent event = reader.peek();
        if (event instanceof javax.xml.stream.events.Comment) {
            return this.createComment((javax.xml.stream.events.Comment)reader.nextEvent());
        }
        throw new XMLStreamException("Expected Comment event, found: " + event);
    }
    
    public Entity readEntityReference(final XMLEventReader reader) throws XMLStreamException {
        final XMLEvent event = reader.peek();
        if (event.isEntityReference()) {
            final EntityReference entityRef = (EntityReference)reader.nextEvent();
            return this.createEntity(entityRef);
        }
        throw new XMLStreamException("Expected EntityRef event, found: " + event);
    }
    
    public ProcessingInstruction readProcessingInstruction(final XMLEventReader reader) throws XMLStreamException {
        final XMLEvent event = reader.peek();
        if (event.isProcessingInstruction()) {
            final javax.xml.stream.events.ProcessingInstruction pi = (javax.xml.stream.events.ProcessingInstruction)reader.nextEvent();
            return this.createProcessingInstruction(pi);
        }
        throw new XMLStreamException("Expected PI event, found: " + event);
    }
    
    public Element createElement(final StartElement startEvent) {
        final QName qname = startEvent.getName();
        final com.newrelic.org.dom4j.QName elemName = this.createQName(qname);
        final Element elem = this.factory.createElement(elemName);
        Iterator i = startEvent.getAttributes();
        while (i.hasNext()) {
            final javax.xml.stream.events.Attribute attr = i.next();
            elem.addAttribute(this.createQName(attr.getName()), attr.getValue());
        }
        i = startEvent.getNamespaces();
        while (i.hasNext()) {
            final javax.xml.stream.events.Namespace ns = i.next();
            elem.addNamespace(ns.getPrefix(), ns.getNamespaceURI());
        }
        return elem;
    }
    
    public Attribute createAttribute(final Element elem, final javax.xml.stream.events.Attribute attr) {
        return this.factory.createAttribute(elem, this.createQName(attr.getName()), attr.getValue());
    }
    
    public Namespace createNamespace(final javax.xml.stream.events.Namespace ns) {
        return this.factory.createNamespace(ns.getPrefix(), ns.getNamespaceURI());
    }
    
    public CharacterData createCharacterData(final Characters characters) {
        final String data = characters.getData();
        if (characters.isCData()) {
            return this.factory.createCDATA(data);
        }
        return this.factory.createText(data);
    }
    
    public Comment createComment(final javax.xml.stream.events.Comment comment) {
        return this.factory.createComment(comment.getText());
    }
    
    public Entity createEntity(final EntityReference entityRef) {
        return this.factory.createEntity(entityRef.getName(), entityRef.getDeclaration().getReplacementText());
    }
    
    public ProcessingInstruction createProcessingInstruction(final javax.xml.stream.events.ProcessingInstruction pi) {
        return this.factory.createProcessingInstruction(pi.getTarget(), pi.getData());
    }
    
    public com.newrelic.org.dom4j.QName createQName(final QName qname) {
        return this.factory.createQName(qname.getLocalPart(), qname.getPrefix(), qname.getNamespaceURI());
    }
}
