// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import java.util.Iterator;
import java.util.Collection;
import com.newrelic.org.objectweb.asm.Label;
import com.newrelic.org.objectweb.asm.commons.Method;
import com.newrelic.agent.compile.ClassMethod;
import java.text.MessageFormat;
import com.newrelic.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.compile.Log;
import com.newrelic.agent.compile.InstrumentationContext;
import com.newrelic.org.objectweb.asm.ClassVisitor;

public class WrapMethodClassVisitor extends ClassVisitor
{
    private final InstrumentationContext context;
    private final Log log;
    
    public WrapMethodClassVisitor(final ClassVisitor cv, final InstrumentationContext context, final Log log) {
        super(327680, cv);
        this.context = context;
        this.log = log;
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String sig, final String[] exceptions) {
        if (this.context.isSkippedMethod(name, desc)) {
            return super.visitMethod(access, name, desc, sig, exceptions);
        }
        return new MethodWrapMethodVisitor(super.visitMethod(access, name, desc, sig, exceptions), access, name, desc, this.context, this.log);
    }
    
    private static final class MethodWrapMethodVisitor extends GeneratorAdapter
    {
        private final String name;
        private final String desc;
        private final InstrumentationContext context;
        private final Log log;
        private boolean newInstructionFound;
        private boolean dupInstructionFound;
        
        public MethodWrapMethodVisitor(final MethodVisitor mv, final int access, final String name, final String desc, final InstrumentationContext context, final Log log) {
            super(327680, mv, access, name, desc);
            this.newInstructionFound = false;
            this.dupInstructionFound = false;
            this.name = name;
            this.desc = desc;
            this.context = context;
            this.log = log;
        }
        
        @Override
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
            this.visitMethodInsn(opcode, owner, name, desc, opcode == 185);
        }
        
        @Override
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean isInterface) {
            if (opcode == 186) {
                this.log.warning(MessageFormat.format("[{0}] INVOKEDYNAMIC instruction cannot be instrumented", this.context.getClassName().replaceAll("/", ".")));
                super.visitMethodInsn(opcode, owner, name, desc, isInterface);
                return;
            }
            if (!this.tryReplaceCallSite(opcode, owner, name, desc) && !this.tryWrapReturnValue(opcode, owner, name, desc)) {
                super.visitMethodInsn(opcode, owner, name, desc, isInterface);
            }
        }
        
        @Override
        public void visitTypeInsn(final int opcode, final String type) {
            if (opcode == 187) {
                this.newInstructionFound = true;
                this.dupInstructionFound = false;
            }
            super.visitTypeInsn(opcode, type);
        }
        
        @Override
        public void visitInsn(final int opcode) {
            if (opcode == 89) {
                this.dupInstructionFound = true;
            }
            super.visitInsn(opcode);
        }
        
        private boolean tryWrapReturnValue(final int opcode, final String owner, final String name, final String desc) {
            final ClassMethod method = new ClassMethod(owner, name, desc);
            final ClassMethod wrappingMethod = this.context.getMethodWrapper(method);
            if (wrappingMethod != null) {
                this.log.debug(MessageFormat.format("[{0}] wrapping call to {1} with {2}", this.context.getClassName().replaceAll("/", "."), method.toString(), wrappingMethod.toString()));
                super.visitMethodInsn(opcode, owner, name, desc, opcode == 185);
                super.visitMethodInsn(184, wrappingMethod.getClassName(), wrappingMethod.getMethodName(), wrappingMethod.getMethodDesc(), false);
                this.context.markModified();
                return true;
            }
            return false;
        }
        
        private boolean tryReplaceCallSite(final int opcode, final String owner, final String name, final String desc) {
            final Collection<ClassMethod> replacementMethods = this.context.getCallSiteReplacements(owner, name, desc);
            if (replacementMethods.isEmpty()) {
                return false;
            }
            final ClassMethod method = new ClassMethod(owner, name, desc);
            final Iterator<ClassMethod> iterator = replacementMethods.iterator();
            if (!iterator.hasNext()) {
                return false;
            }
            final ClassMethod replacementMethod = iterator.next();
            final boolean isSuperCallInOverride = opcode == 183 && !owner.equals(this.context.getClassName()) && this.name.equals(name) && this.desc.equals(desc);
            if (isSuperCallInOverride) {
                this.log.debug(MessageFormat.format("[{0}] skipping call site replacement for super call in overriden method: {1}:{2}", this.context.getClassName().replaceAll("/", "."), this.name, this.desc));
                return false;
            }
            if (opcode == 183 && name.equals("<init>")) {
                final Method originalMethod = new Method(name, desc);
                if (this.context.getSuperClassName() != null && this.context.getSuperClassName().equals(owner)) {
                    this.log.debug(MessageFormat.format("[{0}] skipping call site replacement for class extending {1}", this.context.getFriendlyClassName(), this.context.getFriendlySuperClassName()));
                    return false;
                }
                this.log.debug(MessageFormat.format("[{0}] tracing constructor call to {1} - {2}", this.context.getFriendlyClassName(), method.toString(), owner));
                final int[] locals = new int[originalMethod.getArgumentTypes().length];
                for (int i = locals.length - 1; i >= 0; --i) {
                    this.storeLocal(locals[i] = this.newLocal(originalMethod.getArgumentTypes()[i]));
                }
                this.visitInsn(87);
                if (this.newInstructionFound && this.dupInstructionFound) {
                    this.visitInsn(87);
                }
                for (final int local : locals) {
                    this.loadLocal(local);
                }
                super.visitMethodInsn(184, replacementMethod.getClassName(), replacementMethod.getMethodName(), replacementMethod.getMethodDesc(), false);
                if (this.newInstructionFound && !this.dupInstructionFound) {
                    this.visitInsn(87);
                }
            }
            else if (opcode == 184) {
                this.log.debug(MessageFormat.format("[{0}] replacing static call to {1} with {2}", this.context.getClassName().replaceAll("/", "."), method.toString(), replacementMethod.toString()));
                super.visitMethodInsn(184, replacementMethod.getClassName(), replacementMethod.getMethodName(), replacementMethod.getMethodDesc(), false);
            }
            else {
                final Method newMethod = new Method(replacementMethod.getMethodName(), replacementMethod.getMethodDesc());
                this.log.debug(MessageFormat.format("[{0}] replacing call to {1} with {2} (with instance check)", this.context.getClassName().replaceAll("/", "."), method.toString(), replacementMethod.toString()));
                final Method originalMethod2 = new Method(name, desc);
                final int[] locals2 = new int[originalMethod2.getArgumentTypes().length];
                for (int j = locals2.length - 1; j >= 0; --j) {
                    this.storeLocal(locals2[j] = this.newLocal(originalMethod2.getArgumentTypes()[j]));
                }
                this.dup();
                this.instanceOf(newMethod.getArgumentTypes()[0]);
                final Label isInstanceOfLabel = new Label();
                this.visitJumpInsn(154, isInstanceOfLabel);
                for (final int local2 : locals2) {
                    this.loadLocal(local2);
                }
                super.visitMethodInsn(opcode, owner, name, desc, opcode == 185);
                final Label end = new Label();
                this.visitJumpInsn(167, end);
                this.visitLabel(isInstanceOfLabel);
                this.checkCast(newMethod.getArgumentTypes()[0]);
                for (final int local3 : locals2) {
                    this.loadLocal(local3);
                }
                super.visitMethodInsn(184, replacementMethod.getClassName(), replacementMethod.getMethodName(), replacementMethod.getMethodDesc(), false);
                this.visitLabel(end);
            }
            this.context.markModified();
            return true;
        }
    }
}
