// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.transformers;

import java.lang.instrument.ClassFileTransformer;

public interface NewRelicClassTransformer extends ClassFileTransformer
{
    public static final String DEXER_CLASS_NAME = "com/android/dx/command/dexer/Main";
    public static final String DEXER_METHOD_NAME = "processClass";
    public static final String ANT_DEX_CLASS_NAME = "com/android/ant/DexExecTask";
    public static final String ANT_DEX_METHOD_NAME = "preDexLibraries";
    public static final String MAVEN_DEX_CLASS_NAME = "com/jayway/maven/plugins/android/phase08preparepackage/DexMojo";
    public static final String PROCESS_BUILDER_CLASS_NAME = "java/lang/ProcessBuilder";
    public static final String PROCESS_BUILDER_METHOD_NAME = "start";
    public static final String NR_CLASS_REWRITER_CLASS_NAME = "com/newrelic/agent/compile/ClassTransformer";
    public static final String NR_CLASS_REWRITER_METHOD_NAME = "transformClassBytes";
    public static final String NR_CLASS_REWRITER_METHOD_SIG = "(Ljava/lang/String;[B)[B";
    
    boolean modifies(final Class<?> p0);
}
