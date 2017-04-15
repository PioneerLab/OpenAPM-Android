// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import java.util.NoSuchElementException;
import java.util.Iterator;

public abstract class FilterIterator implements Iterator
{
    protected Iterator proxy;
    private Object next;
    private boolean first;
    
    public FilterIterator(final Iterator proxy) {
        this.first = true;
        this.proxy = proxy;
    }
    
    public boolean hasNext() {
        if (this.first) {
            this.next = this.findNext();
            this.first = false;
        }
        return this.next != null;
    }
    
    public Object next() throws NoSuchElementException {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        final Object answer = this.next;
        this.next = this.findNext();
        return answer;
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    protected abstract boolean matches(final Object p0);
    
    protected Object findNext() {
        if (this.proxy != null) {
            while (this.proxy.hasNext()) {
                final Object nextObject = this.proxy.next();
                if (nextObject != null && this.matches(nextObject)) {
                    return nextObject;
                }
            }
            this.proxy = null;
        }
        return null;
    }
}
