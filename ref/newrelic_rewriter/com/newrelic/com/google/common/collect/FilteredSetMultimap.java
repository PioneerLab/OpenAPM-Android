// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface FilteredSetMultimap<K, V> extends FilteredMultimap<K, V>, SetMultimap<K, V>
{
    SetMultimap<K, V> unfiltered();
}
