// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.util;

import java.lang.ref.WeakReference;

public class PerThreadSingleton implements SingletonStrategy
{
    private String singletonClassName;
    private ThreadLocal perThreadCache;
    
    public PerThreadSingleton() {
        this.singletonClassName = null;
        this.perThreadCache = new ThreadLocal();
    }
    
    public void reset() {
        this.perThreadCache = new ThreadLocal();
    }
    
    public Object instance() {
        Object singletonInstancePerThread = null;
        final WeakReference ref = this.perThreadCache.get();
        if (ref == null || ref.get() == null) {
            Class clazz = null;
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(this.singletonClassName);
                singletonInstancePerThread = clazz.newInstance();
            }
            catch (Exception ignore) {
                try {
                    clazz = Class.forName(this.singletonClassName);
                    singletonInstancePerThread = clazz.newInstance();
                }
                catch (Exception ex) {}
            }
            this.perThreadCache.set(new WeakReference<Object>(singletonInstancePerThread));
        }
        else {
            singletonInstancePerThread = ref.get();
        }
        return singletonInstancePerThread;
    }
    
    public void setSingletonClassName(final String singletonClassName) {
        this.singletonClassName = singletonClassName;
    }
}
