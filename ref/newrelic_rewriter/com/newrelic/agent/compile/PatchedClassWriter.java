// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import com.newrelic.org.objectweb.asm.ClassWriter;

public class PatchedClassWriter extends ClassWriter
{
    private final ClassLoader classLoader;
    
    public PatchedClassWriter(final int flags, final ClassLoader classLoader) {
        super(flags);
        this.classLoader = classLoader;
    }
    
    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        Class c;
        Class d;
        try {
            c = Class.forName(type1.replace('/', '.'), true, this.classLoader);
            d = Class.forName(type2.replace('/', '.'), true, this.classLoader);
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        if (c.isAssignableFrom(d)) {
            return type1;
        }
        if (d.isAssignableFrom(c)) {
            return type2;
        }
        if (c.isInterface() || d.isInterface()) {
            return "java/lang/Object";
        }
        do {
            c = c.getSuperclass();
        } while (!c.isAssignableFrom(d));
        return c.getName().replace('.', '/');
    }
}
