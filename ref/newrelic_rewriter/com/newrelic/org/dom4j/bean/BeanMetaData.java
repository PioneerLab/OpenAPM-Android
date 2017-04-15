// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.HashMap;
import java.lang.reflect.Method;
import com.newrelic.org.dom4j.QName;
import java.beans.PropertyDescriptor;
import com.newrelic.org.dom4j.DocumentFactory;
import java.util.Map;

public class BeanMetaData
{
    protected static final Object[] NULL_ARGS;
    private static Map singletonCache;
    private static final DocumentFactory DOCUMENT_FACTORY;
    private Class beanClass;
    private PropertyDescriptor[] propertyDescriptors;
    private QName[] qNames;
    private Method[] readMethods;
    private Method[] writeMethods;
    private Map nameMap;
    
    public BeanMetaData(final Class beanClass) {
        this.nameMap = new HashMap();
        this.beanClass = beanClass;
        if (beanClass != null) {
            try {
                final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
                this.propertyDescriptors = beanInfo.getPropertyDescriptors();
            }
            catch (IntrospectionException e) {
                this.handleException(e);
            }
        }
        if (this.propertyDescriptors == null) {
            this.propertyDescriptors = new PropertyDescriptor[0];
        }
        final int size = this.propertyDescriptors.length;
        this.qNames = new QName[size];
        this.readMethods = new Method[size];
        this.writeMethods = new Method[size];
        for (int i = 0; i < size; ++i) {
            final PropertyDescriptor propertyDescriptor = this.propertyDescriptors[i];
            final String name = propertyDescriptor.getName();
            final QName qName = BeanMetaData.DOCUMENT_FACTORY.createQName(name);
            this.qNames[i] = qName;
            this.readMethods[i] = propertyDescriptor.getReadMethod();
            this.writeMethods[i] = propertyDescriptor.getWriteMethod();
            final Integer index = new Integer(i);
            this.nameMap.put(name, index);
            this.nameMap.put(qName, index);
        }
    }
    
    public static BeanMetaData get(final Class beanClass) {
        BeanMetaData answer = BeanMetaData.singletonCache.get(beanClass);
        if (answer == null) {
            answer = new BeanMetaData(beanClass);
            BeanMetaData.singletonCache.put(beanClass, answer);
        }
        return answer;
    }
    
    public int attributeCount() {
        return this.propertyDescriptors.length;
    }
    
    public BeanAttributeList createAttributeList(final BeanElement parent) {
        return new BeanAttributeList(parent, this);
    }
    
    public QName getQName(final int index) {
        return this.qNames[index];
    }
    
    public int getIndex(final String name) {
        final Integer index = this.nameMap.get(name);
        return (index != null) ? index : -1;
    }
    
    public int getIndex(final QName qName) {
        final Integer index = this.nameMap.get(qName);
        return (index != null) ? index : -1;
    }
    
    public Object getData(final int index, final Object bean) {
        try {
            final Method method = this.readMethods[index];
            return method.invoke(bean, BeanMetaData.NULL_ARGS);
        }
        catch (Exception e) {
            this.handleException(e);
            return null;
        }
    }
    
    public void setData(final int index, final Object bean, final Object data) {
        try {
            final Method method = this.writeMethods[index];
            final Object[] args = { data };
            method.invoke(bean, args);
        }
        catch (Exception e) {
            this.handleException(e);
        }
    }
    
    protected void handleException(final Exception e) {
    }
    
    static {
        NULL_ARGS = new Object[0];
        BeanMetaData.singletonCache = new HashMap();
        DOCUMENT_FACTORY = BeanDocumentFactory.getInstance();
    }
}
