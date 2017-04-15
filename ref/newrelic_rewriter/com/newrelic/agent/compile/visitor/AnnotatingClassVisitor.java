// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import java.text.MessageFormat;
import com.newrelic.agent.compile.Log;
import com.newrelic.agent.compile.InstrumentationContext;
import com.newrelic.org.objectweb.asm.ClassVisitor;

public class AnnotatingClassVisitor extends ClassVisitor
{
    private final InstrumentationContext context;
    private final Log log;
    
    public AnnotatingClassVisitor(final ClassVisitor cv, final InstrumentationContext context, final Log log) {
        super(327680, cv);
        this.context = context;
        this.log = log;
    }
    
    @Override
    public void visitEnd() {
        if (this.context.isClassModified()) {
            this.context.addUniqueTag("Lcom/newrelic/agent/android/instrumentation/Instrumented;");
            super.visitAnnotation("Lcom/newrelic/agent/android/instrumentation/Instrumented;", false);
            this.log.info(MessageFormat.format("[{0}] tagging as instrumented", this.context.getFriendlyClassName()));
        }
        super.visitEnd();
    }
}
