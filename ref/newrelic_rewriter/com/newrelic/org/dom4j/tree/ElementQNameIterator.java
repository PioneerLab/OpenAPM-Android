// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Element;
import java.util.Iterator;
import com.newrelic.org.dom4j.QName;

public class ElementQNameIterator extends FilterIterator
{
    private QName qName;
    
    public ElementQNameIterator(final Iterator proxy, final QName qName) {
        super(proxy);
        this.qName = qName;
    }
    
    protected boolean matches(final Object object) {
        if (object instanceof Element) {
            final Element element = (Element)object;
            return this.qName.equals(element.getQName());
        }
        return false;
    }
}
