// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface Constraint<E>
{
    E checkElement(final E p0);
    
    String toString();
}
