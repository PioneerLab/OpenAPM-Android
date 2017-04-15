// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Element;
import java.util.Iterator;

public class ElementNameIterator extends FilterIterator
{
    private String name;
    
    public ElementNameIterator(final Iterator proxy, final String name) {
        super(proxy);
        this.name = name;
    }
    
    protected boolean matches(final Object object) {
        if (object instanceof Element) {
            final Element element = (Element)object;
            return this.name.equals(element.getName());
        }
        return false;
    }
}
