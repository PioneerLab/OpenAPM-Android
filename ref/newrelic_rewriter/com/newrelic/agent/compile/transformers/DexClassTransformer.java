// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.transformers;

import java.util.List;
import com.newrelic.agent.util.BytecodeBuilder;
import java.io.File;
import com.newrelic.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.compile.visitor.SkipInstrumentedMethodsMethodVisitor;
import com.newrelic.agent.compile.visitor.SafeInstrumentationMethodVisitor;
import com.newrelic.agent.compile.ClassAdapterBase;
import com.newrelic.agent.compile.visitor.BaseMethodVisitor;
import com.newrelic.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.compile.MethodVisitorFactory;
import com.newrelic.org.objectweb.asm.commons.Method;
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

public final class DexClassTransformer implements NewRelicClassTransformer
{
    private Log log;
    private final Map<String, ClassVisitorFactory> classVisitors;
    
    public DexClassTransformer(final Log log) throws URISyntaxException {
        String agentJarPath;
        try {
            agentJarPath = RewriterAgent.getAgentJarPath();
        }
        catch (URISyntaxException e) {
            log.error("Unable to get the path to the New Relic class rewriter jar", e);
            throw e;
        }
        this.log = log;
        this.classVisitors = new HashMap<String, ClassVisitorFactory>() {
            {
                ((HashMap<String, DexClassTransformer$1$1>)this).put("com/android/dx/command/dexer/Main", new ClassVisitorFactory(true) {
                    @Override
                    public ClassVisitor create(final ClassVisitor cv) {
                        return createDexerMainClassAdapter(cv, log);
                    }
                });
                ((HashMap<String, DexClassTransformer$1$2>)this).put("com/android/ant/DexExecTask", new ClassVisitorFactory(false) {
                    @Override
                    public ClassVisitor create(final ClassVisitor cv) {
                        return createAntTaskClassAdapter(cv, log);
                    }
                });
                ((HashMap<String, DexClassTransformer$1$3>)this).put("com/jayway/maven/plugins/android/phase08preparepackage/DexMojo", new ClassVisitorFactory(true) {
                    @Override
                    public ClassVisitor create(final ClassVisitor cv) {
                        return createMavenClassAdapter(cv, log, agentJarPath);
                    }
                });
                ((HashMap<String, DexClassTransformer$1$4>)this).put("java/lang/ProcessBuilder", new ClassVisitorFactory(true) {
                    @Override
                    public ClassVisitor create(final ClassVisitor cv) {
                        return createProcessBuilderClassAdapter(cv, log);
                    }
                });
            }
        };
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
                this.log.debug("DexTransform: Transformed[" + className + "] Bytes In[" + bytes.length + "] Bytes Out[" + cw.toByteArray().length + "]");
                return cw.toByteArray();
            }
            catch (SkipException ex2) {}
            catch (Exception ex) {
                this.log.error("Error transforming class " + className, ex);
            }
        }
        return null;
    }
    
    private static ClassVisitor createDexerMainClassAdapter(final ClassVisitor cw, final Log log) {
        return new ClassAdapterBase(log, cw, new HashMap<Method, MethodVisitorFactory>() {
            {
                ((HashMap<Method, DexClassTransformer$2$1>)this).put(new Method("processClass", "(Ljava/lang/String;[B)Z"), new MethodVisitorFactory() {
                    @Override
                    public MethodVisitor create(final MethodVisitor mv, final int access, final String name, final String desc) {
                        return new BaseMethodVisitor(mv, access, name, desc) {
                            @Override
                            protected void onMethodEnter() {
                                this.builder.loadInvocationDispatcher().loadInvocationDispatcherKey(RewriterAgent.getProxyInvocationKey("com/android/dx/command/dexer/Main", this.methodName)).loadArgumentsArray(this.methodDesc).invokeDispatcher(false);
                                this.checkCast(Type.getType(byte[].class));
                                this.storeArg(1);
                            }
                        };
                    }
                });
            }
        });
    }
    
    private static ClassVisitor createAntTaskClassAdapter(final ClassVisitor cw, final Log log) {
        final String agentFileFieldName = "NewRelicAgentFile";
        final Map<Method, MethodVisitorFactory> methodVisitors = new HashMap<Method, MethodVisitorFactory>() {
            {
                ((HashMap<Method, DexClassTransformer$3$1>)this).put(new Method("preDexLibraries", "(Ljava/util/List;)V"), new MethodVisitorFactory() {
                    @Override
                    public MethodVisitor create(final MethodVisitor mv, final int access, final String name, final String desc) {
                        return new BaseMethodVisitor(mv, access, name, desc) {
                            @Override
                            protected void onMethodEnter() {
                                this.builder.loadInvocationDispatcher().loadInvocationDispatcherKey(RewriterAgent.getProxyInvocationKey("com/android/ant/DexExecTask", this.methodName)).loadArray(new Runnable() {
                                    @Override
                                    public void run() {
                                        BaseMethodVisitor.this.loadArg(0);
                                    }
                                }).invokeDispatcher(false);
                                this.loadThis();
                                this.swap();
                                this.putField(Type.getObjectType("com/android/ant/DexExecTask"), "NewRelicAgentFile", Type.getType(Object.class));
                            }
                        };
                    }
                });
                ((HashMap<Method, DexClassTransformer$3$2>)this).put(new Method("runDx", "(Ljava/util/Collection;Ljava/lang/String;Z)V"), new MethodVisitorFactory() {
                    @Override
                    public MethodVisitor create(final MethodVisitor mv, final int access, final String name, final String desc) {
                        return new SafeInstrumentationMethodVisitor(mv, access, name, desc) {
                            @Override
                            protected void onMethodEnter() {
                                this.builder.loadInvocationDispatcher().loadInvocationDispatcherKey("SET_INSTRUMENTATION_DISABLED_FLAG").loadArray(new Runnable() {
                                    @Override
                                    public void run() {
                                        SafeInstrumentationMethodVisitor.this.loadThis();
                                        SafeInstrumentationMethodVisitor.this.getField(Type.getObjectType("com/android/ant/DexExecTask"), "NewRelicAgentFile", Type.getType(Object.class));
                                    }
                                }).invokeDispatcher();
                            }
                        };
                    }
                });
            }
        };
        return new ClassAdapterBase(log, cw, methodVisitors) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                this.visitField(2, "NewRelicAgentFile", Type.getType(Object.class).getDescriptor(), null, null);
            }
        };
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
    
    private static ClassVisitor createMavenClassAdapter(final ClassVisitor cw, final Log log, final String agentJarPath) {
        final Map<Method, MethodVisitorFactory> methodVisitors = new HashMap<Method, MethodVisitorFactory>() {
            {
                ((HashMap<Method, DexClassTransformer$6$1>)this).put(new Method("runDex", "(Lcom/jayway/maven/plugins/android/CommandExecutor;Ljava/io/File;Ljava/util/Set;)V"), new MethodVisitorFactory() {
                    @Override
                    public MethodVisitor create(final MethodVisitor mv, final int access, final String name, final String desc) {
                        return new GeneratorAdapter(327680, mv, access, name, desc) {
                            @Override
                            public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean isInterface) {
                                if ("executeCommand".equals(name) && "(Ljava/lang/String;Ljava/util/List;Ljava/io/File;Z)V".equals(desc)) {
                                    final int arg3 = this.newLocal(Type.BOOLEAN_TYPE);
                                    this.storeLocal(arg3);
                                    final int arg4 = this.newLocal(Type.getType(File.class));
                                    this.storeLocal(arg4);
                                    this.dup();
                                    this.push(0);
                                    String agentCommand = "-javaagent:" + agentJarPath;
                                    if (RewriterAgent.getAgentArgs() != null) {
                                        agentCommand = agentCommand + "=" + RewriterAgent.getAgentArgs();
                                    }
                                    new BytecodeBuilder(this).printToInfoLogFromBytecode("Maven agent jar: " + agentCommand);
                                    this.visitLdcInsn(agentCommand);
                                    this.invokeInterface(Type.getType(List.class), new Method("add", "(ILjava/lang/Object;)V"));
                                    this.loadLocal(arg4);
                                    this.loadLocal(arg3);
                                }
                                super.visitMethodInsn(opcode, owner, name, desc, isInterface);
                            }
                        };
                    }
                });
            }
        };
        return new ClassAdapterBase(log, cw, methodVisitors);
    }
}
