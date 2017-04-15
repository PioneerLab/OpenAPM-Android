// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.util.concurrent;

import javax.annotation.Nullable;

public interface FutureCallback<V>
{
    void onSuccess(@Nullable final V p0);
    
    void onFailure(final Throwable p0);
}
