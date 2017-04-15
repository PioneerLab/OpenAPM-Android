// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.org.objectweb.asm.MethodVisitor;

public abstract class SafeInstrumentationMethodVisitor extends BaseMethodVisitor
{
    protected SafeInstrumentationMethodVisitor(final MethodVisitor mv, final int access, final String methodName, final String desc) {
        super(mv, access, methodName, desc);
    }
    
    @Override
    protected final void onMethodExit(final int opcode) {
        this.builder.loadInvocationDispatcher().loadInvocationDispatcherKey("SET_INSTRUMENTATION_DISABLED_FLAG").loadNull().invokeDispatcher();
        super.onMethodExit(opcode);
    }
}
