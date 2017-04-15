// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.collect;

import java.util.SortedSet;

interface SortedMultisetBridge<E> extends Multiset<E>
{
    SortedSet<E> elementSet();
}
