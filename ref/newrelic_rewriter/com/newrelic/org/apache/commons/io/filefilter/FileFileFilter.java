// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;

public class FileFileFilter extends AbstractFileFilter implements Serializable
{
    public static final IOFileFilter FILE;
    
    @Override
    public boolean accept(final File file) {
        return file.isFile();
    }
    
    static {
        FILE = new FileFileFilter();
    }
}
