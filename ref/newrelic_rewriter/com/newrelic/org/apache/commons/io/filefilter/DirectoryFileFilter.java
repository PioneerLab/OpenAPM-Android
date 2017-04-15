// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;

public class DirectoryFileFilter extends AbstractFileFilter implements Serializable
{
    public static final IOFileFilter DIRECTORY;
    public static final IOFileFilter INSTANCE;
    
    @Override
    public boolean accept(final File file) {
        return file.isDirectory();
    }
    
    static {
        DIRECTORY = new DirectoryFileFilter();
        INSTANCE = DirectoryFileFilter.DIRECTORY;
    }
}
