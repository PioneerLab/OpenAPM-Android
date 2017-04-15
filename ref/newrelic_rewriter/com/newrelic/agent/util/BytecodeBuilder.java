// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Field;
import com.newrelic.org.objectweb.asm.commons.Method;
import com.newrelic.org.objectweb.asm.Type;
import com.newrelic.agent.compile.InvocationDispatcher;
import com.newrelic.org.objectweb.asm.commons.GeneratorAdapter;

public final class BytecodeBuilder
{
    private final GeneratorAdapter mv;
    
    public BytecodeBuilder(final GeneratorAdapter adapter) {
        this.mv = adapter;
    }
    
    public BytecodeBuilder loadNull() {
        this.mv.visitInsn(1);
        return this;
    }
    
    public BytecodeBuilder loadInvocationDispatcher() {
        this.mv.visitLdcInsn(Type.getType(InvocationDispatcher.INVOCATION_DISPATCHER_CLASS));
        this.mv.visitLdcInsn("treeLock");
        this.mv.invokeVirtual(Type.getType(Class.class), new Method("getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;"));
        this.mv.dup();
        this.mv.visitInsn(4);
        this.mv.invokeVirtual(Type.getType(Field.class), new Method("setAccessible", "(Z)V"));
        this.mv.visitInsn(1);
        this.mv.invokeVirtual(Type.getType(Field.class), new Method("get", "(Ljava/lang/Object;)Ljava/lang/Object;"));
        return this;
    }
    
    public BytecodeBuilder loadArgumentsArray(final String methodDesc) {
        final Method method = new Method("dummy", methodDesc);
        this.mv.push(method.getArgumentTypes().length);
        final Type objectType = Type.getType(Object.class);
        this.mv.newArray(objectType);
        for (int i = 0; i < method.getArgumentTypes().length; ++i) {
            this.mv.dup();
            this.mv.push(i);
            this.mv.loadArg(i);
            this.mv.arrayStore(objectType);
        }
        return this;
    }
    
    public BytecodeBuilder loadArray(final Runnable... r) {
        this.mv.push(r.length);
        final Type objectType = Type.getObjectType("java/lang/Object");
        this.mv.newArray(objectType);
        for (int i = 0; i < r.length; ++i) {
            this.mv.dup();
            this.mv.push(i);
            r[i].run();
            this.mv.arrayStore(objectType);
        }
        return this;
    }
    
    public BytecodeBuilder printToInfoLogFromBytecode(final String message) {
        this.loadInvocationDispatcher();
        this.mv.visitLdcInsn("PRINT_TO_INFO_LOG");
        this.mv.visitInsn(1);
        this.loadArray(new Runnable() {
            @Override
            public void run() {
                BytecodeBuilder.this.mv.visitLdcInsn(message);
            }
        });
        this.invokeDispatcher();
        return this;
    }
    
    public BytecodeBuilder invokeDispatcher() {
        return this.invokeDispatcher(true);
    }
    
    public BytecodeBuilder invokeDispatcher(final boolean popReturnOffStack) {
        this.mv.invokeInterface(Type.getType(InvocationHandler.class), new Method("invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"));
        if (popReturnOffStack) {
            this.mv.pop();
        }
        return this;
    }
    
    public BytecodeBuilder loadInvocationDispatcherKey(final String key) {
        this.mv.visitLdcInsn(key);
        this.mv.visitInsn(1);
        return this;
    }
}
