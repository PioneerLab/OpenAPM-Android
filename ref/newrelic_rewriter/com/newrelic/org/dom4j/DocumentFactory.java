// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import com.newrelic.org.dom4j.xpath.XPathPattern;
import com.newrelic.org.dom4j.rule.Pattern;
import org.jaxen.VariableContext;
import com.newrelic.org.dom4j.xpath.DefaultXPath;
import com.newrelic.org.dom4j.tree.DefaultProcessingInstruction;
import com.newrelic.org.dom4j.tree.DefaultEntity;
import com.newrelic.org.dom4j.tree.DefaultText;
import com.newrelic.org.dom4j.tree.DefaultComment;
import com.newrelic.org.dom4j.tree.DefaultCDATA;
import com.newrelic.org.dom4j.tree.DefaultAttribute;
import com.newrelic.org.dom4j.tree.DefaultElement;
import com.newrelic.org.dom4j.tree.DefaultDocumentType;
import com.newrelic.org.dom4j.tree.AbstractDocument;
import com.newrelic.org.dom4j.tree.DefaultDocument;
import com.newrelic.org.dom4j.util.SimpleSingleton;
import java.util.Map;
import com.newrelic.org.dom4j.tree.QNameCache;
import com.newrelic.org.dom4j.util.SingletonStrategy;
import java.io.Serializable;

public class DocumentFactory implements Serializable
{
    private static SingletonStrategy singleton;
    protected transient QNameCache cache;
    private Map xpathNamespaceURIs;
    static /* synthetic */ Class class$org$dom4j$DocumentFactory;
    
    private static SingletonStrategy createSingleton() {
        SingletonStrategy result = null;
        String documentFactoryClassName;
        try {
            documentFactoryClassName = System.getProperty("com.newrelic.org.dom4j.factory", "com.newrelic.org.dom4j.DocumentFactory");
        }
        catch (Exception e) {
            documentFactoryClassName = "com.newrelic.org.dom4j.DocumentFactory";
        }
        try {
            final String singletonClass = System.getProperty("com.newrelic.org.dom4j.DocumentFactory.singleton.strategy", "com.newrelic.org.dom4j.util.SimpleSingleton");
            final Class clazz = Class.forName(singletonClass);
            result = clazz.newInstance();
        }
        catch (Exception e) {
            result = new SimpleSingleton();
        }
        result.setSingletonClassName(documentFactoryClassName);
        return result;
    }
    
    public DocumentFactory() {
        this.init();
    }
    
    public static synchronized DocumentFactory getInstance() {
        if (DocumentFactory.singleton == null) {
            DocumentFactory.singleton = createSingleton();
        }
        return (DocumentFactory)DocumentFactory.singleton.instance();
    }
    
    public Document createDocument() {
        final DefaultDocument answer = new DefaultDocument();
        answer.setDocumentFactory(this);
        return answer;
    }
    
    public Document createDocument(final String encoding) {
        final Document answer = this.createDocument();
        if (answer instanceof AbstractDocument) {
            ((AbstractDocument)answer).setXMLEncoding(encoding);
        }
        return answer;
    }
    
    public Document createDocument(final Element rootElement) {
        final Document answer = this.createDocument();
        answer.setRootElement(rootElement);
        return answer;
    }
    
    public DocumentType createDocType(final String name, final String publicId, final String systemId) {
        return new DefaultDocumentType(name, publicId, systemId);
    }
    
    public Element createElement(final QName qname) {
        return new DefaultElement(qname);
    }
    
    public Element createElement(final String name) {
        return this.createElement(this.createQName(name));
    }
    
    public Element createElement(final String qualifiedName, final String namespaceURI) {
        return this.createElement(this.createQName(qualifiedName, namespaceURI));
    }
    
    public Attribute createAttribute(final Element owner, final QName qname, final String value) {
        return new DefaultAttribute(qname, value);
    }
    
    public Attribute createAttribute(final Element owner, final String name, final String value) {
        return this.createAttribute(owner, this.createQName(name), value);
    }
    
    public CDATA createCDATA(final String text) {
        return new DefaultCDATA(text);
    }
    
    public Comment createComment(final String text) {
        return new DefaultComment(text);
    }
    
    public Text createText(final String text) {
        if (text == null) {
            final String msg = "Adding text to an XML document must not be null";
            throw new IllegalArgumentException(msg);
        }
        return new DefaultText(text);
    }
    
    public Entity createEntity(final String name, final String text) {
        return new DefaultEntity(name, text);
    }
    
    public Namespace createNamespace(final String prefix, final String uri) {
        return Namespace.get(prefix, uri);
    }
    
    public ProcessingInstruction createProcessingInstruction(final String target, final String data) {
        return new DefaultProcessingInstruction(target, data);
    }
    
    public ProcessingInstruction createProcessingInstruction(final String target, final Map data) {
        return new DefaultProcessingInstruction(target, data);
    }
    
    public QName createQName(final String localName, final Namespace namespace) {
        return this.cache.get(localName, namespace);
    }
    
    public QName createQName(final String localName) {
        return this.cache.get(localName);
    }
    
    public QName createQName(final String name, final String prefix, final String uri) {
        return this.cache.get(name, Namespace.get(prefix, uri));
    }
    
    public QName createQName(final String qualifiedName, final String uri) {
        return this.cache.get(qualifiedName, uri);
    }
    
    public XPath createXPath(final String xpathExpression) throws InvalidXPathException {
        final DefaultXPath xpath = new DefaultXPath(xpathExpression);
        if (this.xpathNamespaceURIs != null) {
            xpath.setNamespaceURIs(this.xpathNamespaceURIs);
        }
        return xpath;
    }
    
    public XPath createXPath(final String xpathExpression, final VariableContext variableContext) {
        final XPath xpath = this.createXPath(xpathExpression);
        xpath.setVariableContext(variableContext);
        return xpath;
    }
    
    public NodeFilter createXPathFilter(final String xpathFilterExpression, final VariableContext variableContext) {
        final XPath answer = this.createXPath(xpathFilterExpression);
        answer.setVariableContext(variableContext);
        return answer;
    }
    
    public NodeFilter createXPathFilter(final String xpathFilterExpression) {
        return this.createXPath(xpathFilterExpression);
    }
    
    public Pattern createPattern(final String xpathPattern) {
        return new XPathPattern(xpathPattern);
    }
    
    public List getQNames() {
        return this.cache.getQNames();
    }
    
    public Map getXPathNamespaceURIs() {
        return this.xpathNamespaceURIs;
    }
    
    public void setXPathNamespaceURIs(final Map namespaceURIs) {
        this.xpathNamespaceURIs = namespaceURIs;
    }
    
    protected static DocumentFactory createSingleton(final String className) {
        try {
            final Class theClass = Class.forName(className, true, ((DocumentFactory.class$org$dom4j$DocumentFactory == null) ? (DocumentFactory.class$org$dom4j$DocumentFactory = class$("com.newrelic.org.dom4j.DocumentFactory")) : DocumentFactory.class$org$dom4j$DocumentFactory).getClassLoader());
            return theClass.newInstance();
        }
        catch (Throwable e) {
            System.out.println("WARNING: Cannot load DocumentFactory: " + className);
            return new DocumentFactory();
        }
    }
    
    protected QName intern(final QName qname) {
        return this.cache.intern(qname);
    }
    
    protected QNameCache createQNameCache() {
        return new QNameCache(this);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init();
    }
    
    protected void init() {
        this.cache = this.createQNameCache();
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
    
    static {
        DocumentFactory.singleton = null;
    }
}
