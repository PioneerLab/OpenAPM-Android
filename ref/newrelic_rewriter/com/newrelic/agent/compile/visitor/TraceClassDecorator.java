// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.org.objectweb.asm.Label;
import com.newrelic.org.objectweb.asm.MethodVisitor;
import com.newrelic.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.org.objectweb.asm.commons.Method;
import com.newrelic.org.objectweb.asm.Type;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.newrelic.org.objectweb.asm.ClassVisitor;

public class TraceClassDecorator
{
    private ClassVisitor adapter;
    
    public TraceClassDecorator(final ClassVisitor adapter) {
        this.adapter = adapter;
    }
    
    public void addTraceField() {
        this.adapter.visitField(1, "_nr_trace", "Lcom/newrelic/agent/android/tracing/Trace;", null, null);
    }
    
    public static String[] addInterface(final String[] interfaces) {
        final ArrayList<String> newInterfaces = new ArrayList<String>(Arrays.asList(interfaces));
        newInterfaces.add("com/newrelic/agent/android/api/v2/TraceFieldInterface");
        return newInterfaces.toArray(new String[newInterfaces.size()]);
    }
    
    public void addTraceInterface(final Type ownerType) {
        final Method method = new Method("_nr_setTrace", "(Lcom/newrelic/agent/android/tracing/Trace;)V");
        MethodVisitor mv = this.adapter.visitMethod(1, method.getName(), method.getDescriptor(), null, null);
        mv = new GeneratorAdapter(327680, mv, 1, method.getName(), method.getDescriptor()) {
            @Override
            public void visitCode() {
                final Label tryStart = new Label();
                final Label tryEnd = new Label();
                final Label tryHandler = new Label();
                super.visitCode();
                this.visitLabel(tryStart);
                this.loadThis();
                this.loadArgs();
                this.putField(ownerType, "_nr_trace", Type.getType("Lcom/newrelic/agent/android/tracing/Trace;"));
                this.goTo(tryEnd);
                this.visitLabel(tryHandler);
                this.pop();
                this.visitLabel(tryEnd);
                this.visitTryCatchBlock(tryStart, tryEnd, tryHandler, "java/lang/Exception");
                this.visitInsn(177);
            }
        };
        mv.visitCode();
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}
