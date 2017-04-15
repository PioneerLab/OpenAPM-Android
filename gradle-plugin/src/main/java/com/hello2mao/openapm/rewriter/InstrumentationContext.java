package com.hello2mao.openapm.rewriter;

import com.hello2mao.openapm.rewriter.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class InstrumentationContext {
    private static String[] ANDROID_8_MISSING_CLASS_WHITE_LIST
            = new String[] { "android.view.View$AccessibilityDelegate",
            "android.view.accessibility.AccessibilityNodeProvider" };
    private static HashMap<Integer, Set<String>> MISSING_CLASS_WHITE_LIST
            = new HashMap<Integer, Set<String>>() {
        {
            put(8, new HashSet<>(Arrays.asList(InstrumentationContext.ANDROID_8_MISSING_CLASS_WHITE_LIST)));
        }
    };
    private WeakHashMap<String, Class<?>> cache;
    private ClassRemapperConfig config;
    private Log log;
    private boolean classModified;
    private String className;
    private String superClassName;
    private ArrayList<String> tags;
    private HashMap<String, String> tracedMethods;
    private HashMap<String, String> skippedMethods;
    private HashMap<String, ArrayList<String>> tracedMethodParameters;
    
    public InstrumentationContext(ClassRemapperConfig config, Log log) {
        this.cache = new WeakHashMap<>();
        this.tags = new ArrayList<>();
        this.tracedMethodParameters = new HashMap<>();
        this.config = config;
        this.log = log;
        this.tracedMethods = new HashMap<>();
        this.skippedMethods = new HashMap<>();
    }
    
    public Log getLog() {
        return this.log;
    }
    
    public void reset() {
        this.classModified = false;
        this.className = null;
        this.superClassName = null;
        this.tags.clear();
    }
    
    public void markModified() {
        this.classModified = true;
    }
    
    public boolean isClassModified() {
        return this.classModified;
    }
    
    public void addTag(String tag) {
        this.tags.add(tag);
    }
    
    public void addUniqueTag(String tag) {
        while (this.tags.remove(tag)) {}
        this.addTag(tag);
    }
    
    public void addTracedMethod(String name, String desc) {
        this.log.debug("Will trace method [" + this.className + "#" + name + ":" + desc + "] as requested");
        this.tracedMethods.put(this.className + "#" + name, desc);
    }
    
    public void addSkippedMethod(String name, String desc) {
        this.log.debug("Will skip all tracing in method [" + this.className + "#" + name + ":" + desc + "] as requested");
        this.skippedMethods.put(this.className + "#" + name, desc);
    }
    
    public void addTracedMethodParameter(String methodName, String parameterName, String parameterClass, String parameterValue) {
        this.log.debug("Adding traced method parameter [" + parameterName + "] for method [" + methodName + "]");
        String name = this.className + "#" + methodName;
        if (!this.tracedMethodParameters.containsKey(name)) {
            this.tracedMethodParameters.put(name, new ArrayList<String>());
        }
        ArrayList<String> methodParameters = this.tracedMethodParameters.get(name);
        methodParameters.add(parameterName);
        methodParameters.add(parameterClass);
        methodParameters.add(parameterValue);
    }
    
    public ArrayList<String> getTracedMethodParameters(String methodName) {
        return this.tracedMethodParameters.get(this.className + "#" + methodName);
    }
    
    public boolean isTracedMethod(String name, String desc) {
        return this.searchMethodMap(this.tracedMethods, name, desc);
    }
    
    public boolean isSkippedMethod(String name, String desc) {
        return this.searchMethodMap(this.skippedMethods, name, desc);
    }
    
    private boolean searchMethodMap(Map<String, String> map, String name, String desc) {
        String descToMatch = map.get(this.className + "#" + name);
        return descToMatch != null && desc.equals(desc);
    }
    
    public List<String> getTags() {
        return this.tags;
    }
    
    public boolean hasTag(String tag) {
        return this.tags.contains(tag);
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getClassName() {
        return className;
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
    
    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }
    
    public String getSuperClassName() {
        return this.superClassName;
    }

    public ClassData newClassData(final byte[] mainClassBytes) {
        return new ClassData(mainClassBytes, this.isClassModified());
    }

    public ClassMethod getMethodWrapper(ClassMethod method) {
        return this.config.getMethodWrapper(method);
    }
    
    public Collection<ClassMethod> getCallSiteReplacements(String className, String methodName, String methodDesc) {
        return this.config.getCallSiteReplacements(className, methodName, methodDesc);
    }

}
