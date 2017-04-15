// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.eventbus;

public interface SubscriberExceptionHandler
{
    void handleException(final Throwable p0, final SubscriberExceptionContext p1);
}
