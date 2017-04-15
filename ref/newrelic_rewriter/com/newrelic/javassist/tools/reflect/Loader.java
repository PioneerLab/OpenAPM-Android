// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist.tools.reflect;

import com.newrelic.javassist.NotFoundException;
import com.newrelic.javassist.CannotCompileException;
import com.newrelic.javassist.Translator;
import com.newrelic.javassist.ClassPool;

public class Loader extends com.newrelic.javassist.Loader
{
    protected Reflection reflection;
    
    public static void main(final String[] args) throws Throwable {
        final Loader cl = new Loader();
        cl.run(args);
    }
    
    public Loader() throws CannotCompileException, NotFoundException {
        this.delegateLoadingOf("com.newrelic.javassist.tools.reflect.Loader");
        this.reflection = new Reflection();
        final ClassPool pool = ClassPool.getDefault();
        this.addTranslator(pool, this.reflection);
    }
    
    public boolean makeReflective(final String clazz, final String metaobject, final String metaclass) throws CannotCompileException, NotFoundException {
        return this.reflection.makeReflective(clazz, metaobject, metaclass);
    }
}
