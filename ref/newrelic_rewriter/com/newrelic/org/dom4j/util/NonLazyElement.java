// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.util;

import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.tree.BaseElement;

public class NonLazyElement extends BaseElement
{
    public NonLazyElement(final String name) {
        super(name);
        this.attributes = this.createAttributeList();
        this.content = this.createContentList();
    }
    
    public NonLazyElement(final QName qname) {
        super(qname);
        this.attributes = this.createAttributeList();
        this.content = this.createContentList();
    }
    
    public NonLazyElement(final String name, final Namespace namespace) {
        super(name, namespace);
        this.attributes = this.createAttributeList();
        this.content = this.createContentList();
    }
    
    public NonLazyElement(final QName qname, final int attributeCount) {
        super(qname);
        this.attributes = this.createAttributeList(attributeCount);
        this.content = this.createContentList();
    }
}
