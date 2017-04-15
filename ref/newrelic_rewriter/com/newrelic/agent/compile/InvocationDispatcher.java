// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import java.util.Collection;
import java.util.logging.Logger;
import java.text.MessageFormat;
import com.newrelic.agent.compile.visitor.ContextInitializationClassVisitor;
import com.newrelic.agent.compile.visitor.WrapMethodClassVisitor;
import com.newrelic.agent.compile.visitor.TraceAnnotationClassVisitor;
import com.newrelic.agent.compile.visitor.AsyncTaskClassVisitor;
import com.newrelic.agent.compile.visitor.AnnotatingClassVisitor;
import com.newrelic.agent.compile.visitor.ActivityClassVisitor;
import com.newrelic.agent.compile.visitor.NewRelicClassVisitor;
import com.newrelic.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.compile.visitor.PrefilterClassVisitor;
import com.newrelic.org.objectweb.asm.ClassWriter;
import com.newrelic.org.objectweb.asm.ClassReader;
import java.util.Arrays;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.io.File;
import java.util.List;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.InvocationHandler;

public class InvocationDispatcher implements InvocationHandler
{
    public static final Class INVOCATION_DISPATCHER_CLASS;
    public static final String INVOCATION_DISPATCHER_FIELD_NAME = "treeLock";
    public static final Set<String> DX_COMMAND_NAMES;
    public static final Set<String> JAVA_NAMES;
    private static final Set<String> AGENT_JAR_NAMES;
    public static final HashSet<String> EXCLUDED_PACKAGES;
    private final Log log;
    private final ClassRemapperConfig config;
    private final InstrumentationContext context;
    private final Map<String, InvocationHandler> invocationHandlers;
    private boolean writeDisabledMessage;
    private final String agentJarPath;
    private boolean disableInstrumentation;
    
