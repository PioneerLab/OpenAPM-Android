package com.hello2mao.openapm.rewriter.visitor;


import com.hello2mao.openapm.rewriter.InstrumentationContext;
import com.hello2mao.openapm.rewriter.util.Log;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.text.MessageFormat;

public class PrefilterClassVisitor extends ClassVisitor {

    private static String TRACE_ANNOTATION_CLASSPATH = "Lcom/hello2mao/openapm/agent/instrumentation/Trace;";
    private static String SKIP_TRACE_ANNOTATION_CLASSPATH = "Lcom/hello2mao/openapm/agent/instrumentation/SkipTrace;";
    private InstrumentationContext context;
    private Log log;
    
    public PrefilterClassVisitor(InstrumentationContext context, Log log) {
        super(Opcodes.ASM5);
        this.context = context;
        this.log = log;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
                      String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        context.setClassName(name);
        context.setSuperClassName(superName);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (Annotations.isOpenAPMAnnotation(desc)) {
            log.info(MessageFormat.format("[{0}] class has OpenAPM tag: {1}", context.getClassName(), desc));
            context.addTag(desc);
        }
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new MethodVisitor(Opcodes.ASM5) {

            @Override
            public AnnotationVisitor visitAnnotation(String annotationDesc, boolean visible) {
                // FIXME: ?
                if (annotationDesc.equals(TRACE_ANNOTATION_CLASSPATH)) {
                    context.addTracedMethod(name, desc);
                    return new TraceAnnotationVisitor(name, context);
                }
                // FIXME: ?
                if (annotationDesc.equals(SKIP_TRACE_ANNOTATION_CLASSPATH)) {
                    context.addSkippedMethod(name, desc);
                    return null;
                }
                return null;
            }
        };
    }
}
