// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import java.util.HashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.WeakHashMap;
import java.util.Set;
import java.util.HashMap;

public class InstrumentationContext
{
    private static final String[] ANDROID_8_MISSING_CLASS_WHITE_LIST;
    private static final HashMap<Integer, Set<String>> MISSING_CLASS_WHITE_LIST;
    private final WeakHashMap<String, Class<?>> cache;
    private final ClassRemapperConfig config;
    private Shim shim;
    private final Log log;
    private boolean classModified;
    private String className;
    private String superClassName;
    private final ArrayList<String> tags;
    private HashMap<String, String> tracedMethods;
    private HashMap<String, String> skippedMethods;
    private final HashMap<String, ArrayList<String>> tracedMethodParameters;
    
    public InstrumentationContext(final ClassRemapperConfig config, final Log log) {
        this.cache = new WeakHashMap<String, Class<?>>();
        this.tags = new ArrayList<String>();
        this.tracedMethodParameters = new HashMap<String, ArrayList<String>>();
        this.config = config;
        this.log = log;
        this.tracedMethods = new HashMap<String, String>();
        this.skippedMethods = new HashMap<String, String>();
    }
    
    public Log getLog() {
        return this.log;
    }
    
    public void reset() {
        this.classModified = false;
        this.className = null;
        this.superClassName = null;
        this.shim = null;
        this.tags.clear();
    }
    
    public void markModified() {
        this.classModified = true;
    }
    
    public boolean isClassModified() {
        return this.classModified;
    }
    
    public void addTag(final String tag) {
        this.tags.add(tag);
    }
    
    public void addUniqueTag(final String tag) {
        while (this.tags.remove(tag)) {}
        this.addTag(tag);
    }
    
    public void addTracedMethod(final String name, final String desc) {
        this.log.debug("Will trace method [" + this.className + "#" + name + ":" + desc + "] as requested");
        this.tracedMethods.put(this.className + "#" + name, desc);
    }
    
    public void addSkippedMethod(final String name, final String desc) {
        this.log.debug("Will skip all tracing in method [" + this.className + "#" + name + ":" + desc + "] as requested");
        this.skippedMethods.put(this.className + "#" + name, desc);
    }
    
    public void addTracedMethodParameter(final String methodName, final String parameterName, final String parameterClass, final String parameterValue) {
        this.log.debug("Adding traced method parameter [" + parameterName + "] for method [" + methodName + "]");
        final String name = this.className + "#" + methodName;
        if (!this.tracedMethodParameters.containsKey(name)) {
            this.tracedMethodParameters.put(name, new ArrayList<String>());
        }
        final ArrayList<String> methodParameters = this.tracedMethodParameters.get(name);
        methodParameters.add(parameterName);
        methodParameters.add(parameterClass);
        methodParameters.add(parameterValue);
    }
    
    public ArrayList<String> getTracedMethodParameters(final String methodName) {
        return this.tracedMethodParameters.get(this.className + "#" + methodName);
    }
    
    public boolean isTracedMethod(final String name, final String desc) {
        return this.searchMethodMap(this.tracedMethods, name, desc);
    }
    
    public boolean isSkippedMethod(final String name, final String desc) {
        return this.searchMethodMap(this.skippedMethods, name, desc);
    }
    
    private boolean searchMethodMap(final Map<String, String> map, final String name, final String desc) {
        final String descToMatch = map.get(this.className + "#" + name);
        return descToMatch != null && desc.equals(desc);
    }
    
    public List<String> getTags() {
        return this.tags;
    }
    
    public boolean hasTag(final String tag) {
        return this.tags.contains(tag);
    }
    
    public void setClassName(final String className) {
        this.className = className;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String getFriendlyClassName() {
        return this.className.replaceAll("/", ".");
    }
    
    public String getFriendlySuperClassName() {
        return this.superClassName.replaceAll("/", ".");
    }
    
    public String getSimpleClassName() {
        if (this.className.contains("/")) {
            return this.className.substring(this.className.lastIndexOf("/") + 1);
        }
        return this.className;
    }
    
    public void setSuperClassName(final String superClassName) {
        this.superClassName = superClassName;
    }
    
    public String getSuperClassName() {
        return this.superClassName;
    }
    
    public void setShim(final Shim shim) {
        this.shim = shim;
    }
    
    public Shim getShim() {
        return this.shim;
    }
    
    public ClassData newClassData(final byte[] mainClassBytes) {
        if (this.shim != null) {
            return new ClassData(mainClassBytes, this.shim.getClassName(), this.shim.getBytes(), this.isClassModified());
        }
        return new ClassData(mainClassBytes, this.isClassModified());
    }
    
    public ClassMethod getMethodWrapper(final ClassMethod method) {
        return this.config.getMethodWrapper(method);
    }
    
    public Collection<ClassMethod> getCallSiteReplacements(final String className, final String methodName, final String methodDesc) {
        return this.config.getCallSiteReplacements(className, methodName, methodDesc);
    }
    
    static {
        ANDROID_8_MISSING_CLASS_WHITE_LIST = new String[] { "android.view.View$AccessibilityDelegate", "android.view.accessibility.AccessibilityNodeProvider" };
        MISSING_CLASS_WHITE_LIST = new HashMap<Integer, Set<String>>() {
            {
                ((HashMap<Integer, HashSet<String>>)this).put(8, new HashSet<String>(Arrays.asList(InstrumentationContext.ANDROID_8_MISSING_CLASS_WHITE_LIST)));
            }
        };
    }
}
