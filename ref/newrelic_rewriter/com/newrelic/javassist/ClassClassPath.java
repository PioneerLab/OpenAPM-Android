// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist;

import java.net.URL;
import java.io.InputStream;

public class ClassClassPath implements ClassPath
{
    private Class thisClass;
    static /* synthetic */ Class class$java$lang$Object;
    
    public ClassClassPath(final Class c) {
        this.thisClass = c;
    }
    
    ClassClassPath() {
        this((ClassClassPath.class$java$lang$Object == null) ? (ClassClassPath.class$java$lang$Object = class$("java.lang.Object")) : ClassClassPath.class$java$lang$Object);
    }
    
    public InputStream openClassfile(final String classname) {
        final String jarname = "/" + classname.replace('.', '/') + ".class";
        return this.thisClass.getResourceAsStream(jarname);
    }
    
    public URL find(final String classname) {
        final String jarname = "/" + classname.replace('.', '/') + ".class";
        return this.thisClass.getResource(jarname);
    }
    
    public void close() {
    }
    
    public String toString() {
        return this.thisClass.getName() + ".class";
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
}
