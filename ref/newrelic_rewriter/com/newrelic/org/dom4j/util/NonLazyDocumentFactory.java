// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.util;

import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.DocumentFactory;

public class NonLazyDocumentFactory extends DocumentFactory
{
    protected static transient NonLazyDocumentFactory singleton;
    
    public static DocumentFactory getInstance() {
        return NonLazyDocumentFactory.singleton;
    }
    
    public Element createElement(final QName qname) {
        return new NonLazyElement(qname);
    }
    
    static {
        NonLazyDocumentFactory.singleton = new NonLazyDocumentFactory();
    }
}
