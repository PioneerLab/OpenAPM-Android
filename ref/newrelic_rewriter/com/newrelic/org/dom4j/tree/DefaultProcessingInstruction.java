// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import java.util.Map;
import com.newrelic.org.dom4j.Element;

public class DefaultProcessingInstruction extends FlyweightProcessingInstruction
{
    private Element parent;
    
    public DefaultProcessingInstruction(final String target, final Map values) {
        super(target, values);
    }
    
    public DefaultProcessingInstruction(final String target, final String values) {
        super(target, values);
    }
    
    public DefaultProcessingInstruction(final Element parent, final String target, final String values) {
        super(target, values);
        this.parent = parent;
    }
    
    public void setTarget(final String target) {
        this.target = target;
    }
    
    public void setText(final String text) {
        this.text = text;
        this.values = this.parseValues(text);
    }
    
    public void setValues(final Map values) {
        this.values = values;
        this.text = this.toString(values);
    }
    
    public void setValue(final String name, final String value) {
        this.values.put(name, value);
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
