// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.io.File;
import java.util.HashMap;
import java.util.Collections;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.instrument.IllegalClassFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.security.ProtectionDomain;
import java.io.OutputStream;
import com.newrelic.agent.util.Streams;
import java.io.ByteArrayOutputStream;
import java.util.List;
import com.newrelic.agent.compile.transformers.NewRelicClassTransformer;
import java.util.ArrayList;
import java.lang.instrument.ClassFileTransformer;
import com.newrelic.agent.compile.transformers.DexClassTransformer;
import com.newrelic.agent.compile.transformers.ClassRewriterTransformer;
import com.newrelic.agent.compile.transformers.NoOpClassTransformer;
import java.lang.management.ManagementFactory;
import java.lang.instrument.Instrumentation;
import java.util.Map;

public final class RewriterAgent
{
    public static final String VERSION = "5.12.2";
    public static final String DISABLE_INSTRUMENTATION_SYSTEM_PROPERTY = "newrelic.instrumentation.disabled";
    public static final String SET_INSTRUMENTATION_DISABLED_FLAG = "SET_INSTRUMENTATION_DISABLED_FLAG";
    public static final String PRINT_TO_INFO_LOG = "PRINT_TO_INFO_LOG";
    private static String agentArgs;
    private static Map<String, String> agentOptions;
    
    public static void premain(final String agentArgs, final Instrumentation instrumentation) {
        Throwable argsError = null;
        RewriterAgent.agentArgs = agentArgs;
        try {
            RewriterAgent.agentOptions = parseAgentArgs(agentArgs);
        }
        catch (Throwable t) {
            argsError = t;
        }
        final String logFileName = RewriterAgent.agentOptions.get("logfile");
        final Log log = (logFileName == null) ? new SystemErrLog(RewriterAgent.agentOptions) : new FileLogImpl(RewriterAgent.agentOptions, logFileName);
        if (argsError != null) {
            log.error("Agent args error: " + agentArgs, argsError);
        }
        final String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        final int p = nameOfRunningVM.indexOf(64);
        final String pid = nameOfRunningVM.substring(0, p);
        log.debug("Bootstrapping New Relic Android class rewriter");
        log.debug("Agent args[" + agentArgs + "]");
        log.debug("Agent running in pid " + pid + " arguments: " + agentArgs);
        try {
            NewRelicClassTransformer classTransformer;
            if (RewriterAgent.agentOptions.containsKey("deinstrument")) {
                log.info("Deinstrumenting...");
                classTransformer = new NoOpClassTransformer();
            }
            else {
                if (RewriterAgent.agentOptions.containsKey("classTransformer")) {
                    log.info("Using class transformer.");
                    classTransformer = new ClassRewriterTransformer(log);
                }
                else {
                    log.info("Using DEX transformer.");
                    classTransformer = new DexClassTransformer(log);
                }
                createInvocationDispatcher(log);
            }
            instrumentation.addTransformer(classTransformer, true);
            final List<Class<?>> classes = new ArrayList<Class<?>>();
            for (final Class<?> clazz : instrumentation.getAllLoadedClasses()) {
                if (classTransformer.modifies(clazz)) {
                    classes.add(clazz);
                }
            }
            if (!classes.isEmpty()) {
                if (instrumentation.isRetransformClassesSupported()) {
                    instrumentation.retransformClasses((Class<?>[])classes.toArray(new Class[classes.size()]));
                }
                else {
                    log.warning("Unable to retransform classes: " + classes);
                }
            }
            if (!RewriterAgent.agentOptions.containsKey("deinstrument")) {
                redefineClass(instrumentation, classTransformer, ProcessBuilder.class);
            }
        }
        catch (Throwable ex) {
            log.error("Agent startup error", ex);
            throw new RuntimeException(ex);
        }
    }
    
    public static void agentmain(final String agentArgs, final Instrumentation instrumentation) {
        premain(agentArgs, instrumentation);
    }
    
    public static String getVersion() {
        return "5.12.2";
    }
    
    public static Map<String, String> getAgentOptions() {
        return RewriterAgent.agentOptions;
    }
    
    public static String getAgentArgs() {
        return RewriterAgent.agentArgs;
    }
    
    public static String getProxyInvocationKey(final String className, final String methodName) {
        return className + "." + methodName;
    }
    
    private static void redefineClass(final Instrumentation instrumentation, final ClassFileTransformer classTransformer, final Class<?> klass) throws IOException, IllegalClassFormatException, ClassNotFoundException, UnmodifiableClassException {
        final String internalClassName = klass.getName().replace('.', '/');
        final String classPath = internalClassName + ".class";
        final ClassLoader cl = (klass.getClassLoader() == null) ? RewriterAgent.class.getClassLoader() : klass.getClassLoader();
        final InputStream stream = cl.getResourceAsStream(classPath);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        Streams.copy(stream, output);
        stream.close();
        final byte[] newBytes = classTransformer.transform(klass.getClassLoader(), internalClassName, klass, null, output.toByteArray());
        final ClassDefinition def = new ClassDefinition(klass, newBytes);
        instrumentation.redefineClasses(def);
    }
    
    public static Map<String, String> parseAgentArgs(final String agentArgs) {
        if (agentArgs == null) {
            return Collections.emptyMap();
        }
        final Map<String, String> options = new HashMap<String, String>();
        for (final String arg : agentArgs.split(";")) {
            final String[] keyValue = arg.split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("Invalid argument: " + arg);
            }
            options.put(keyValue[0], keyValue[1]);
        }
        return options;
    }
    
    public static String getAgentJarPath() throws URISyntaxException {
        return new File(RewriterAgent.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
    }
    
    private static void createInvocationDispatcher(final Log log) throws Exception {
        final Field field = InvocationDispatcher.INVOCATION_DISPATCHER_CLASS.getDeclaredField("treeLock");
        field.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
        if (field.get(null) instanceof InvocationDispatcher) {
            log.info("Detected cached instrumentation.");
        }
        else {
            field.set(null, new InvocationDispatcher(log));
        }
    }
    
    static {
        RewriterAgent.agentOptions = Collections.emptyMap();
    }
}
