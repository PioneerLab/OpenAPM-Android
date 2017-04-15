// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.transformers;

import com.newrelic.org.objectweb.asm.Type;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;

public final class NoOpClassTransformer implements NewRelicClassTransformer
{
    private static HashSet<String> classVisitors;
    
    @Override
    public byte[] transform(final ClassLoader classLoader, final String s, final Class<?> aClass, final ProtectionDomain protectionDomain, final byte[] bytes) throws IllegalClassFormatException {
        return null;
    }
    
    @Override
    public boolean modifies(final Class<?> clazz) {
        final Type t = Type.getType(clazz);
        return NoOpClassTransformer.classVisitors.contains(t.getInternalName());
    }
    
    static {
        NoOpClassTransformer.classVisitors = new HashSet<String>() {
            {
                this.add("com/android/dx/command/dexer/Main");
                this.add("com/android/ant/DexExecTask");
                this.add("com/jayway/maven/plugins/android/phase08preparepackage/DexMojo");
                this.add("java/lang/ProcessBuilder");
                this.add("com/newrelic/agent/compile/ClassTransformer");
            }
        };
    }
}
