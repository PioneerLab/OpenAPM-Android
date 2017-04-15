// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;

public class TrueFileFilter implements IOFileFilter, Serializable
{
    public static final IOFileFilter TRUE;
    public static final IOFileFilter INSTANCE;
    
    @Override
    public boolean accept(final File file) {
        return true;
    }
    
    @Override
    public boolean accept(final File dir, final String name) {
        return true;
    }
    
    static {
        TRUE = new TrueFileFilter();
        INSTANCE = TrueFileFilter.TRUE;
    }
}
