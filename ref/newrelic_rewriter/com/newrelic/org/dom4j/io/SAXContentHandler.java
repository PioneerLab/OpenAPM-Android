// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import com.newrelic.org.dom4j.tree.AbstractElement;
import com.newrelic.org.dom4j.Namespace;
import java.lang.reflect.Method;
import com.newrelic.org.dom4j.dtd.ExternalEntityDecl;
import com.newrelic.org.dom4j.dtd.InternalEntityDecl;
import com.newrelic.org.dom4j.dtd.AttributeDecl;
import com.newrelic.org.dom4j.dtd.ElementDecl;
import com.newrelic.org.dom4j.DocumentType;
import org.xml.sax.SAXParseException;
import com.newrelic.org.dom4j.Branch;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.ElementPath;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import java.util.HashMap;
import com.newrelic.org.dom4j.Element;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import java.util.List;
import java.util.Map;
import org.xml.sax.Locator;
import com.newrelic.org.dom4j.ElementHandler;
import com.newrelic.org.dom4j.tree.NamespaceStack;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.DocumentFactory;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXContentHandler extends DefaultHandler implements LexicalHandler, DeclHandler, DTDHandler
{
    private DocumentFactory documentFactory;
    private Document document;
    private ElementStack elementStack;
    private NamespaceStack namespaceStack;
    private ElementHandler elementHandler;
    private Locator locator;
    private String entity;
    private boolean insideDTDSection;
    private boolean insideCDATASection;
    private StringBuffer cdataText;
    private Map availableNamespaceMap;
    private List declaredNamespaceList;
    private List internalDTDDeclarations;
    private List externalDTDDeclarations;
    private int declaredNamespaceIndex;
    private EntityResolver entityResolver;
    private InputSource inputSource;
    private Element currentElement;
    private boolean includeInternalDTDDeclarations;
    private boolean includeExternalDTDDeclarations;
    private int entityLevel;
    private boolean internalDTDsubset;
    private boolean mergeAdjacentText;
    private boolean textInTextBuffer;
    private boolean ignoreComments;
    private StringBuffer textBuffer;
    private boolean stripWhitespaceText;
    
    public SAXContentHandler() {
        this(DocumentFactory.getInstance());
    }
    
    public SAXContentHandler(final DocumentFactory documentFactory) {
        this(documentFactory, null);
    }
    
    public SAXContentHandler(final DocumentFactory documentFactory, final ElementHandler elementHandler) {
        this(documentFactory, elementHandler, null);
        this.elementStack = this.createElementStack();
    }
    
    public SAXContentHandler(final DocumentFactory documentFactory, final ElementHandler elementHandler, final ElementStack elementStack) {
        this.availableNamespaceMap = new HashMap();
        this.declaredNamespaceList = new ArrayList();
        this.includeInternalDTDDeclarations = false;
        this.includeExternalDTDDeclarations = false;
        this.internalDTDsubset = false;
        this.mergeAdjacentText = false;
        this.textInTextBuffer = false;
        this.ignoreComments = false;
        this.stripWhitespaceText = false;
        this.documentFactory = documentFactory;
        this.elementHandler = elementHandler;
        this.elementStack = elementStack;
        this.namespaceStack = new NamespaceStack(documentFactory);
    }
    
    public Document getDocument() {
        if (this.document == null) {
            this.document = this.createDocument();
        }
        return this.document;
    }
    
    public void setDocumentLocator(final Locator documentLocator) {
        this.locator = documentLocator;
    }
    
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (this.mergeAdjacentText && this.textInTextBuffer) {
            this.completeCurrentTextNode();
        }
        if (this.currentElement != null) {
            this.currentElement.addProcessingInstruction(target, data);
        }
        else {
            this.getDocument().addProcessingInstruction(target, data);
        }
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this.namespaceStack.push(prefix, uri);
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        this.namespaceStack.pop(prefix);
        this.declaredNamespaceIndex = this.namespaceStack.size();
    }
    
    public void startDocument() throws SAXException {
        this.document = null;
        this.currentElement = null;
        this.elementStack.clear();
        if (this.elementHandler != null && this.elementHandler instanceof DispatchHandler) {
            this.elementStack.setDispatchHandler((DispatchHandler)this.elementHandler);
        }
        this.namespaceStack.clear();
        this.declaredNamespaceIndex = 0;
        if (this.mergeAdjacentText && this.textBuffer == null) {
            this.textBuffer = new StringBuffer();
        }
        this.textInTextBuffer = false;
    }
    
    public void endDocument() throws SAXException {
        this.namespaceStack.clear();
        this.elementStack.clear();
        this.currentElement = null;
        this.textBuffer = null;
    }
    
    public void startElement(final String namespaceURI, final String localName, final String qualifiedName, final Attributes attributes) throws SAXException {
        if (this.mergeAdjacentText && this.textInTextBuffer) {
            this.completeCurrentTextNode();
        }
        final QName qName = this.namespaceStack.getQName(namespaceURI, localName, qualifiedName);
        Branch branch = this.currentElement;
        if (branch == null) {
            branch = this.getDocument();
        }
        final Element element = branch.addElement(qName);
        this.addDeclaredNamespaces(element);
        this.addAttributes(element, attributes);
        this.elementStack.pushElement(element);
        this.currentElement = element;
        this.entity = null;
        if (this.elementHandler != null) {
            this.elementHandler.onStart(this.elementStack);
        }
    }
    
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        if (this.mergeAdjacentText && this.textInTextBuffer) {
            this.completeCurrentTextNode();
        }
        if (this.elementHandler != null && this.currentElement != null) {
            this.elementHandler.onEnd(this.elementStack);
        }
        this.elementStack.popElement();
        this.currentElement = this.elementStack.peekElement();
    }
    
    public void characters(final char[] ch, final int start, final int end) throws SAXException {
        if (end == 0) {
            return;
        }
        if (this.currentElement != null) {
            if (this.entity != null) {
                if (this.mergeAdjacentText && this.textInTextBuffer) {
                    this.completeCurrentTextNode();
                }
                this.currentElement.addEntity(this.entity, new String(ch, start, end));
                this.entity = null;
            }
            else if (this.insideCDATASection) {
                if (this.mergeAdjacentText && this.textInTextBuffer) {
                    this.completeCurrentTextNode();
                }
                this.cdataText.append(new String(ch, start, end));
            }
            else if (this.mergeAdjacentText) {
                this.textBuffer.append(ch, start, end);
                this.textInTextBuffer = true;
            }
            else {
                this.currentElement.addText(new String(ch, start, end));
            }
        }
    }
    
    public void warning(final SAXParseException exception) throws SAXException {
    }
    
    public void error(final SAXParseException exception) throws SAXException {
        throw exception;
    }
    
    public void fatalError(final SAXParseException exception) throws SAXException {
        throw exception;
    }
    
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        this.getDocument().addDocType(name, publicId, systemId);
        this.insideDTDSection = true;
        this.internalDTDsubset = true;
    }
    
    public void endDTD() throws SAXException {
        this.insideDTDSection = false;
        final DocumentType docType = this.getDocument().getDocType();
        if (docType != null) {
            if (this.internalDTDDeclarations != null) {
                docType.setInternalDeclarations(this.internalDTDDeclarations);
            }
            if (this.externalDTDDeclarations != null) {
                docType.setExternalDeclarations(this.externalDTDDeclarations);
            }
        }
        this.internalDTDDeclarations = null;
        this.externalDTDDeclarations = null;
    }
    
    public void startEntity(final String name) throws SAXException {
        ++this.entityLevel;
        this.entity = null;
        if (!this.insideDTDSection && !this.isIgnorableEntity(name)) {
            this.entity = name;
        }
        this.internalDTDsubset = false;
    }
    
    public void endEntity(final String name) throws SAXException {
        --this.entityLevel;
        this.entity = null;
        if (this.entityLevel == 0) {
            this.internalDTDsubset = true;
        }
    }
    
    public void startCDATA() throws SAXException {
        this.insideCDATASection = true;
        this.cdataText = new StringBuffer();
    }
    
    public void endCDATA() throws SAXException {
        this.insideCDATASection = false;
        this.currentElement.addCDATA(this.cdataText.toString());
    }
    
    public void comment(final char[] ch, final int start, final int end) throws SAXException {
        if (!this.ignoreComments) {
            if (this.mergeAdjacentText && this.textInTextBuffer) {
                this.completeCurrentTextNode();
            }
            final String text = new String(ch, start, end);
            if (!this.insideDTDSection && text.length() > 0) {
                if (this.currentElement != null) {
                    this.currentElement.addComment(text);
                }
                else {
                    this.getDocument().addComment(text);
                }
            }
        }
    }
    
    public void elementDecl(final String name, final String model) throws SAXException {
        if (this.internalDTDsubset) {
            if (this.includeInternalDTDDeclarations) {
                this.addDTDDeclaration(new ElementDecl(name, model));
            }
        }
        else if (this.includeExternalDTDDeclarations) {
            this.addExternalDTDDeclaration(new ElementDecl(name, model));
        }
    }
    
    public void attributeDecl(final String eName, final String aName, final String type, final String valueDefault, final String val) throws SAXException {
        if (this.internalDTDsubset) {
            if (this.includeInternalDTDDeclarations) {
                this.addDTDDeclaration(new AttributeDecl(eName, aName, type, valueDefault, val));
            }
        }
        else if (this.includeExternalDTDDeclarations) {
            this.addExternalDTDDeclaration(new AttributeDecl(eName, aName, type, valueDefault, val));
        }
    }
    
    public void internalEntityDecl(final String name, final String value) throws SAXException {
        if (this.internalDTDsubset) {
            if (this.includeInternalDTDDeclarations) {
                this.addDTDDeclaration(new InternalEntityDecl(name, value));
            }
        }
        else if (this.includeExternalDTDDeclarations) {
            this.addExternalDTDDeclaration(new InternalEntityDecl(name, value));
        }
    }
    
    public void externalEntityDecl(final String name, final String publicId, final String sysId) throws SAXException {
        final ExternalEntityDecl declaration = new ExternalEntityDecl(name, publicId, sysId);
        if (this.internalDTDsubset) {
            if (this.includeInternalDTDDeclarations) {
                this.addDTDDeclaration(declaration);
            }
        }
        else if (this.includeExternalDTDDeclarations) {
            this.addExternalDTDDeclaration(declaration);
        }
    }
    
    public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) throws SAXException {
    }
    
    public ElementStack getElementStack() {
        return this.elementStack;
    }
    
    public void setElementStack(final ElementStack elementStack) {
        this.elementStack = elementStack;
    }
    
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }
    
    public InputSource getInputSource() {
        return this.inputSource;
    }
    
    public void setInputSource(final InputSource inputSource) {
        this.inputSource = inputSource;
    }
    
    public boolean isIncludeInternalDTDDeclarations() {
        return this.includeInternalDTDDeclarations;
    }
    
    public void setIncludeInternalDTDDeclarations(final boolean include) {
        this.includeInternalDTDDeclarations = include;
    }
    
    public boolean isIncludeExternalDTDDeclarations() {
        return this.includeExternalDTDDeclarations;
    }
    
    public void setIncludeExternalDTDDeclarations(final boolean include) {
        this.includeExternalDTDDeclarations = include;
    }
    
    public boolean isMergeAdjacentText() {
        return this.mergeAdjacentText;
    }
    
    public void setMergeAdjacentText(final boolean mergeAdjacentText) {
        this.mergeAdjacentText = mergeAdjacentText;
    }
    
    public boolean isStripWhitespaceText() {
        return this.stripWhitespaceText;
    }
    
    public void setStripWhitespaceText(final boolean stripWhitespaceText) {
        this.stripWhitespaceText = stripWhitespaceText;
    }
    
    public boolean isIgnoreComments() {
        return this.ignoreComments;
    }
    
    public void setIgnoreComments(final boolean ignoreComments) {
        this.ignoreComments = ignoreComments;
    }
    
    protected void completeCurrentTextNode() {
        if (this.stripWhitespaceText) {
            boolean whitespace = true;
            for (int i = 0, size = this.textBuffer.length(); i < size; ++i) {
                if (!Character.isWhitespace(this.textBuffer.charAt(i))) {
                    whitespace = false;
                    break;
                }
            }
            if (!whitespace) {
                this.currentElement.addText(this.textBuffer.toString());
            }
        }
        else {
            this.currentElement.addText(this.textBuffer.toString());
        }
        this.textBuffer.setLength(0);
        this.textInTextBuffer = false;
    }
    
    protected Document createDocument() {
        final String encoding = this.getEncoding();
        final Document doc = this.documentFactory.createDocument(encoding);
        doc.setEntityResolver(this.entityResolver);
        if (this.inputSource != null) {
            doc.setName(this.inputSource.getSystemId());
        }
        return doc;
    }
    
    private String getEncoding() {
        if (this.locator == null) {
            return null;
        }
        try {
            final Method m = this.locator.getClass().getMethod("getEncoding", (Class<?>[])new Class[0]);
            if (m != null) {
                return (String)m.invoke(this.locator, (Object[])null);
            }
        }
        catch (Exception ex) {}
        return null;
    }
    
    protected boolean isIgnorableEntity(final String name) {
        return "amp".equals(name) || "apos".equals(name) || "gt".equals(name) || "lt".equals(name) || "quot".equals(name);
    }
    
    protected void addDeclaredNamespaces(final Element element) {
        final Namespace elementNamespace = element.getNamespace();
        final int size = this.namespaceStack.size();
        while (this.declaredNamespaceIndex < size) {
            final Namespace namespace = this.namespaceStack.getNamespace(this.declaredNamespaceIndex);
            element.add(namespace);
            ++this.declaredNamespaceIndex;
        }
    }
    
    protected void addAttributes(final Element element, final Attributes attributes) {
        final boolean noNamespaceAttributes = false;
        if (element instanceof AbstractElement) {
            final AbstractElement baseElement = (AbstractElement)element;
            baseElement.setAttributes(attributes, this.namespaceStack, noNamespaceAttributes);
        }
        else {
            for (int size = attributes.getLength(), i = 0; i < size; ++i) {
                final String attributeQName = attributes.getQName(i);
                if (noNamespaceAttributes || !attributeQName.startsWith("xmlns")) {
                    final String attributeURI = attributes.getURI(i);
                    final String attributeLocalName = attributes.getLocalName(i);
                    final String attributeValue = attributes.getValue(i);
                    final QName qName = this.namespaceStack.getAttributeQName(attributeURI, attributeLocalName, attributeQName);
                    element.addAttribute(qName, attributeValue);
                }
            }
        }
    }
    
    protected void addDTDDeclaration(final Object declaration) {
        if (this.internalDTDDeclarations == null) {
            this.internalDTDDeclarations = new ArrayList();
        }
        this.internalDTDDeclarations.add(declaration);
    }
    
    protected void addExternalDTDDeclaration(final Object declaration) {
        if (this.externalDTDDeclarations == null) {
            this.externalDTDDeclarations = new ArrayList();
        }
        this.externalDTDDeclarations.add(declaration);
    }
    
    protected ElementStack createElementStack() {
        return new ElementStack();
    }
}
