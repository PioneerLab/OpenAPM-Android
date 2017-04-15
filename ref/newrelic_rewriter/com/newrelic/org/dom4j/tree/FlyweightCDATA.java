// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.CDATA;

public class FlyweightCDATA extends AbstractCDATA implements CDATA
{
    protected String text;
    
    public FlyweightCDATA(final String text) {
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
    
    protected Node createXPathResult(final Element parent) {
        return new DefaultCDATA(parent, this.getText());
    }
}
