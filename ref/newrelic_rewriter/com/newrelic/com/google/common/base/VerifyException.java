// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.base;

import javax.annotation.Nullable;
import com.newrelic.com.google.common.annotations.GwtCompatible;
import com.newrelic.com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public class VerifyException extends RuntimeException
{
    public VerifyException() {
    }
    
    public VerifyException(@Nullable final String message) {
        super(message);
    }
}
