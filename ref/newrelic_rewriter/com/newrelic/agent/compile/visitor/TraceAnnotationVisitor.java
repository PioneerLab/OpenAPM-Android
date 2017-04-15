// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.org.objectweb.asm.Type;
import com.newrelic.agent.compile.InstrumentationContext;
import com.newrelic.agent.compile.Log;
import com.newrelic.agent.util.AnnotationImpl;

public class TraceAnnotationVisitor extends AnnotationImpl
{
    final Log log;
    final InstrumentationContext context;
    
    public TraceAnnotationVisitor(final String name, final InstrumentationContext context) {
        super(name);
        this.context = context;
        this.log = context.getLog();
    }
    
    @Override
    public void visitEnum(final String parameterName, final String desc, final String value) {
        super.visitEnum(parameterName, desc, value);
        final String className = Type.getType(desc).getClassName();
        this.context.addTracedMethodParameter(this.getName(), parameterName, className, value);
    }
    
    @Override
    public void visit(final String parameterName, final Object value) {
        super.visit(parameterName, value);
        final String className = value.getClass().getName();
        this.context.addTracedMethodParameter(this.getName(), parameterName, className, value.toString());
    }
}
