// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.org.objectweb.asm.Label;
import com.newrelic.org.objectweb.asm.MethodVisitor;
import com.newrelic.org.objectweb.asm.FieldVisitor;
import com.newrelic.org.objectweb.asm.Attribute;
import java.text.MessageFormat;
import com.newrelic.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.compile.Log;
import com.newrelic.agent.compile.InstrumentationContext;
import com.newrelic.org.objectweb.asm.ClassVisitor;

public class PrefilterClassVisitor extends ClassVisitor
{
    private static final String TRACE_ANNOTATION_CLASSPATH = "Lcom/newrelic/agent/android/instrumentation/Trace;";
    private static final String SKIP_TRACE_ANNOTATION_CLASSPATH = "Lcom/newrelic/agent/android/instrumentation/SkipTrace;";
    private final InstrumentationContext context;
    private final Log log;
    
    public PrefilterClassVisitor(final InstrumentationContext context, final Log log) {
        super(327680);
        this.context = context;
        this.log = log;
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String sig, final String superName, final String[] interfaces) {
        this.context.setClassName(name);
        this.context.setSuperClassName(superName);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        if (Annotations.isNewRelicAnnotation(desc)) {
            this.log.info(MessageFormat.format("[{0}] class has New Relic tag: {1}", this.context.getClassName(), desc));
            this.context.addTag(desc);
        }
        return null;
    }
    
    @Override
    public void visitAttribute(final Attribute arg0) {
    }
    
    @Override
    public void visitEnd() {
    }
    
    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String sig, final Object value) {
        return null;
    }
    
    @Override
    public void visitInnerClass(final String arg0, final String arg1, final String arg2, final int arg3) {
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final MethodVisitor methodVisitor = new MethodVisitor(327680) {
            @Override
            public AnnotationVisitor visitAnnotationDefault() {
                return null;
            }
            
            @Override
            public AnnotationVisitor visitAnnotation(final String annotationDesc, final boolean visible) {
                if (annotationDesc.equals("Lcom/newrelic/agent/android/instrumentation/Trace;")) {
                    PrefilterClassVisitor.this.context.addTracedMethod(name, desc);
                    return new TraceAnnotationVisitor(name, PrefilterClassVisitor.this.context);
                }
                if (annotationDesc.equals("Lcom/newrelic/agent/android/instrumentation/SkipTrace;")) {
                    PrefilterClassVisitor.this.context.addSkippedMethod(name, desc);
                    return null;
                }
                return null;
            }
            
            @Override
            public AnnotationVisitor visitParameterAnnotation(final int i, final String s, final boolean b) {
                return null;
            }
            
            @Override
            public void visitAttribute(final Attribute attribute) {
            }
            
            @Override
            public void visitCode() {
            }
            
            @Override
            public void visitFrame(final int i, final int i2, final Object[] objects, final int i3, final Object[] objects2) {
            }
            
            @Override
            public void visitInsn(final int i) {
            }
            
            @Override
            public void visitIntInsn(final int i, final int i2) {
            }
            
            @Override
            public void visitVarInsn(final int i, final int i2) {
            }
            
            @Override
            public void visitTypeInsn(final int i, final String s) {
            }
            
            @Override
            public void visitFieldInsn(final int i, final String s, final String s2, final String s3) {
            }
            
            @Override
            public void visitMethodInsn(final int i, final String s, final String s2, final String s3, final boolean b) {
            }
            
            @Override
            public void visitJumpInsn(final int i, final Label label) {
            }
            
            @Override
            public void visitLabel(final Label label) {
            }
            
            @Override
            public void visitLdcInsn(final Object o) {
            }
            
            @Override
            public void visitIincInsn(final int i, final int i2) {
            }
            
            @Override
            public void visitTableSwitchInsn(final int i, final int i2, final Label label, final Label[] labels) {
            }
            
            @Override
            public void visitLookupSwitchInsn(final Label label, final int[] ints, final Label[] labels) {
            }
            
            @Override
            public void visitMultiANewArrayInsn(final String s, final int i) {
            }
            
            @Override
            public void visitTryCatchBlock(final Label label, final Label label2, final Label label3, final String s) {
            }
            
            @Override
            public void visitLocalVariable(final String s, final String s2, final String s3, final Label label, final Label label2, final int i) {
            }
            
            @Override
            public void visitLineNumber(final int i, final Label label) {
            }
            
            @Override
            public void visitMaxs(final int i, final int i2) {
            }
            
            @Override
            public void visitEnd() {
            }
        };
        return methodVisitor;
    }
    
    @Override
    public void visitOuterClass(final String arg0, final String arg1, final String arg2) {
    }
    
    @Override
    public void visitSource(final String arg0, final String arg1) {
    }
}
