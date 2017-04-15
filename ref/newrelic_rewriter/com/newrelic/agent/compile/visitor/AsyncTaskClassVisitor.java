// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.org.objectweb.asm.MethodVisitor;
import com.newrelic.org.objectweb.asm.Type;
import com.newrelic.com.google.common.collect.ImmutableMap;
import com.newrelic.agent.compile.Log;
import com.newrelic.agent.compile.InstrumentationContext;
import com.newrelic.org.objectweb.asm.ClassVisitor;

public class AsyncTaskClassVisitor extends ClassVisitor
{
    public static final String TARGET_CLASS = "android/os/AsyncTask";
    private final InstrumentationContext context;
    private final Log log;
    private boolean instrument;
    public static final ImmutableMap<String, String> traceMethodMap;
    public static final ImmutableMap<String, String> endTraceMethodMap;
    
    public AsyncTaskClassVisitor(final ClassVisitor cv, final InstrumentationContext context, final Log log) {
        super(327680, cv);
        this.instrument = false;
        this.context = context;
        this.log = log;
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, String[] interfaces) {
        if (superName != null && superName.equals("android/os/AsyncTask")) {
            interfaces = TraceClassDecorator.addInterface(interfaces);
            super.visit(version, access, name, signature, superName, interfaces);
            this.instrument = true;
            this.log.debug("AsyncTaskClassVisitor: Rewriting " + this.context.getClassName());
            this.context.markModified();
        }
        else {
            super.visit(version, access, name, signature, superName, interfaces);
        }
    }
    
    @Override
    public void visitEnd() {
        if (this.instrument) {
            final TraceClassDecorator decorator = new TraceClassDecorator(this);
            decorator.addTraceField();
            decorator.addTraceInterface(Type.getObjectType(this.context.getClassName()));
            this.log.info("Added Trace object and interface to " + this.context.getClassName());
        }
        super.visitEnd();
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        if (this.instrument) {
            if (AsyncTaskClassVisitor.traceMethodMap.containsKey(name) && AsyncTaskClassVisitor.traceMethodMap.get(name).equals(desc)) {
                final TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor(methodVisitor, access, name, desc, this.context);
                traceMethodVisitor.setUnloadContext();
                return traceMethodVisitor;
            }
            if (AsyncTaskClassVisitor.endTraceMethodMap.containsKey(name) && AsyncTaskClassVisitor.endTraceMethodMap.get(name).equals(desc)) {
                final TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor(methodVisitor, access, name, desc, this.context);
                return traceMethodVisitor;
            }
        }
        return methodVisitor;
    }
    
    static {
        traceMethodMap = ImmutableMap.of("doInBackground", "([Ljava/lang/Object;)Ljava/lang/Object;");
        endTraceMethodMap = ImmutableMap.of("onPostExecute", "(Ljava/lang/Object;)V");
    }
}
