package com.hello2mao.openapm.rewriter;

import com.hello2mao.openapm.rewriter.util.Log;

import org.objectweb.asm.commons.ClassRemapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ClassRemapperConfig {

    public static final String WRAP_METHOD_IDENTIFIER = "WRAP_METHOD:";
    public static final String REPLACE_CALL_SITE_IDENTIFIER = "REPLACE_CALL_SITE:";
    private final Map<ClassMethod, ClassMethod> methodWrappers;
    private final Map<String, Collection<ClassMethod>> callSiteReplacements;
    
    public ClassRemapperConfig(final Log log) throws ClassNotFoundException {
        final Map<String, String> remappings = (Map<String, String>)getRemappings(log);
        this.methodWrappers = getMethodWrappers(remappings, log);
        this.callSiteReplacements = getCallSiteReplacements(remappings, log);
    }
    
    public ClassMethod getMethodWrapper(final ClassMethod method) {
        return this.methodWrappers.get(method);
    }
    
    public Collection<ClassMethod> getCallSiteReplacements(final String className, final String methodName, final String methodDesc) {
        final ArrayList<ClassMethod> methods = new ArrayList<ClassMethod>();
        Collection<ClassMethod> matches = this.callSiteReplacements.get(MessageFormat.format("{0}:{1}", methodName, methodDesc));
        if (matches != null) {
            methods.addAll(matches);
        }
        matches = this.callSiteReplacements.get(MessageFormat.format("{0}.{1}:{2}", className, methodName, methodDesc));
        if (matches != null) {
            methods.addAll(matches);
        }
        return methods;
    }
    
    private static Map<ClassMethod, ClassMethod> getMethodWrappers(final Map<String, String> remappings, final Log log) throws ClassNotFoundException {
        final HashMap<ClassMethod, ClassMethod> methodWrappers = new HashMap<ClassMethod, ClassMethod>();
        for (final Map.Entry<String, String> entry : remappings.entrySet()) {
            if (entry.getKey().startsWith(WRAP_METHOD_IDENTIFIER)) {
                final String originalSig = entry.getKey().substring(WRAP_METHOD_IDENTIFIER.length());
                final ClassMethod origClassMethod = ClassMethod.getClassMethod(originalSig);
                final ClassMethod wrappingMethod = ClassMethod.getClassMethod(entry.getValue());
                methodWrappers.put(origClassMethod, wrappingMethod);
            }
        }
        return methodWrappers;
    }
    
    private static Map<String, Collection<ClassMethod>> getCallSiteReplacements(final Map<String, String> remappings, final Log log) throws ClassNotFoundException {
        final HashMap<String, Set<ClassMethod>> temp = new HashMap<String, Set<ClassMethod>>();
        for (final Map.Entry<String, String> entry : remappings.entrySet()) {
            if (entry.getKey().startsWith(REPLACE_CALL_SITE_IDENTIFIER)) {
                final String originalSig = entry.getKey().substring(REPLACE_CALL_SITE_IDENTIFIER.length());
                if (originalSig.contains(".")) {
                    final ClassMethod origClassMethod = ClassMethod.getClassMethod(originalSig);
                    final ClassMethod replacement = ClassMethod.getClassMethod(entry.getValue());
                    final String key = MessageFormat.format("{0}.{1}:{2}", origClassMethod.getClassName(), origClassMethod.getMethodName(), origClassMethod.getMethodDesc());
                    Set<ClassMethod> replacements = temp.get(key);
                    if (replacements == null) {
                        replacements = new HashSet<ClassMethod>();
                        temp.put(key, replacements);
                    }
                    replacements.add(replacement);
                }
                else {
                    final String[] nameDesc = originalSig.split(":");
                    final int paren = originalSig.indexOf("(");
                    final String methodName = originalSig.substring(0, paren);
                    final String methodDesc = originalSig.substring(paren);
                    final String key2 = MessageFormat.format("{0}:{1}", methodName, methodDesc);
                    final ClassMethod replacement2 = ClassMethod.getClassMethod(entry.getValue());
                    Set<ClassMethod> replacements2 = temp.get(key2);
                    if (replacements2 == null) {
                        replacements2 = new HashSet<ClassMethod>();
                        temp.put(key2, replacements2);
                    }
                    replacements2.add(replacement2);
                }
            }
        }
        final HashMap<String, Collection<ClassMethod>> callSiteReplacements = new HashMap<String, Collection<ClassMethod>>();
        for (final Map.Entry<String, Set<ClassMethod>> entry2 : temp.entrySet()) {
            callSiteReplacements.put(entry2.getKey(), entry2.getValue());
        }
        return callSiteReplacements;
    }
    
    private static Map getRemappings(final Log log) {
        final Properties props = new Properties();
        final URL resource = ClassRemapper.class.getResource("/type_map.properties");
        if (resource == null) {
            log.error("Unable to find the type map");
            System.exit(1);
        }
        InputStream in = null;
        try {
            in = resource.openStream();
            props.load(in);
        }
        catch (Throwable ex) {
            log.error("Unable to read the type map", ex);
            System.exit(1);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ex2) {}
            }
        }
        return props;
    }
}
