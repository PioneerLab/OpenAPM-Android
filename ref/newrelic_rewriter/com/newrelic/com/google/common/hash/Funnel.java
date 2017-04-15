// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.hash;

import com.newrelic.com.google.common.annotations.Beta;
import java.io.Serializable;

@Beta
public interface Funnel<T> extends Serializable
{
    void funnel(final T p0, final PrimitiveSink p1);
}
