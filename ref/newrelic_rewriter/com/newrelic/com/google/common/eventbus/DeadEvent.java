// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.eventbus;

import com.newrelic.com.google.common.base.Preconditions;
import com.newrelic.com.google.common.annotations.Beta;

@Beta
public class DeadEvent
{
    private final Object source;
    private final Object event;
    
    public DeadEvent(final Object source, final Object event) {
        this.source = Preconditions.checkNotNull(source);
        this.event = Preconditions.checkNotNull(event);
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public Object getEvent() {
        return this.event;
    }
}
