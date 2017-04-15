// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import org.xml.sax.Attributes;
import java.util.List;
import java.io.IOException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;
import java.util.Iterator;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.Text;
import com.newrelic.org.dom4j.CharacterData;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import com.newrelic.org.dom4j.Branch;
import com.newrelic.org.dom4j.tree.NamespaceStack;
import org.xml.sax.SAXException;
import com.newrelic.org.dom4j.DocumentType;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Comment;
import com.newrelic.org.dom4j.ProcessingInstruction;
import com.newrelic.org.dom4j.Entity;
import com.newrelic.org.dom4j.CDATA;
import com.newrelic.org.dom4j.Attribute;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Node;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.DTDHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;

public class SAXWriter implements XMLReader
{
    protected static final String[] LEXICAL_HANDLER_NAMES;
    protected static final String FEATURE_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    protected static final String FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";
    private ContentHandler contentHandler;
    private DTDHandler dtdHandler;
    private EntityResolver entityResolver;
    private ErrorHandler errorHandler;
    private LexicalHandler lexicalHandler;
    private AttributesImpl attributes;
    private Map features;
    private Map properties;
    private boolean declareNamespaceAttributes;
    
    public SAXWriter() {
        this.attributes = new AttributesImpl();
        this.features = new HashMap();
        (this.properties = new HashMap()).put("http://xml.org/sax/features/namespace-prefixes", Boolean.FALSE);
        this.properties.put("http://xml.org/sax/features/namespace-prefixes", Boolean.TRUE);
    }
    
    public SAXWriter(final ContentHandler contentHandler) {
        this();
        this.contentHandler = contentHandler;
    }
    
    public SAXWriter(final ContentHandler contentHandler, final LexicalHandler lexicalHandler) {
        this();
        this.contentHandler = contentHandler;
        this.lexicalHandler = lexicalHandler;
    }
    
    public SAXWriter(final ContentHandler contentHandler, final LexicalHandler lexicalHandler, final EntityResolver entityResolver) {
        this();
        this.contentHandler = contentHandler;
        this.lexicalHandler = lexicalHandler;
        this.entityResolver = entityResolver;
    }
    
    public void write(final Node node) throws SAXException {
        final int nodeType = node.getNodeType();
        switch (nodeType) {
            case 1: {
                this.write((Element)node);
                break;
            }
            case 2: {
                this.write(node);
                break;
            }
            case 3: {
                this.write(node.getText());
                break;
            }
            case 4: {
                this.write((CDATA)node);
                break;
            }
            case 5: {
                this.write((Entity)node);
                break;
            }
            case 7: {
                this.write((ProcessingInstruction)node);
                break;
            }
            case 8: {
                this.write((Comment)node);
                break;
            }
            case 9: {
                this.write((Document)node);
                break;
            }
            case 10: {
                this.write(node);
                break;
            }
            case 13: {
                break;
            }
            default: {
                throw new SAXException("Invalid node type: " + node);
            }
        }
    }
    
    public void write(final Document document) throws SAXException {
        if (document != null) {
            this.checkForNullHandlers();
            this.documentLocator(document);
            this.startDocument();
            this.entityResolver(document);
            this.dtdHandler(document);
            this.writeContent(document, new NamespaceStack());
            this.endDocument();
        }
    }
    
    public void write(final Element element) throws SAXException {
        this.write(element, new NamespaceStack());
    }
    
    public void writeOpen(final Element element) throws SAXException {
        this.startElement(element, null);
    }
    
    public void writeClose(final Element element) throws SAXException {
        this.endElement(element);
    }
    
    public void write(final String text) throws SAXException {
        if (text != null) {
            final char[] chars = text.toCharArray();
            this.contentHandler.characters(chars, 0, chars.length);
        }
    }
    
    public void write(final CDATA cdata) throws SAXException {
        final String text = cdata.getText();
        if (this.lexicalHandler != null) {
            this.lexicalHandler.startCDATA();
            this.write(text);
            this.lexicalHandler.endCDATA();
        }
        else {
            this.write(text);
        }
    }
    
    public void write(final Comment comment) throws SAXException {
        if (this.lexicalHandler != null) {
            final String text = comment.getText();
            final char[] chars = text.toCharArray();
            this.lexicalHandler.comment(chars, 0, chars.length);
        }
    }
    
    public void write(final Entity entity) throws SAXException {
        final String text = entity.getText();
        if (this.lexicalHandler != null) {
            final String name = entity.getName();
            this.lexicalHandler.startEntity(name);
            this.write(text);
            this.lexicalHandler.endEntity(name);
        }
        else {
            this.write(text);
        }
    }
    
    public void write(final ProcessingInstruction pi) throws SAXException {
        final String target = pi.getTarget();
        final String text = pi.getText();
        this.contentHandler.processingInstruction(target, text);
    }
    
