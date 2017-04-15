// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.util.concurrent;

import com.newrelic.com.google.common.annotations.Beta;

@Beta
public interface FutureFallback<V>
{
    ListenableFuture<V> create(final Throwable p0) throws Exception;
}
