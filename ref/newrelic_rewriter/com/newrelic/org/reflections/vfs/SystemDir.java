// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.vfs;

import com.newrelic.com.google.common.collect.Lists;
import java.util.List;
import java.util.Collection;
import java.util.Stack;
import com.newrelic.com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import java.io.File;

public class SystemDir implements Vfs.Dir
{
    private final java.io.File file;
    
    public SystemDir(final java.io.File file) {
        if (file == null || !file.exists() || !file.isDirectory() || !file.canRead()) {
            throw new RuntimeException("cannot use dir " + file);
        }
        this.file = file;
    }
    
    public String getPath() {
        return this.file.getPath().replace("\\", "/");
    }
    
    public Iterable<Vfs.File> getFiles() {
        return new Iterable<Vfs.File>() {
            public Iterator<Vfs.File> iterator() {
                return new AbstractIterator<Vfs.File>() {
                    final Stack<java.io.File> stack;
                    
                    {
                        (this.stack = new Stack<java.io.File>()).addAll((Collection<?>)listFiles(SystemDir.this.file));
                    }
                    
                    protected Vfs.File computeNext() {
                        while (!this.stack.isEmpty()) {
                            final java.io.File file = this.stack.pop();
                            if (!file.isDirectory()) {
                                return new SystemFile(SystemDir.this, file);
                            }
                            this.stack.addAll((Collection<?>)listFiles(file));
                        }
                        return this.endOfData();
                    }
                };
            }
        };
    }
    
    private static List<java.io.File> listFiles(final java.io.File file) {
        final java.io.File[] files = file.listFiles();
        if (files != null) {
            return Lists.newArrayList(files);
        }
        return (List<java.io.File>)Lists.newArrayList();
    }
    
    public void close() {
    }
    
    public String toString() {
        return this.file.toString();
    }
}
