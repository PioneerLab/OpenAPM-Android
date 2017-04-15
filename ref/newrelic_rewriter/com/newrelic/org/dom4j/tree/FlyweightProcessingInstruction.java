// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.Element;
import java.util.Collections;
import java.util.Map;

public class FlyweightProcessingInstruction extends AbstractProcessingInstruction
{
    protected String target;
    protected String text;
    protected Map values;
    
    public FlyweightProcessingInstruction() {
    }
    
    public FlyweightProcessingInstruction(final String target, final Map values) {
        this.target = target;
        this.values = values;
        this.text = this.toString(values);
    }
    
    public FlyweightProcessingInstruction(final String target, final String text) {
        this.target = target;
        this.text = text;
        this.values = this.parseValues(text);
    }
    
    public String getTarget() {
        return this.target;
    }
    
    public void setTarget(final String target) {
        throw new UnsupportedOperationException("This PI is read-only and cannot be modified");
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getValue(final String name) {
        final String answer = this.values.get(name);
        if (answer == null) {
            return "";
        }
        return answer;
    }
    
    public Map getValues() {
        return Collections.unmodifiableMap((Map<?, ?>)this.values);
    }
    
    protected Node createXPathResult(final Element parent) {
        return new DefaultProcessingInstruction(parent, this.getTarget(), this.getText());
    }
}
