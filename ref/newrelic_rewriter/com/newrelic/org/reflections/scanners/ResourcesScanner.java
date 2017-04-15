// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.scanners;

import com.newrelic.org.reflections.vfs.Vfs;

public class ResourcesScanner extends AbstractScanner
{
    public boolean acceptsInput(final String file) {
        return !file.endsWith(".class");
    }
    
    public void scan(final Vfs.File file) {
        this.getStore().put(file.getName(), file.getRelativePath());
    }
    
    public void scan(final Object cls) {
        throw new UnsupportedOperationException();
    }
}
