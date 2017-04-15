package com.hello2mao.openapm.rewriter.visitor;


import com.hello2mao.openapm.rewriter.InstrumentationContext;
import com.hello2mao.openapm.rewriter.util.Log;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

/**
 * 标记注入成功了
 */
public class OpenAPMClassVisitor extends ClassVisitor {

    private InstrumentationContext context;
    private Log log;
    
    public OpenAPMClassVisitor(ClassVisitor cv, InstrumentationContext context, Log log) {
        super(Opcodes.ASM5, cv);
        this.context = context;
        this.log = log;
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (context.getClassName().equals("com/hello2mao/openapm/agent/OpenAPM") && name.equals("isInstrumented")) {
            return new OpenAPMMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
    
    private class OpenAPMMethodVisitor extends GeneratorAdapter {

        public OpenAPMMethodVisitor(MethodVisitor mv, int access, String name, String desc) {
            super(Opcodes.ASM5, mv, access, name, desc);
        }
        
        @Override
        public void visitCode() {
            // 返回true，以此来说明注入成功了
            super.visitInsn(Opcodes.ICONST_1);
            super.visitInsn(Opcodes.IRETURN);
            log.info("Marking OpenAPM agent as instrumented");
            context.markModified();
        }
    }

}
