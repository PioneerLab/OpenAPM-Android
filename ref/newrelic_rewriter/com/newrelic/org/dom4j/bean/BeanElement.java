// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.bean;

import com.newrelic.org.dom4j.tree.NamespaceStack;
import org.xml.sax.Attributes;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Attribute;
import java.util.List;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.DocumentFactory;
import com.newrelic.org.dom4j.tree.DefaultElement;

public class BeanElement extends DefaultElement
{
    private static final DocumentFactory DOCUMENT_FACTORY;
    private Object bean;
    static /* synthetic */ Class class$org$dom4j$bean$BeanElement;
    
    public BeanElement(final String name, final Object bean) {
        this(BeanElement.DOCUMENT_FACTORY.createQName(name), bean);
    }
    
    public BeanElement(final String name, final Namespace namespace, final Object bean) {
        this(BeanElement.DOCUMENT_FACTORY.createQName(name, namespace), bean);
    }
    
    public BeanElement(final QName qname, final Object bean) {
        super(qname);
        this.bean = bean;
    }
    
    public BeanElement(final QName qname) {
        super(qname);
    }
    
    public Object getData() {
        return this.bean;
    }
    
    public void setData(final Object data) {
        this.bean = data;
        this.setAttributeList(null);
    }
    
    public Attribute attribute(final String name) {
        return this.getBeanAttributeList().attribute(name);
    }
    
    public Attribute attribute(final QName qname) {
        return this.getBeanAttributeList().attribute(qname);
    }
    
    public Element addAttribute(final String name, final String value) {
        final Attribute attribute = this.attribute(name);
        if (attribute != null) {
            attribute.setValue(value);
        }
        return this;
    }
    
    public Element addAttribute(final QName qName, final String value) {
        final Attribute attribute = this.attribute(qName);
        if (attribute != null) {
            attribute.setValue(value);
        }
        return this;
    }
    
    public void setAttributes(final List attributes) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    public void setAttributes(final Attributes attributes, final NamespaceStack namespaceStack, final boolean noNamespaceAttributes) {
        final String className = attributes.getValue("class");
        if (className != null) {
            try {
                final Class beanClass = Class.forName(className, true, ((BeanElement.class$org$dom4j$bean$BeanElement == null) ? (BeanElement.class$org$dom4j$bean$BeanElement = class$("com.newrelic.org.dom4j.bean.BeanElement")) : BeanElement.class$org$dom4j$bean$BeanElement).getClassLoader());
                this.setData(beanClass.newInstance());
                for (int i = 0; i < attributes.getLength(); ++i) {
                    final String attributeName = attributes.getLocalName(i);
                    if (!"class".equalsIgnoreCase(attributeName)) {
                        this.addAttribute(attributeName, attributes.getValue(i));
                    }
                }
            }
            catch (Exception ex) {
                ((BeanDocumentFactory)this.getDocumentFactory()).handleException(ex);
            }
        }
        else {
            super.setAttributes(attributes, namespaceStack, noNamespaceAttributes);
        }
    }
    
    protected DocumentFactory getDocumentFactory() {
        return BeanElement.DOCUMENT_FACTORY;
    }
    
    protected BeanAttributeList getBeanAttributeList() {
        return (BeanAttributeList)this.attributeList();
    }
    
    protected List createAttributeList() {
        return new BeanAttributeList(this);
    }
    
    protected List createAttributeList(final int size) {
        return new BeanAttributeList(this);
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
        DOCUMENT_FACTORY = BeanDocumentFactory.getInstance();
    }
}
