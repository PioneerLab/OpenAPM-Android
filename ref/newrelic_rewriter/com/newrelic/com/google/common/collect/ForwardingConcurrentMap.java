// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.collect;

import java.util.Map;
import com.newrelic.com.google.common.annotations.GwtCompatible;
import java.util.concurrent.ConcurrentMap;

@GwtCompatible
public abstract class ForwardingConcurrentMap<K, V> extends ForwardingMap<K, V> implements ConcurrentMap<K, V>
{
    @Override
    protected abstract ConcurrentMap<K, V> delegate();
    
    @Override
    public V putIfAbsent(final K key, final V value) {
        return this.delegate().putIfAbsent(key, value);
    }
    
    @Override
    public boolean remove(final Object key, final Object value) {
        return this.delegate().remove(key, value);
    }
    
    @Override
    public V replace(final K key, final V value) {
        return this.delegate().replace(key, value);
    }
    
    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        return this.delegate().replace(key, oldValue, newValue);
    }
}
