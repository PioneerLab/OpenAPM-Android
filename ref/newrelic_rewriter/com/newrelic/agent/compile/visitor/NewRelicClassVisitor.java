// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.agent.Obfuscation.Proguard;
import com.newrelic.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.compile.RewriterAgent;
import com.newrelic.org.objectweb.asm.FieldVisitor;
import com.newrelic.org.objectweb.asm.MethodVisitor;
import java.util.UUID;
import com.newrelic.agent.compile.Log;
import com.newrelic.agent.compile.InstrumentationContext;
import com.newrelic.org.objectweb.asm.ClassVisitor;

public class NewRelicClassVisitor extends ClassVisitor
{
    public static final String BUILD_ID_KEY = "NewRelic.BuildId";
    private static String buildId;
    private final InstrumentationContext context;
    private final Log log;
    
    public NewRelicClassVisitor(final ClassVisitor cv, final InstrumentationContext context, final Log log) {
        super(327680, cv);
        this.context = context;
        this.log = log;
    }
    
    public static String getBuildId() {
        if (NewRelicClassVisitor.buildId == null) {
            System.setProperty("NewRelic.BuildId", NewRelicClassVisitor.buildId = UUID.randomUUID().toString());
        }
        return NewRelicClassVisitor.buildId;
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        if (this.context.getClassName().equals("com/newrelic/agent/android/NewRelic") && name.equals("isInstrumented")) {
            return new NewRelicMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc);
        }
        if (this.context.getClassName().equals("com/newrelic/agent/android/harvest/crash/Crash") && name.equals("getBuildId")) {
            return new BuildIdMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc);
        }
        if (this.context.getClassName().equals("com/newrelic/agent/android/AndroidAgentImpl") && name.equals("pokeCanary")) {
            return new CanaryMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
    
    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        if (this.context.getClassName().equals("com/newrelic/agent/android/Agent") && name.equals("VERSION") && !value.equals(RewriterAgent.getVersion())) {
            this.log.warning("New Relic Error: Your agent and class rewriter versions do not match: agent[" + value + "] class rewriter[" + RewriterAgent.getVersion() + "]. " + "You may need to update one of these components, or simply invalidate your AndroidStudio cache.  " + "If you're using gradle and just updated, run gradle -stop to restart the daemon.");
        }
        return super.visitField(access, name, desc, signature, value);
    }
    
    private final class BuildIdMethodVisitor extends GeneratorAdapter
    {
        public BuildIdMethodVisitor(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(327680, mv, access, name, desc);
        }
        
        @Override
        public void visitCode() {
            super.visitLdcInsn(NewRelicClassVisitor.getBuildId());
            super.visitInsn(176);
            NewRelicClassVisitor.this.log.info("Setting build identifier to " + NewRelicClassVisitor.getBuildId());
            NewRelicClassVisitor.this.context.markModified();
        }
    }
    
    private final class NewRelicMethodVisitor extends GeneratorAdapter
    {
        public NewRelicMethodVisitor(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(327680, mv, access, name, desc);
        }
        
        @Override
        public void visitCode() {
            super.visitInsn(4);
            super.visitInsn(172);
            NewRelicClassVisitor.this.log.info("Marking NewRelic agent as instrumented");
            NewRelicClassVisitor.this.context.markModified();
        }
    }
    
    private final class CanaryMethodVisitor extends GeneratorAdapter
    {
        private boolean foundCanaryAlive;
        
        public CanaryMethodVisitor(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(327680, mv, access, name, desc);
            this.foundCanaryAlive = false;
        }
        
        @Override
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean b) {
            if (name.equals("canaryMethod")) {
                this.foundCanaryAlive = true;
            }
        }
        
        @Override
        public void visitEnd() {
            if (this.foundCanaryAlive) {
                NewRelicClassVisitor.this.log.info("Found canary alive");
            }
            else {
                NewRelicClassVisitor.this.log.info("Evidence of Proguard/Dexguard detected, sending mapping.txt");
                final Proguard proguard = new Proguard(NewRelicClassVisitor.this.log);
                proguard.findAndSendMapFile();
            }
        }
    }
}
