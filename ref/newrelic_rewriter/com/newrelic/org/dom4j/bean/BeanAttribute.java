// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.bean;

import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.tree.AbstractAttribute;

public class BeanAttribute extends AbstractAttribute
{
    private final BeanAttributeList beanList;
    private final int index;
    
    public BeanAttribute(final BeanAttributeList beanList, final int index) {
        this.beanList = beanList;
        this.index = index;
    }
    
    public QName getQName() {
        return this.beanList.getQName(this.index);
    }
    
    public Element getParent() {
        return this.beanList.getParent();
    }
    
    public String getValue() {
        final Object data = this.getData();
        return (data != null) ? data.toString() : null;
    }
    
    public void setValue(final String data) {
        this.beanList.setData(this.index, data);
    }
    
    public Object getData() {
        return this.beanList.getData(this.index);
    }
    
    public void setData(final Object data) {
        this.beanList.setData(this.index, data);
    }
}
