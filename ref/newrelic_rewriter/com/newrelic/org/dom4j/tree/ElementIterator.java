// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Element;
import java.util.Iterator;

public class ElementIterator extends FilterIterator
{
    public ElementIterator(final Iterator proxy) {
        super(proxy);
    }
    
    protected boolean matches(final Object element) {
        return element instanceof Element;
    }
}
