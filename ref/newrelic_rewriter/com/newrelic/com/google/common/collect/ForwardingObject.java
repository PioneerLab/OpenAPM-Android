// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class ForwardingObject
{
    protected abstract Object delegate();
    
    @Override
    public String toString() {
        return this.delegate().toString();
    }
}
