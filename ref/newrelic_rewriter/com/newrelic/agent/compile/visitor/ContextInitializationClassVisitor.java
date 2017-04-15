// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.agent.compile.InstrumentationContext;
import com.newrelic.org.objectweb.asm.ClassVisitor;

public class ContextInitializationClassVisitor extends ClassVisitor
{
    private final InstrumentationContext context;
    
    public ContextInitializationClassVisitor(final ClassVisitor cv, final InstrumentationContext context) {
        super(327680, cv);
        this.context = context;
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String sig, final String superName, final String[] interfaces) {
        this.context.setClassName(name);
        this.context.setSuperClassName(superName);
        super.visit(version, access, name, sig, superName, interfaces);
    }
}
