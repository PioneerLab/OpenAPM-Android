// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.cache;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import com.newrelic.com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public interface Weigher<K, V>
{
    int weigh(final K p0, final V p1);
}
