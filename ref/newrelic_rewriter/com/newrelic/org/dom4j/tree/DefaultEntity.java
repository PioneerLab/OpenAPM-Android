// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Element;

public class DefaultEntity extends FlyweightEntity
{
    private Element parent;
    
    public DefaultEntity(final String name) {
        super(name);
    }
    
    public DefaultEntity(final String name, final String text) {
        super(name, text);
    }
    
    public DefaultEntity(final Element parent, final String name, final String text) {
        super(name, text);
        this.parent = parent;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setText(final String text) {
        this.text = text;
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
