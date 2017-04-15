// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

public class HaltBuildException extends RuntimeException
{
    public HaltBuildException(final String message) {
        super(message);
    }
    
    public HaltBuildException(final Exception e) {
        super(e);
    }
    
    public HaltBuildException(final String message, final Exception e) {
        super(message, e);
    }
}
