// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import java.util.ListIterator;

@GwtCompatible
public abstract class UnmodifiableListIterator<E> extends UnmodifiableIterator<E> implements ListIterator<E>
{
    @Deprecated
    @Override
    public final void add(final E e) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final void set(final E e) {
        throw new UnsupportedOperationException();
    }
}
