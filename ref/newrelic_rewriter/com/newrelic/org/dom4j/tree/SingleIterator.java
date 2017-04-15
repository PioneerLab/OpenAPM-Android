// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import java.util.Iterator;

public class SingleIterator implements Iterator
{
    private boolean first;
    private Object object;
    
    public SingleIterator(final Object object) {
        this.first = true;
        this.object = object;
    }
    
    public boolean hasNext() {
        return this.first;
    }
    
    public Object next() {
        final Object answer = this.object;
        this.object = null;
        this.first = false;
        return answer;
    }
    
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported by this iterator");
    }
}
