// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Element;

public class DefaultText extends FlyweightText
{
    private Element parent;
    
    public DefaultText(final String text) {
        super(text);
    }
    
    public DefaultText(final Element parent, final String text) {
        super(text);
        this.parent = parent;
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
