// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.transformers;

import com.newrelic.agent.compile.ClassAdapterBase;
import com.newrelic.agent.compile.MethodVisitorFactory;
import com.newrelic.agent.compile.visitor.SkipInstrumentedMethodsMethodVisitor;
import com.newrelic.org.objectweb.asm.commons.Method;
import com.newrelic.agent.compile.visitor.BaseMethodVisitor;
import com.newrelic.org.objectweb.asm.MethodVisitor;
import java.lang.instrument.IllegalClassFormatException;
import com.newrelic.org.objectweb.asm.ClassWriter;
import com.newrelic.agent.compile.SkipException;
import com.newrelic.agent.compile.PatchedClassWriter;
import com.newrelic.org.objectweb.asm.ClassReader;
import java.security.ProtectionDomain;
import com.newrelic.org.objectweb.asm.Type;
import com.newrelic.org.objectweb.asm.ClassVisitor;
import java.util.HashMap;
import java.net.URISyntaxException;
import com.newrelic.agent.compile.RewriterAgent;
import com.newrelic.agent.compile.ClassVisitorFactory;
import java.util.Map;
import com.newrelic.agent.compile.Log;

public final class ClassRewriterTransformer implements NewRelicClassTransformer
{
    private Log log;
    private final Map<String, ClassVisitorFactory> classVisitors;
    
    public ClassRewriterTransformer(final Log log) throws URISyntaxException {
        try {
            RewriterAgent.getAgentJarPath();
        }
        catch (URISyntaxException e) {
            log.error("Unable to get the path to the New Relic class rewriter jar", e);
            throw e;
        }
        this.log = log;
        (this.classVisitors = new HashMap<String, ClassVisitorFactory>()).put("java/lang/ProcessBuilder", new ClassVisitorFactory(true) {
            @Override
            public ClassVisitor create(final ClassVisitor cv) {
                return createProcessBuilderClassAdapter(cv, log);
            }
        });
        this.classVisitors.put("com/newrelic/agent/compile/ClassTransformer", new ClassVisitorFactory(true) {
            @Override
            public ClassVisitor create(final ClassVisitor cv) {
                return createTransformClassAdapter(cv, log);
            }
        });
    }
    
    @Override
    public boolean modifies(final Class<?> clazz) {
        final Type t = Type.getType(clazz);
        return this.classVisitors.containsKey(t.getInternalName());
    }
    
    @Override
    public byte[] transform(final ClassLoader classLoader, final String className, final Class<?> clazz, final ProtectionDomain protectionDomain, final byte[] bytes) throws IllegalClassFormatException {
        final ClassVisitorFactory factory = this.classVisitors.get(className);
        if (factory != null) {
            if (clazz != null && !factory.isRetransformOkay()) {
                this.log.error("Cannot instrument " + className);
                return null;
            }
            try {
                final ClassReader cr = new ClassReader(bytes);
                final ClassWriter cw = new PatchedClassWriter(3, classLoader);
                final ClassVisitor adapter = factory.create(cw);
                cr.accept(adapter, 4);
                this.log.debug("ClassTransformer: Transformed[" + className + "] Bytes In[" + bytes.length + "] Bytes Out[" + cw.toByteArray().length + "]");
                return cw.toByteArray();
            }
            catch (SkipException ex2) {}
            catch (Exception ex) {
                this.log.error("Error transforming class " + className, ex);
            }
        }
        return null;
    }
    
    private static ClassVisitor createProcessBuilderClassAdapter(final ClassVisitor cw, final Log log) {
        return new ClassVisitor(327680, cw) {
            @Override
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if ("start".equals(name)) {
                    mv = new SkipInstrumentedMethodsMethodVisitor(new BaseMethodVisitor(mv, access, name, desc) {
                        @Override
                        protected void onMethodEnter() {
                            this.builder.loadInvocationDispatcher().loadInvocationDispatcherKey(RewriterAgent.getProxyInvocationKey("java/lang/ProcessBuilder", this.methodName)).loadArray(new Runnable() {
                                @Override
                                public void run() {
                                    BaseMethodVisitor.this.loadThis();
                                    BaseMethodVisitor.this.invokeVirtual(Type.getObjectType("java/lang/ProcessBuilder"), new Method("command", "()Ljava/util/List;"));
                                }
                            }).invokeDispatcher();
                        }
                    });
                }
                return mv;
            }
        };
    }
    
    private static ClassVisitor createTransformClassAdapter(final ClassVisitor cw, final Log log) {
        return new ClassAdapterBase(log, cw, new HashMap<Method, MethodVisitorFactory>() {
            {
                ((HashMap<Method, ClassRewriterTransformer$4$1>)this).put(new Method("transformClassBytes", "(Ljava/lang/String;[B)[B"), new MethodVisitorFactory() {
                    @Override
                    public MethodVisitor create(final MethodVisitor mv, final int access, final String name, final String desc) {
                        return new BaseMethodVisitor(mv, access, name, desc) {
                            @Override
                            protected void onMethodEnter() {
                                this.builder.loadInvocationDispatcher().loadInvocationDispatcherKey(RewriterAgent.getProxyInvocationKey("com/newrelic/agent/compile/ClassTransformer", this.methodName)).loadArgumentsArray(this.methodDesc).invokeDispatcher(false);
                                this.checkCast(Type.getType(byte[].class));
                                this.storeArg(1);
                            }
                        };
                    }
                });
            }
        });
    }
}
