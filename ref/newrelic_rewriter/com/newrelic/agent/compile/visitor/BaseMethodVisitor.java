// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.org.objectweb.asm.Type;
import com.newrelic.agent.compile.InstrumentedMethod;
import com.newrelic.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.util.BytecodeBuilder;
import com.newrelic.org.objectweb.asm.commons.AdviceAdapter;

public abstract class BaseMethodVisitor extends AdviceAdapter
{
    protected final String methodName;
    protected final BytecodeBuilder builder;
    
    protected BaseMethodVisitor(final MethodVisitor mv, final int access, final String methodName, final String desc) {
        super(327680, mv, access, methodName, desc);
        this.builder = new BytecodeBuilder(this);
        this.methodName = methodName;
    }
    
    @Override
    public void visitEnd() {
        super.visitAnnotation(Type.getDescriptor(InstrumentedMethod.class), false);
        super.visitEnd();
    }
}
