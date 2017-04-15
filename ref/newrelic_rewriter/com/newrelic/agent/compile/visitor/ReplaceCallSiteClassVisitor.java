// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.org.objectweb.asm.MethodVisitor;
import com.newrelic.com.google.common.collect.Sets;
import java.util.Set;
import com.newrelic.agent.compile.Log;
import com.newrelic.agent.compile.InstrumentationContext;
import com.newrelic.org.objectweb.asm.ClassVisitor;

public class ReplaceCallSiteClassVisitor extends ClassVisitor
{
    private final InstrumentationContext context;
    private final Log log;
    private final Set<String> recursiveCallCheckThreadLocals;
    
    public ReplaceCallSiteClassVisitor(final ClassVisitor cv, final InstrumentationContext context, final Log log) {
        super(327680, cv);
        this.recursiveCallCheckThreadLocals = (Set<String>)Sets.newHashSet();
        this.context = context;
        this.log = log;
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String sig, final String[] exceptions) {
        return new MethodWrapMethodVisitor(super.visitMethod(access, name, desc, sig, exceptions), access, name, desc);
    }
    
    private final class MethodWrapMethodVisitor extends GeneratorAdapter
    {
        private final String name;
        private final String desc;
        private boolean isReplaceClassSite;
        
        public MethodWrapMethodVisitor(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(mv, access, name, desc);
            ReplaceCallSiteClassVisitor.this.log.debug("DUDE " + name + desc);
            this.name = name;
            this.desc = desc;
        }
        
        @Override
        public AnnotationVisitor visitAnnotation(final String name, final boolean arg1) {
            if ("Lcom/newrelic/agent/android/instrumentation/ReplaceCallSite;".equals(name)) {
                this.isReplaceClassSite = true;
            }
            return super.visitAnnotation(name, arg1);
        }
        
        @Override
        public void visitCode() {
            super.visitCode();
        }
    }
}
