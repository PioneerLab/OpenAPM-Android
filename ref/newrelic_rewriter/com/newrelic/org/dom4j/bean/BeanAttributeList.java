// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.bean;

import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.Attribute;
import java.util.AbstractList;

public class BeanAttributeList extends AbstractList
{
    private BeanElement parent;
    private BeanMetaData beanMetaData;
    private BeanAttribute[] attributes;
    
    public BeanAttributeList(final BeanElement parent, final BeanMetaData beanMetaData) {
        this.parent = parent;
        this.beanMetaData = beanMetaData;
        this.attributes = new BeanAttribute[beanMetaData.attributeCount()];
    }
    
    public BeanAttributeList(final BeanElement parent) {
        this.parent = parent;
        final Object data = parent.getData();
        final Class beanClass = (data != null) ? data.getClass() : null;
        this.beanMetaData = BeanMetaData.get(beanClass);
        this.attributes = new BeanAttribute[this.beanMetaData.attributeCount()];
    }
    
    public Attribute attribute(final String name) {
        final int index = this.beanMetaData.getIndex(name);
        return this.attribute(index);
    }
    
    public Attribute attribute(final QName qname) {
        final int index = this.beanMetaData.getIndex(qname);
        return this.attribute(index);
    }
    
    public BeanAttribute attribute(final int index) {
        if (index >= 0 && index <= this.attributes.length) {
            BeanAttribute attribute = this.attributes[index];
            if (attribute == null) {
                attribute = this.createAttribute(this.parent, index);
                this.attributes[index] = attribute;
            }
            return attribute;
        }
        return null;
    }
    
    public BeanElement getParent() {
        return this.parent;
    }
    
    public QName getQName(final int index) {
        return this.beanMetaData.getQName(index);
    }
    
    public Object getData(final int index) {
        return this.beanMetaData.getData(index, this.parent.getData());
    }
    
    public void setData(final int index, final Object data) {
        this.beanMetaData.setData(index, this.parent.getData(), data);
    }
    
    public int size() {
        return this.attributes.length;
    }
    
    public Object get(final int index) {
        BeanAttribute attribute = this.attributes[index];
        if (attribute == null) {
            attribute = this.createAttribute(this.parent, index);
            this.attributes[index] = attribute;
        }
        return attribute;
    }
    
    public boolean add(final Object object) {
        throw new UnsupportedOperationException("add(Object) unsupported");
    }
    
    public void add(final int index, final Object object) {
        throw new UnsupportedOperationException("add(int,Object) unsupported");
    }
    
    public Object set(final int index, final Object object) {
        throw new UnsupportedOperationException("set(int,Object) unsupported");
    }
    
    public boolean remove(final Object object) {
        return false;
    }
    
    public Object remove(final int index) {
        final BeanAttribute attribute = (BeanAttribute)this.get(index);
        final Object oldValue = attribute.getValue();
        attribute.setValue(null);
        return oldValue;
    }
    
    public void clear() {
        for (int i = 0, size = this.attributes.length; i < size; ++i) {
            final BeanAttribute attribute = this.attributes[i];
            if (attribute != null) {
                attribute.setValue(null);
            }
        }
    }
    
    protected BeanAttribute createAttribute(final BeanElement element, final int index) {
        return new BeanAttribute(this, index);
    }
}
