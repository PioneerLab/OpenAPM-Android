// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections;

public class ReflectionsException extends RuntimeException
{
    public ReflectionsException(final String message) {
        super(message);
    }
    
    public ReflectionsException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ReflectionsException(final Throwable cause) {
        super(cause);
    }
}
