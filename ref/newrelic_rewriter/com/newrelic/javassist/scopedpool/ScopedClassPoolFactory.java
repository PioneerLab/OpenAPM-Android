// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist.scopedpool;

import com.newrelic.javassist.ClassPool;

public interface ScopedClassPoolFactory
{
    ScopedClassPool create(final ClassLoader p0, final ClassPool p1, final ScopedClassPoolRepository p2);
    
    ScopedClassPool create(final ClassPool p0, final ScopedClassPoolRepository p1);
}
