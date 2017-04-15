// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.util;

import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.DocumentFactory;

public class IndexedDocumentFactory extends DocumentFactory
{
    protected static transient IndexedDocumentFactory singleton;
    
    public static DocumentFactory getInstance() {
        return IndexedDocumentFactory.singleton;
    }
    
    public Element createElement(final QName qname) {
        return new IndexedElement(qname);
    }
    
    public Element createElement(final QName qname, final int attributeCount) {
        return new IndexedElement(qname, attributeCount);
    }
    
    static {
        IndexedDocumentFactory.singleton = new IndexedDocumentFactory();
    }
}
