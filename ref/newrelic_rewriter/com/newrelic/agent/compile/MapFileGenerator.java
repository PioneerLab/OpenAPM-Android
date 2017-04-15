// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import java.util.Collection;
import java.net.URL;
import java.util.Set;
import com.newrelic.agent.android.instrumentation.TraceConstructor;
import com.newrelic.org.objectweb.asm.Type;
import com.newrelic.agent.android.instrumentation.ReplaceCallSite;
import com.newrelic.agent.util.MethodAnnotation;
import com.newrelic.agent.util.Annotations;
import com.newrelic.agent.android.instrumentation.WrapReturn;
import com.newrelic.org.reflections.util.ClasspathHelper;
import java.util.HashMap;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Map;

public class MapFileGenerator
{
    public static void main(final String[] args) {
        if (args.length != 1) {
            System.err.println("Usage:   MapFileGenerator class_dir");
            System.exit(1);
        }
        try {
            Class.forName("com.newrelic.agent.android.Agent");
        }
        catch (Exception ex2) {
            System.err.println("Unable to load agent classes");
            System.exit(1);
        }
        final Map<String, String> remapperProperties = getRemapperProperties();
        if (remapperProperties.size() == 0) {
            System.err.println("No class mappings were found");
            System.exit(1);
        }
        for (final Map.Entry<String, String> entry : remapperProperties.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        final Properties props = new Properties();
        props.putAll(remapperProperties);
        try {
            System.out.println("Storing mapping data to " + args[0]);
            final FileOutputStream out = new FileOutputStream(args[0]);
            props.store(out, "");
            out.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    static Map<String, String> getRemapperProperties() {
        final Map<String, String> classMap = new HashMap<String, String>();
        final Set<URL> urls = ClasspathHelper.forPackage("com.newrelic.agent", new ClassLoader[0]);
        final Collection<MethodAnnotation> wrapReturnAnnotations = Annotations.getMethodAnnotations(WrapReturn.class, "com/newrelic/agent", urls);
        for (final MethodAnnotation annotation : wrapReturnAnnotations) {
            final String originalClassName = annotation.getAttributes().get("className");
            final String originalMethodName = annotation.getAttributes().get("methodName");
            final String originalMethodDesc = annotation.getAttributes().get("methodDesc");
            final String newClassName = annotation.getClassName();
            final String newMethodName = annotation.getMethodName();
            classMap.put("WRAP_METHOD:" + originalClassName.replace('.', '/') + '.' + originalMethodName + originalMethodDesc, newClassName + '.' + newMethodName + annotation.getMethodDesc());
        }
        final Collection<MethodAnnotation> callSiteAnnotations = Annotations.getMethodAnnotations(ReplaceCallSite.class, "com/newrelic/agent", urls);
        for (final MethodAnnotation annotation2 : callSiteAnnotations) {
            Boolean isStatic = annotation2.getAttributes().get("isStatic");
            final String scope = annotation2.getAttributes().get("scope");
            if (isStatic == null) {
                isStatic = new Boolean(false);
            }
            final String originalMethodName2 = annotation2.getMethodName();
            String originalMethodDesc2 = annotation2.getMethodDesc();
            if (!isStatic) {
                final Type[] argTypes = Type.getArgumentTypes(originalMethodDesc2);
                final Type[] newArgTypes = new Type[argTypes.length - 1];
                for (int i = 0; i < newArgTypes.length; ++i) {
                    newArgTypes[i] = argTypes[i + 1];
                }
                final Type returnType = Type.getReturnType(originalMethodDesc2);
                originalMethodDesc2 = Type.getMethodDescriptor(returnType, newArgTypes);
            }
            final String newClassName2 = annotation2.getClassName();
            final String newMethodName2 = annotation2.getMethodName();
            if (scope == null) {
                classMap.put("REPLACE_CALL_SITE:" + originalMethodName2 + originalMethodDesc2, newClassName2 + '.' + newMethodName2 + annotation2.getMethodDesc());
            }
            else {
                classMap.put("REPLACE_CALL_SITE:" + scope.replace('.', '/') + "." + originalMethodName2 + originalMethodDesc2, newClassName2 + '.' + newMethodName2 + annotation2.getMethodDesc());
            }
        }
        final Collection<MethodAnnotation> constructorAnnotations = Annotations.getMethodAnnotations(TraceConstructor.class, "com/newrelic/agent", urls);
        for (final MethodAnnotation annotation3 : constructorAnnotations) {
            final int typeStart = annotation3.getMethodDesc().indexOf(")L");
            final int typeEnd = annotation3.getMethodDesc().lastIndexOf(";");
            System.out.print("Start: " + typeStart + " end: " + typeEnd + " for " + annotation3.getMethodDesc());
            final String originalClassName2 = annotation3.getMethodDesc().substring(typeStart + 2, typeEnd);
            final String originalMethodDesc3 = annotation3.getMethodDesc().substring(0, typeStart + 1) + "V";
            final String newClassName3 = annotation3.getClassName();
            final String newMethodName3 = annotation3.getMethodName();
            classMap.put("REPLACE_CALL_SITE:" + originalClassName2.replace('.', '/') + "." + "<init>" + originalMethodDesc3, newClassName3 + '.' + newMethodName3 + annotation3.getMethodDesc());
        }
        return classMap;
    }
}
