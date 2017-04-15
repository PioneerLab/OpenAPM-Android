// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.compile.Log;
import com.newrelic.agent.compile.InstrumentationContext;
import com.newrelic.org.objectweb.asm.ClassVisitor;

public class TraceAnnotationClassVisitor extends ClassVisitor
{
    private final InstrumentationContext context;
    
    public TraceAnnotationClassVisitor(final ClassVisitor cv, final InstrumentationContext context, final Log log) {
        super(327680, cv);
        this.context = context;
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        if (this.context.isTracedMethod(name, desc) & !this.context.isSkippedMethod(name, desc)) {
            this.context.markModified();
            return new TraceMethodVisitor(methodVisitor, access, name, desc, this.context);
        }
        return methodVisitor;
    }
}