    public InvocationDispatcher(final Log log) throws IOException, ClassNotFoundException, URISyntaxException {
        this.writeDisabledMessage = true;
        this.disableInstrumentation = false;
        this.log = log;
        this.config = new ClassRemapperConfig(log);
        this.context = new InstrumentationContext(this.config, log);
        this.agentJarPath = RewriterAgent.getAgentJarPath();
        this.invocationHandlers = Collections.unmodifiableMap((Map<? extends String, ? extends InvocationHandler>)new HashMap<String, InvocationHandler>() {
            {
                String proxyInvocationKey = RewriterAgent.getProxyInvocationKey("com/android/dx/command/dexer/Main", "processClass");
                ((HashMap<String, InvocationDispatcher$2$1>)this).put(proxyInvocationKey, new InvocationHandler() {
                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                        final String filename = (String)args[0];
                        final byte[] bytes = (byte[])args[1];
                        log.debug("dexer/main/processClass arg[0](filename)[" + filename + "] arg[1](bytes)[" + bytes.length + "]");
                        if (InvocationDispatcher.this.isInstrumentationDisabled()) {
                            if (InvocationDispatcher.this.writeDisabledMessage) {
                                InvocationDispatcher.this.writeDisabledMessage = false;
                                log.info("Instrumentation disabled, no agent present");
                            }
                            return bytes;
                        }
                        InvocationDispatcher.this.writeDisabledMessage = true;
                        synchronized (InvocationDispatcher.this.context) {
                            final ClassData classData = InvocationDispatcher.this.visitClassBytes(bytes);
                            if (classData != null && classData.getMainClassBytes() != null && classData.isModified()) {
                                log.debug("dexer/main/processClass transformed bytes[" + bytes.length + "]");
                                return classData.getMainClassBytes();
                            }
                        }
                        return bytes;
                    }
                });
                proxyInvocationKey = RewriterAgent.getProxyInvocationKey("com/android/ant/DexExecTask", "preDexLibraries");
                ((HashMap<String, InvocationDispatcher$2$2>)this).put(proxyInvocationKey, new InvocationHandler() {
                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                        final List<File> files = (List<File>)args[0];
                        for (final File file : files) {
                            if (InvocationDispatcher.AGENT_JAR_NAMES.contains(file.getName().toLowerCase())) {
                                log.info("Detected the New Relic Android agent in an Ant build (" + file.getPath() + ")");
                                return file;
                            }
                        }
                        log.debug("Ant preDexLibraries: " + files);
                        log.info("No New Relic agent detected in Ant build");
                        return null;
                    }
                });
                ((HashMap<String, InvocationDispatcher$2$3>)this).put("SET_INSTRUMENTATION_DISABLED_FLAG", new InvocationHandler() {
                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                        InvocationDispatcher.this.disableInstrumentation = (args != null && args[0] == null);
                        log.debug("DisableInstrumentation: " + InvocationDispatcher.this.disableInstrumentation + " (" + args + ")");
                        return null;
                    }
                });
                ((HashMap<String, InvocationDispatcher$2$4>)this).put("PRINT_TO_INFO_LOG", new InvocationHandler() {
                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                        log.info(args[0].toString());
                        return null;
                    }
                });
                proxyInvocationKey = RewriterAgent.getProxyInvocationKey("java/lang/ProcessBuilder", "start");
                ((HashMap<String, InvocationDispatcher$2$5>)this).put(proxyInvocationKey, new InvocationHandler() {
                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                        final List<Object> list = (List<Object>)args[0];
                        final String command = list.get(0);
                        final File commandFile = new File(command);
                        log.debug("processBuilder/start command[" + command + "]");
                        if (InvocationDispatcher.this.isInstrumentationDisabled()) {
                            log.info("Instrumentation disabled, no agent present.  Command: " + commandFile.getName());
                            log.debug("Execute: " + list.toString());
                            return null;
                        }
                        String javaagentString = null;
                        if (InvocationDispatcher.DX_COMMAND_NAMES.contains(commandFile.getName().toLowerCase())) {
                            javaagentString = "-Jjavaagent:" + InvocationDispatcher.this.agentJarPath;
                        }
                        else if (InvocationDispatcher.JAVA_NAMES.contains(commandFile.getName().toLowerCase())) {
                            javaagentString = "-javaagent:" + InvocationDispatcher.this.agentJarPath;
                        }
                        if (javaagentString != null) {
                            final String agentArgs = RewriterAgent.getAgentArgs();
                            if (agentArgs != null) {
                                javaagentString = javaagentString + "=" + agentArgs;
                            }
                            list.add(1, this.quoteProperty(javaagentString));
                        }
                        log.debug("processBuilder/start Execute[" + list.toString() + "]");
                        return null;
                    }
                    
                    private String quoteProperty(final String string) {
                        if (System.getProperty("os.name").toLowerCase().contains("win")) {
                            return "\"" + string + "\"";
                        }
                        return string;
                    }
                });
                proxyInvocationKey = RewriterAgent.getProxyInvocationKey("com/newrelic/agent/compile/ClassTransformer", "transformClassBytes");
                ((HashMap<String, InvocationDispatcher$2$6>)this).put(proxyInvocationKey, new InvocationHandler() {
                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                        final String filename = (String)args[0];
                        final byte[] bytes = (byte[])args[1];
                        if (InvocationDispatcher.this.isInstrumentationDisabled()) {
                            if (InvocationDispatcher.this.writeDisabledMessage) {
                                InvocationDispatcher.this.writeDisabledMessage = false;
                                log.info("Instrumentation disabled, no agent present");
                            }
                            return bytes;
                        }
                        InvocationDispatcher.this.writeDisabledMessage = true;
                        synchronized (InvocationDispatcher.this.context) {
                            log.debug("ClassTransformer/transformClassBytes arg[0](filename)[" + filename + "] arg[1](bytes)[" + bytes.length + "]");
                            final ClassData classData = InvocationDispatcher.this.visitClassBytes(bytes);
                            if (classData != null && classData.getMainClassBytes() != null && classData.isModified()) {
                                if (bytes.length != classData.getMainClassBytes().length) {
                                    log.debug("ClassTransformer/transformClassBytes transformed bytes[" + classData.getMainClassBytes().length + "]");
                                }
                                return classData.getMainClassBytes();
                            }
                        }
                        return null;
                    }
                });
            }
        });
    }
    
    private boolean isInstrumentationDisabled() {
        return this.disableInstrumentation || System.getProperty("newrelic.instrumentation.disabled") != null;
    }
    
    private boolean isExcludedPackage(final String packageName) {
        for (final String name : InvocationDispatcher.EXCLUDED_PACKAGES) {
            if (packageName.contains(name)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final InvocationHandler handler = this.invocationHandlers.get(proxy);
        if (handler == null) {
            this.log.error("Unknown invocation type: " + proxy + ".  Arguments: " + Arrays.asList(args));
            return null;
        }
        try {
            return handler.invoke(proxy, method, args);
        }
        catch (Throwable t) {
            this.log.error("Error:" + t.getMessage(), t);
            return null;
        }
    }
    
    private ClassData visitClassBytes(final byte[] bytes) {
        String className = "an unknown class";
        try {
            final ClassReader cr = new ClassReader(bytes);
            final ClassWriter cw = new ClassWriter(cr, 1);
            this.context.reset();
            cr.accept(new PrefilterClassVisitor(this.context, this.log), 7);
            className = this.context.getClassName();
            if (!this.context.hasTag("Lcom/newrelic/agent/android/instrumentation/Instrumented;")) {
                ClassVisitor cv = cw;
                if (this.context.getClassName().startsWith("com/newrelic/agent/android")) {
                    cv = new NewRelicClassVisitor(cv, this.context, this.log);
                }
                else if (this.context.getClassName().startsWith("android/support/")) {
                    cv = new ActivityClassVisitor(cv, this.context, this.log);
                }
                else {
                    if (this.isExcludedPackage(this.context.getClassName())) {
                        return null;
                    }
                    cv = new AnnotatingClassVisitor(cv, this.context, this.log);
                    cv = new ActivityClassVisitor(cv, this.context, this.log);
                    cv = new AsyncTaskClassVisitor(cv, this.context, this.log);
                    cv = new TraceAnnotationClassVisitor(cv, this.context, this.log);
                    cv = new WrapMethodClassVisitor(cv, this.context, this.log);
                }
                cv = new ContextInitializationClassVisitor(cv, this.context);
                cr.accept(cv, 12);
                if (bytes.length != cw.toByteArray().length) {
                    this.log.debug("[InvocationDispatcher] class[" + className + "] bytes[" + bytes.length + "] transformed[" + cw.toByteArray().length + "]");
                }
            }
            else {
                this.log.warning(MessageFormat.format("[{0}] class is already instrumented! skipping ...", this.context.getFriendlyClassName()));
            }
            return this.context.newClassData(cw.toByteArray());
        }
        catch (SkipException ex) {
            return null;
        }
        catch (HaltBuildException e) {
            throw new RuntimeException(e);
        }
        catch (Throwable t) {
            this.log.error("Unfortunately, an error has occurred while processing " + className + ". Please copy your build logs and the jar containing this class and visit http://support.newrelic.com, thanks!\n" + t.getMessage(), t);
            return new ClassData(bytes, false);
        }
    }
    
    static {
        INVOCATION_DISPATCHER_CLASS = Logger.class;
        DX_COMMAND_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("dx", "dx.bat")));
        JAVA_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("java", "java.exe")));
        AGENT_JAR_NAMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("newrelic.android.fat.jar", "newrelic.android.jar", "obfuscated.jar")));
        EXCLUDED_PACKAGES = new HashSet<String>() {
            {
                this.add("com/newrelic/agent/android");
                this.add("com/google/gson");
                this.add("com/squareup/okhttp");
            }
        };
    }
}
