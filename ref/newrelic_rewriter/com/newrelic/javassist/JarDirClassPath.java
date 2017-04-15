// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist;

import java.net.URL;
import java.io.InputStream;
import java.io.FilenameFilter;
import java.io.File;

final class JarDirClassPath implements ClassPath
{
    JarClassPath[] jars;
    
    JarDirClassPath(final String dirName) throws NotFoundException {
        final File[] files = new File(dirName).listFiles(new FilenameFilter() {
            public boolean accept(final File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jar") || name.endsWith(".zip");
            }
        });
        if (files != null) {
            this.jars = new JarClassPath[files.length];
            for (int i = 0; i < files.length; ++i) {
                this.jars[i] = new JarClassPath(files[i].getPath());
            }
        }
    }
    
    public InputStream openClassfile(final String classname) throws NotFoundException {
        if (this.jars != null) {
            for (int i = 0; i < this.jars.length; ++i) {
                final InputStream is = this.jars[i].openClassfile(classname);
                if (is != null) {
                    return is;
                }
            }
        }
        return null;
    }
    
    public URL find(final String classname) {
        if (this.jars != null) {
            for (int i = 0; i < this.jars.length; ++i) {
                final URL url = this.jars[i].find(classname);
                if (url != null) {
                    return url;
                }
            }
        }
        return null;
    }
    
    public void close() {
        if (this.jars != null) {
            for (int i = 0; i < this.jars.length; ++i) {
                this.jars[i].close();
            }
        }
    }
}
