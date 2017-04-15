// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.base;

import javax.annotation.Nullable;
import com.newrelic.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface Function<F, T>
{
    @Nullable
    T apply(@Nullable final F p0);
    
    boolean equals(@Nullable final Object p0);
}
