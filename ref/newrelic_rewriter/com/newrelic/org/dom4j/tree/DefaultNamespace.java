// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Namespace;

public class DefaultNamespace extends Namespace
{
    private Element parent;
    
    public DefaultNamespace(final String prefix, final String uri) {
        super(prefix, uri);
    }
    
    public DefaultNamespace(final Element parent, final String prefix, final String uri) {
        super(prefix, uri);
        this.parent = parent;
    }
    
    protected int createHashCode() {
        int hashCode = super.createHashCode();
        if (this.parent != null) {
            hashCode ^= this.parent.hashCode();
        }
        return hashCode;
    }
    
    public boolean equals(final Object object) {
        if (object instanceof DefaultNamespace) {
            final DefaultNamespace that = (DefaultNamespace)object;
            if (that.parent == this.parent) {
                return super.equals(object);
            }
        }
        return false;
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    public Element getParent() {
        return this.parent;
    }
    
    public void setParent(final Element parent) {
        this.parent = parent;
    }
    
    public boolean supportsParent() {
        return true;
    }
    
    public boolean isReadOnly() {
        return false;
    }
}
