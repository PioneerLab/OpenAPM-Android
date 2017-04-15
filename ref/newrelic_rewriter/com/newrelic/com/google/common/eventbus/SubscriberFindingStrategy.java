// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.eventbus;

import com.newrelic.com.google.common.collect.Multimap;

interface SubscriberFindingStrategy
{
    Multimap<Class<?>, EventSubscriber> findAllSubscribers(final Object p0);
}
