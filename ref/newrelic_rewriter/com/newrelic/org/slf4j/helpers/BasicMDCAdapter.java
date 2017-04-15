// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.slf4j.helpers;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import com.newrelic.org.slf4j.spi.MDCAdapter;

public class BasicMDCAdapter implements MDCAdapter
{
    private InheritableThreadLocal inheritableThreadLocal;
    
    public BasicMDCAdapter() {
        this.inheritableThreadLocal = new InheritableThreadLocal();
    }
    
    public void put(final String key, final String val) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        HashMap map = (HashMap)this.inheritableThreadLocal.get();
        if (map == null) {
            map = new HashMap();
            this.inheritableThreadLocal.set(map);
        }
        map.put(key, val);
    }
    
    public String get(final String key) {
        final HashMap hashMap = (HashMap)this.inheritableThreadLocal.get();
        if (hashMap != null && key != null) {
            return hashMap.get(key);
        }
        return null;
    }
    
    public void remove(final String key) {
        final HashMap map = (HashMap)this.inheritableThreadLocal.get();
        if (map != null) {
            map.remove(key);
        }
    }
    
    public void clear() {
        final HashMap hashMap = (HashMap)this.inheritableThreadLocal.get();
        if (hashMap != null) {
            hashMap.clear();
            this.inheritableThreadLocal.remove();
        }
    }
    
    public Set getKeys() {
        final HashMap hashMap = (HashMap)this.inheritableThreadLocal.get();
        if (hashMap != null) {
            return hashMap.keySet();
        }
        return null;
    }
    
    public Map getCopyOfContextMap() {
        final HashMap hashMap = (HashMap)this.inheritableThreadLocal.get();
        if (hashMap != null) {
            return new HashMap(hashMap);
        }
        return null;
    }
    
    public void setContextMap(final Map contextMap) {
        HashMap hashMap = (HashMap)this.inheritableThreadLocal.get();
        if (hashMap != null) {
            hashMap.clear();
            hashMap.putAll(contextMap);
        }
        else {
            hashMap = new HashMap(contextMap);
            this.inheritableThreadLocal.set(hashMap);
        }
    }
}
