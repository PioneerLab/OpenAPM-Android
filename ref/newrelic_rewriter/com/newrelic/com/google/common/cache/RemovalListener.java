// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.cache;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import com.newrelic.com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public interface RemovalListener<K, V>
{
    void onRemoval(final RemovalNotification<K, V> p0);
}
