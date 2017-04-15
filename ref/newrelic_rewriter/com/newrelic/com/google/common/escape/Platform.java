// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.escape;

import com.newrelic.com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class Platform
{
    private static final ThreadLocal<char[]> DEST_TL;
    
    static char[] charBufferFromThreadLocal() {
        return Platform.DEST_TL.get();
    }
    
    static {
        DEST_TL = new ThreadLocal<char[]>() {
            @Override
            protected char[] initialValue() {
                return new char[1024];
            }
        };
    }
}
