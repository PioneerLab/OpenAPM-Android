// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import java.util.Iterator;

@GwtCompatible
public abstract class UnmodifiableIterator<E> implements Iterator<E>
{
    @Deprecated
    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}
