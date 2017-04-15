// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.collect;

import java.util.Map;
import com.newrelic.com.google.common.base.Predicate;
import com.newrelic.com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface FilteredMultimap<K, V> extends Multimap<K, V>
{
    Multimap<K, V> unfiltered();
    
    Predicate<? super Map.Entry<K, V>> entryPredicate();
}
