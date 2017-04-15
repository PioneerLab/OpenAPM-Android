// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.util;

public class SimpleSingleton implements SingletonStrategy
{
    private String singletonClassName;
    private Object singletonInstance;
    
    public SimpleSingleton() {
        this.singletonClassName = null;
        this.singletonInstance = null;
    }
    
    public Object instance() {
        return this.singletonInstance;
    }
    
    public void reset() {
        if (this.singletonClassName != null) {
            Class clazz = null;
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(this.singletonClassName);
                this.singletonInstance = clazz.newInstance();
            }
            catch (Exception ignore) {
                try {
                    clazz = Class.forName(this.singletonClassName);
                    this.singletonInstance = clazz.newInstance();
                }
                catch (Exception ex) {}
            }
        }
    }
    
    public void setSingletonClassName(final String singletonClassName) {
        this.singletonClassName = singletonClassName;
        this.reset();
    }
}
