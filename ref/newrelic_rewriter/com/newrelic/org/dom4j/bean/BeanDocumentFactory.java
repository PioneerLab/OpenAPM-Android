// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.bean;

import com.newrelic.org.dom4j.tree.DefaultAttribute;
import com.newrelic.org.dom4j.Attribute;
import org.xml.sax.Attributes;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.DocumentFactory;

public class BeanDocumentFactory extends DocumentFactory
{
    private static BeanDocumentFactory singleton;
    static /* synthetic */ Class class$org$dom4j$bean$BeanDocumentFactory;
    
    public static DocumentFactory getInstance() {
        return BeanDocumentFactory.singleton;
    }
    
    public Element createElement(final QName qname) {
        final Object bean = this.createBean(qname);
        if (bean == null) {
            return new BeanElement(qname);
        }
        return new BeanElement(qname, bean);
    }
    
    public Element createElement(final QName qname, final Attributes attributes) {
        final Object bean = this.createBean(qname, attributes);
        if (bean == null) {
            return new BeanElement(qname);
        }
        return new BeanElement(qname, bean);
    }
    
    public Attribute createAttribute(final Element owner, final QName qname, final String value) {
        return new DefaultAttribute(qname, value);
    }
    
    protected Object createBean(final QName qname) {
        return null;
    }
    
    protected Object createBean(final QName qname, final Attributes attributes) {
        final String value = attributes.getValue("class");
        if (value != null) {
            try {
                final Class beanClass = Class.forName(value, true, ((BeanDocumentFactory.class$org$dom4j$bean$BeanDocumentFactory == null) ? (BeanDocumentFactory.class$org$dom4j$bean$BeanDocumentFactory = class$("com.newrelic.org.dom4j.bean.BeanDocumentFactory")) : BeanDocumentFactory.class$org$dom4j$bean$BeanDocumentFactory).getClassLoader());
                return beanClass.newInstance();
            }
            catch (Exception e) {
                this.handleException(e);
            }
        }
        return null;
    }
    
    protected void handleException(final Exception e) {
        System.out.println("#### Warning: couldn't create bean: " + e);
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
        BeanDocumentFactory.singleton = new BeanDocumentFactory();
    }
}
