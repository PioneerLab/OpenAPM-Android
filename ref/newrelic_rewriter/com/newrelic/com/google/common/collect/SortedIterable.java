// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.collect;

import java.util.Iterator;
import java.util.Comparator;
import com.newrelic.com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface SortedIterable<T> extends Iterable<T>
{
    Comparator<? super T> comparator();
    
    Iterator<T> iterator();
}
