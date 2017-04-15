// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.util;

import com.newrelic.org.dom4j.rule.Pattern;
import com.newrelic.org.dom4j.NodeFilter;
import org.jaxen.VariableContext;
import com.newrelic.org.dom4j.XPath;
import java.util.Map;
import com.newrelic.org.dom4j.ProcessingInstruction;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.Entity;
import com.newrelic.org.dom4j.Text;
import com.newrelic.org.dom4j.Comment;
import com.newrelic.org.dom4j.CDATA;
import com.newrelic.org.dom4j.Attribute;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.DocumentType;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.DocumentFactory;

public abstract class ProxyDocumentFactory
{
    private DocumentFactory proxy;
    
    public ProxyDocumentFactory() {
        this.proxy = DocumentFactory.getInstance();
    }
    
    public ProxyDocumentFactory(final DocumentFactory proxy) {
        this.proxy = proxy;
    }
    
    public Document createDocument() {
        return this.proxy.createDocument();
    }
    
    public Document createDocument(final Element rootElement) {
        return this.proxy.createDocument(rootElement);
    }
    
    public DocumentType createDocType(final String name, final String publicId, final String systemId) {
        return this.proxy.createDocType(name, publicId, systemId);
    }
    
    public Element createElement(final QName qname) {
        return this.proxy.createElement(qname);
    }
    
    public Element createElement(final String name) {
        return this.proxy.createElement(name);
    }
    
    public Attribute createAttribute(final Element owner, final QName qname, final String value) {
        return this.proxy.createAttribute(owner, qname, value);
    }
    
    public Attribute createAttribute(final Element owner, final String name, final String value) {
        return this.proxy.createAttribute(owner, name, value);
    }
    
    public CDATA createCDATA(final String text) {
        return this.proxy.createCDATA(text);
    }
    
    public Comment createComment(final String text) {
        return this.proxy.createComment(text);
    }
    
    public Text createText(final String text) {
        return this.proxy.createText(text);
    }
    
    public Entity createEntity(final String name, final String text) {
        return this.proxy.createEntity(name, text);
    }
    
    public Namespace createNamespace(final String prefix, final String uri) {
        return this.proxy.createNamespace(prefix, uri);
    }
    
    public ProcessingInstruction createProcessingInstruction(final String target, final String data) {
        return this.proxy.createProcessingInstruction(target, data);
    }
    
    public ProcessingInstruction createProcessingInstruction(final String target, final Map data) {
        return this.proxy.createProcessingInstruction(target, data);
    }
    
    public QName createQName(final String localName, final Namespace namespace) {
        return this.proxy.createQName(localName, namespace);
    }
    
    public QName createQName(final String localName) {
        return this.proxy.createQName(localName);
    }
    
    public QName createQName(final String name, final String prefix, final String uri) {
        return this.proxy.createQName(name, prefix, uri);
    }
    
    public QName createQName(final String qualifiedName, final String uri) {
        return this.proxy.createQName(qualifiedName, uri);
    }
    
    public XPath createXPath(final String xpathExpression) {
        return this.proxy.createXPath(xpathExpression);
    }
    
    public XPath createXPath(final String xpathExpression, final VariableContext variableContext) {
        return this.proxy.createXPath(xpathExpression, variableContext);
    }
    
    public NodeFilter createXPathFilter(final String xpathFilterExpression, final VariableContext variableContext) {
        return this.proxy.createXPathFilter(xpathFilterExpression, variableContext);
    }
    
    public NodeFilter createXPathFilter(final String xpathFilterExpression) {
        return this.proxy.createXPathFilter(xpathFilterExpression);
    }
    
    public Pattern createPattern(final String xpathPattern) {
        return this.proxy.createPattern(xpathPattern);
    }
    
    protected DocumentFactory getProxy() {
        return this.proxy;
    }
    
    protected void setProxy(DocumentFactory proxy) {
        if (proxy == null) {
            proxy = DocumentFactory.getInstance();
        }
        this.proxy = proxy;
    }
}
