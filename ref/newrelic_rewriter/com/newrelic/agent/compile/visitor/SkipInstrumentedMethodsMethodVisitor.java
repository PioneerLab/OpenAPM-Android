// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.agent.compile.SkipException;
import com.newrelic.org.objectweb.asm.Type;
import com.newrelic.agent.compile.InstrumentedMethod;
import com.newrelic.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.org.objectweb.asm.MethodVisitor;

public class SkipInstrumentedMethodsMethodVisitor extends MethodVisitor
{
    public SkipInstrumentedMethodsMethodVisitor(final MethodVisitor mv) {
        super(327680, mv);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        if (Type.getDescriptor(InstrumentedMethod.class).equals(desc)) {
            throw new SkipException();
        }
        return super.visitAnnotation(desc, visible);
    }
}
