// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import com.newrelic.agent.compile.visitor.SkipInstrumentedMethodsMethodVisitor;
import com.newrelic.org.objectweb.asm.MethodVisitor;
import com.newrelic.org.objectweb.asm.commons.Method;
import java.util.Map;
import com.newrelic.org.objectweb.asm.ClassVisitor;

public class ClassAdapterBase extends ClassVisitor
{
    final Map<Method, MethodVisitorFactory> methodVisitors;
    private final Log log;
    
    public ClassAdapterBase(final Log log, final ClassVisitor cv, final Map<Method, MethodVisitorFactory> methodVisitors) {
        super(327680, cv);
        this.methodVisitors = methodVisitors;
        this.log = log;
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        final MethodVisitorFactory factory = this.methodVisitors.get(new Method(name, desc));
        if (factory != null) {
            return new SkipInstrumentedMethodsMethodVisitor(factory.create(mv, access, name, desc));
        }
        return mv;
    }
}
