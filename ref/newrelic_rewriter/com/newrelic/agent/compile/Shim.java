// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;

public final class Shim
{
    public static final String SHIM_CLASS_SUFFIX = "$$NewRelicShim$$1";
    private final String className;
    private final String superClassName;
    private final HashSet<ClassMethod> overrides;
    private final byte[] bytes;
    
    public Shim(final String className, final String superClassName, final byte[] bytes, final List<ClassMethod> overrides) {
        this.className = className;
        this.superClassName = superClassName;
        this.bytes = bytes;
        this.overrides = new HashSet<ClassMethod>(overrides);
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String getFriendlyClassName() {
        return this.className.replaceAll("/", ".");
    }
    
    public String getSuperClassName() {
        return this.superClassName;
    }
    
    public boolean overrides(final String className, final String name, final String signature) {
        return this.overrides.contains(new ClassMethod(className, name, signature));
    }
    
    public byte[] getBytes() {
        return this.bytes;
    }
    
    public static boolean isShimClass(final String className) {
        return className.endsWith("$$NewRelicShim$$1");
    }
    
    public static String getShimClassName(final String className) {
        return className + "$$NewRelicShim$$1";
    }
    
    public static File getShimClassFile(final String shimClassName, final File directory) {
        final String[] parts = shimClassName.split("/");
        final String className = parts[parts.length - 1];
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; ++i) {
            sb.append(parts[i]);
            sb.append(File.separator);
        }
        final String packageDir = sb.toString();
        return new File(directory.getAbsolutePath() + File.separator + packageDir + className + ".class");
    }
}
