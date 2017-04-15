// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.util.concurrent;

import com.newrelic.com.google.common.annotations.Beta;
import java.util.concurrent.ScheduledFuture;

@Beta
public interface ListenableScheduledFuture<V> extends ScheduledFuture<V>, ListenableFuture<V>
{
}
