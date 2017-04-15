// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import com.newrelic.org.objectweb.asm.MethodVisitor;

public interface MethodVisitorFactory
{
    MethodVisitor create(final MethodVisitor p0, final int p1, final String p2, final String p3);
}