    public boolean isDeclareNamespaceAttributes() {
        return this.declareNamespaceAttributes;
    }
    
    public void setDeclareNamespaceAttributes(final boolean declareNamespaceAttrs) {
        this.declareNamespaceAttributes = declareNamespaceAttrs;
    }
    
    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }
    
    public void setContentHandler(final ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }
    
    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }
    
    public void setDTDHandler(final DTDHandler handler) {
        this.dtdHandler = handler;
    }
    
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }
    
    public LexicalHandler getLexicalHandler() {
        return this.lexicalHandler;
    }
    
    public void setLexicalHandler(final LexicalHandler lexicalHandler) {
        this.lexicalHandler = lexicalHandler;
    }
    
    public void setXMLReader(final XMLReader xmlReader) {
        this.setContentHandler(xmlReader.getContentHandler());
        this.setDTDHandler(xmlReader.getDTDHandler());
        this.setEntityResolver(xmlReader.getEntityResolver());
        this.setErrorHandler(xmlReader.getErrorHandler());
    }
    
    public boolean getFeature(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        final Boolean answer = this.features.get(name);
        return answer != null && answer;
    }
    
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            this.setDeclareNamespaceAttributes(value);
        }
        else if ("http://xml.org/sax/features/namespace-prefixes".equals(name) && !value) {
            final String msg = "Namespace feature is always supported in dom4j";
            throw new SAXNotSupportedException(msg);
        }
        this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setProperty(final String name, final Object value) {
        for (int i = 0; i < SAXWriter.LEXICAL_HANDLER_NAMES.length; ++i) {
            if (SAXWriter.LEXICAL_HANDLER_NAMES[i].equals(name)) {
                this.setLexicalHandler((LexicalHandler)value);
                return;
            }
        }
        this.properties.put(name, value);
    }
    
    public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        for (int i = 0; i < SAXWriter.LEXICAL_HANDLER_NAMES.length; ++i) {
            if (SAXWriter.LEXICAL_HANDLER_NAMES[i].equals(name)) {
                return this.getLexicalHandler();
            }
        }
        return this.properties.get(name);
    }
    
    public void parse(final String systemId) throws SAXNotSupportedException {
        throw new SAXNotSupportedException("This XMLReader can only accept <dom4j> InputSource objects");
    }
    
    public void parse(final InputSource input) throws SAXException {
        if (input instanceof DocumentInputSource) {
            final DocumentInputSource documentInput = (DocumentInputSource)input;
            final Document document = documentInput.getDocument();
            this.write(document);
            return;
        }
        throw new SAXNotSupportedException("This XMLReader can only accept <dom4j> InputSource objects");
    }
    
    protected void writeContent(final Branch branch, final NamespaceStack namespaceStack) throws SAXException {
        final Iterator iter = branch.nodeIterator();
        while (iter.hasNext()) {
            final Object object = iter.next();
            if (object instanceof Element) {
                this.write((Element)object, namespaceStack);
            }
            else if (object instanceof CharacterData) {
                if (object instanceof Text) {
                    final Text text = (Text)object;
                    this.write(text.getText());
                }
                else if (object instanceof CDATA) {
                    this.write((CDATA)object);
                }
                else {
                    if (!(object instanceof Comment)) {
                        throw new SAXException("Invalid Node in DOM4J content: " + object + " of type: " + object.getClass());
                    }
                    this.write((Comment)object);
                }
            }
            else if (object instanceof String) {
                this.write((String)object);
            }
            else if (object instanceof Entity) {
                this.write((Entity)object);
            }
            else if (object instanceof ProcessingInstruction) {
                this.write((ProcessingInstruction)object);
            }
            else {
                if (!(object instanceof Namespace)) {
                    throw new SAXException("Invalid Node in DOM4J content: " + object);
                }
                this.write((Node)object);
            }
        }
    }
    
    protected void documentLocator(final Document document) throws SAXException {
        final LocatorImpl locator = new LocatorImpl();
        String publicID = null;
        String systemID = null;
        final DocumentType docType = document.getDocType();
        if (docType != null) {
            publicID = docType.getPublicID();
            systemID = docType.getSystemID();
        }
        if (publicID != null) {
            locator.setPublicId(publicID);
        }
        if (systemID != null) {
            locator.setSystemId(systemID);
        }
        locator.setLineNumber(-1);
        locator.setColumnNumber(-1);
        this.contentHandler.setDocumentLocator(locator);
    }
    
    protected void entityResolver(final Document document) throws SAXException {
        if (this.entityResolver != null) {
            final DocumentType docType = document.getDocType();
            if (docType != null) {
                final String publicID = docType.getPublicID();
                final String systemID = docType.getSystemID();
                if (publicID == null) {
                    if (systemID == null) {
                        return;
                    }
                }
                try {
                    this.entityResolver.resolveEntity(publicID, systemID);
                }
                catch (IOException e) {
                    throw new SAXException("Could not resolve publicID: " + publicID + " systemID: " + systemID, e);
                }
            }
        }
    }
    
    protected void dtdHandler(final Document document) throws SAXException {
    }
    
    protected void startDocument() throws SAXException {
        this.contentHandler.startDocument();
    }
    
    protected void endDocument() throws SAXException {
        this.contentHandler.endDocument();
    }
    
    protected void write(final Element element, final NamespaceStack namespaceStack) throws SAXException {
        final int stackSize = namespaceStack.size();
        final AttributesImpl namespaceAttributes = this.startPrefixMapping(element, namespaceStack);
        this.startElement(element, namespaceAttributes);
        this.writeContent(element, namespaceStack);
        this.endElement(element);
        this.endPrefixMapping(namespaceStack, stackSize);
    }
    
    protected AttributesImpl startPrefixMapping(final Element element, final NamespaceStack namespaceStack) throws SAXException {
        AttributesImpl namespaceAttributes = null;
        final Namespace elementNamespace = element.getNamespace();
        if (elementNamespace != null && !this.isIgnoreableNamespace(elementNamespace, namespaceStack)) {
            namespaceStack.push(elementNamespace);
            this.contentHandler.startPrefixMapping(elementNamespace.getPrefix(), elementNamespace.getURI());
            namespaceAttributes = this.addNamespaceAttribute(namespaceAttributes, elementNamespace);
        }
        final List declaredNamespaces = element.declaredNamespaces();
        for (int i = 0, size = declaredNamespaces.size(); i < size; ++i) {
            final Namespace namespace = declaredNamespaces.get(i);
            if (!this.isIgnoreableNamespace(namespace, namespaceStack)) {
                namespaceStack.push(namespace);
                this.contentHandler.startPrefixMapping(namespace.getPrefix(), namespace.getURI());
                namespaceAttributes = this.addNamespaceAttribute(namespaceAttributes, namespace);
            }
        }
        return namespaceAttributes;
    }
    
    protected void endPrefixMapping(final NamespaceStack stack, final int stackSize) throws SAXException {
        while (stack.size() > stackSize) {
            final Namespace namespace = stack.pop();
            if (namespace != null) {
                this.contentHandler.endPrefixMapping(namespace.getPrefix());
            }
        }
    }
    
    protected void startElement(final Element element, final AttributesImpl namespaceAttributes) throws SAXException {
        this.contentHandler.startElement(element.getNamespaceURI(), element.getName(), element.getQualifiedName(), this.createAttributes(element, namespaceAttributes));
    }
    
    protected void endElement(final Element element) throws SAXException {
        this.contentHandler.endElement(element.getNamespaceURI(), element.getName(), element.getQualifiedName());
    }
    
    protected Attributes createAttributes(final Element element, final Attributes namespaceAttributes) throws SAXException {
        this.attributes.clear();
        if (namespaceAttributes != null) {
            this.attributes.setAttributes(namespaceAttributes);
        }
        final Iterator iter = element.attributeIterator();
        while (iter.hasNext()) {
            final Attribute attribute = iter.next();
            this.attributes.addAttribute(attribute.getNamespaceURI(), attribute.getName(), attribute.getQualifiedName(), "CDATA", attribute.getValue());
        }
        return this.attributes;
    }
    
    protected AttributesImpl addNamespaceAttribute(AttributesImpl attrs, final Namespace namespace) {
        if (this.declareNamespaceAttributes) {
            if (attrs == null) {
                attrs = new AttributesImpl();
            }
            final String prefix = namespace.getPrefix();
            String qualifiedName = "xmlns";
            if (prefix != null && prefix.length() > 0) {
                qualifiedName = "xmlns:" + prefix;
            }
            final String uri = "";
            final String localName = prefix;
            final String type = "CDATA";
            final String value = namespace.getURI();
            attrs.addAttribute(uri, localName, qualifiedName, type, value);
        }
        return attrs;
    }
    
    protected boolean isIgnoreableNamespace(final Namespace namespace, final NamespaceStack namespaceStack) {
        if (namespace.equals(Namespace.NO_NAMESPACE) || namespace.equals(Namespace.XML_NAMESPACE)) {
            return true;
        }
        final String uri = namespace.getURI();
        return uri == null || uri.length() <= 0 || namespaceStack.contains(namespace);
    }
    
    protected void checkForNullHandlers() {
    }
    
    static {
        LEXICAL_HANDLER_NAMES = new String[] { "http://xml.org/sax/properties/lexical-handler", "http://xml.org/sax/handlers/LexicalHandler" };
    }
}
