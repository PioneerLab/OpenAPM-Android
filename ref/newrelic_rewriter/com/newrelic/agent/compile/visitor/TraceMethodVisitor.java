// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import java.util.Iterator;
import java.util.ArrayList;
import com.newrelic.org.objectweb.asm.Label;
import com.newrelic.org.objectweb.asm.commons.Method;
import com.newrelic.org.objectweb.asm.Type;
import com.newrelic.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.compile.Log;
import com.newrelic.agent.compile.InstrumentationContext;
import com.newrelic.org.objectweb.asm.commons.AdviceAdapter;

public class TraceMethodVisitor extends AdviceAdapter
{
    public static final String TRACE_MACHINE_INTERNAL_CLASSNAME = "com/newrelic/agent/android/tracing/TraceMachine";
    protected final InstrumentationContext context;
    protected final Log log;
    private String name;
    protected Boolean unloadContext;
    protected Boolean startTracing;
    private int access;
    
    public TraceMethodVisitor(final MethodVisitor mv, final int access, final String name, final String desc, final InstrumentationContext context) {
        super(327680, mv, access, name, desc);
        this.unloadContext = false;
        this.startTracing = false;
        this.access = access;
        this.context = context;
        this.log = context.getLog();
        this.name = name;
    }
    
    public void setUnloadContext() {
        this.unloadContext = true;
    }
    
    public void setStartTracing() {
        this.startTracing = true;
    }
    
    @Override
    protected void onMethodEnter() {
        final Type targetType = Type.getObjectType("com/newrelic/agent/android/tracing/TraceMachine");
        if (this.startTracing) {
            super.visitLdcInsn(this.context.getSimpleClassName());
            this.log.debug("Start tracing [" + this.context.getSimpleClassName() + "]");
            super.invokeStatic(targetType, new Method("startTracing", "(Ljava/lang/String;)V"));
        }
        if ((this.access & 0x8) != 0x0) {
            this.log.debug("Tracing static method [" + this.context.getClassName() + "#" + this.name + "]");
            super.visitInsn(1);
            super.visitLdcInsn(this.context.getSimpleClassName() + "#" + this.name);
            this.emitAnnotationParamsList(this.name);
            super.invokeStatic(targetType, new Method("enterMethod", "(Lcom/newrelic/agent/android/tracing/Trace;Ljava/lang/String;Ljava/util/ArrayList;)V"));
        }
        else {
            this.log.debug("Tracing method [" + this.context.getClassName() + "#" + this.name + "]");
            final Label tryStart = new Label();
            final Label tryEnd = new Label();
            final Label tryHandler = new Label();
            super.visitLabel(tryStart);
            super.loadThis();
            super.getField(Type.getObjectType(this.context.getClassName()), "_nr_trace", Type.getType("Lcom/newrelic/agent/android/tracing/Trace;"));
            super.visitLdcInsn(this.context.getSimpleClassName() + "#" + this.name);
            this.emitAnnotationParamsList(this.name);
            this.log.debug("Tracing: enterMethod [" + this.name + "]");
            super.invokeStatic(targetType, new Method("enterMethod", "(Lcom/newrelic/agent/android/tracing/Trace;Ljava/lang/String;Ljava/util/ArrayList;)V"));
            super.goTo(tryEnd);
            super.visitLabel(tryHandler);
            super.pop();
            super.visitInsn(1);
            super.visitLdcInsn(this.context.getSimpleClassName() + "#" + this.name);
            this.emitAnnotationParamsList(this.name);
            super.invokeStatic(targetType, new Method("enterMethod", "(Lcom/newrelic/agent/android/tracing/Trace;Ljava/lang/String;Ljava/util/ArrayList;)V"));
            super.visitLabel(tryEnd);
            super.visitTryCatchBlock(tryStart, tryEnd, tryHandler, "java/lang/NoSuchFieldError");
        }
    }
    
    private void emitAnnotationParamsList(final String name) {
        final ArrayList<String> annotationParameters = this.context.getTracedMethodParameters(name);
        if (annotationParameters == null || annotationParameters.size() == 0) {
            super.visitInsn(1);
            return;
        }
        final Method constructor = Method.getMethod("void <init> ()");
        final Method add = Method.getMethod("boolean add(java.lang.Object)");
        final Type arrayListType = Type.getObjectType("java/util/ArrayList");
        super.newInstance(arrayListType);
        super.dup();
        super.invokeConstructor(arrayListType, constructor);
        for (final String parameterEntry : annotationParameters) {
            super.dup();
            super.visitLdcInsn(parameterEntry);
            super.invokeVirtual(arrayListType, add);
            super.pop();
        }
    }
    
    @Override
    protected void onMethodExit(final int opcode) {
        Type targetType = Type.getObjectType("com/newrelic/agent/android/tracing/TraceMachine");
        super.invokeStatic(targetType, new Method("exitMethod", "()V"));
        this.log.debug("Tracing: exitMethod [" + this.name + "]");
        if (this.unloadContext) {
            super.loadThis();
            targetType = Type.getObjectType("com/newrelic/agent/android/tracing/TraceMachine");
            super.invokeStatic(targetType, new Method("unloadTraceContext", "(Ljava/lang/Object;)V"));
        }
    }
}
