// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.serializers;

import com.newrelic.org.reflections.ReflectionUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import com.newrelic.org.reflections.ReflectionsException;
import com.newrelic.com.google.common.base.Joiner;
import com.newrelic.com.google.common.collect.Multimap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.newrelic.com.google.common.collect.Multimaps;
import com.newrelic.com.google.common.collect.Sets;
import java.util.Set;
import com.newrelic.com.google.common.base.Supplier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Collections;
import com.newrelic.com.google.common.collect.Lists;
import com.newrelic.org.reflections.scanners.TypeElementsScanner;
import com.newrelic.org.reflections.scanners.Scanner;
import com.newrelic.org.reflections.scanners.TypesScanner;
import java.io.IOException;
import com.newrelic.com.google.common.io.Files;
import java.nio.charset.Charset;
import java.util.Date;
import com.newrelic.org.reflections.util.Utils;
import java.io.File;
import com.newrelic.org.reflections.Reflections;
import java.io.InputStream;

public class JavaCodeSerializer implements Serializer
{
    private static final char pathSeparator = '$';
    private static final String arrayDescriptor = "$$";
    private static final String tokenSeparator = "_";
    
    public Reflections read(final InputStream inputStream) {
        throw new UnsupportedOperationException("read is not implemented on JavaCodeSerializer");
    }
    
    public File save(final Reflections reflections, String name) {
        if (name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }
        final String filename = name.replace('.', '/').concat(".java");
        final File file = Utils.prepareFile(filename);
        final int lastDot = name.lastIndexOf(46);
        String packageName;
        String className;
        if (lastDot == -1) {
            packageName = "";
            className = name.substring(name.lastIndexOf(47) + 1);
        }
        else {
            packageName = name.substring(name.lastIndexOf(47) + 1, lastDot);
            className = name.substring(lastDot + 1);
        }
        try {
            final StringBuilder sb = new StringBuilder();
            sb.append("//generated using Reflections JavaCodeSerializer").append(" [").append(new Date()).append("]").append("\n");
            if (packageName.length() != 0) {
                sb.append("package ").append(packageName).append(";\n");
                sb.append("\n");
            }
            sb.append("import static org.reflections.serializers.JavaCodeSerializer.*;\n");
            sb.append("\n");
            sb.append("public interface ").append(className).append(" extends IElement").append(" {\n\n");
            sb.append(this.toString(reflections));
            sb.append("}\n");
            Files.write(sb.toString(), new File(filename), Charset.defaultCharset());
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
        return file;
    }
    
    public String toString(final Reflections reflections) {
        if ((reflections.getStore().get(TypesScanner.class).isEmpty() || reflections.getStore().get(TypeElementsScanner.class).isEmpty()) && Reflections.log != null) {
            Reflections.log.warn("JavaCodeSerializer needs TypeScanner and TypeElemenetsScanner configured");
        }
        final StringBuilder sb = new StringBuilder();
        List<String> prevPaths = (List<String>)Lists.newArrayList();
        int indent = 1;
        final List<String> keys = (List<String>)Lists.newArrayList((Iterable<?>)reflections.getStore().get(TypesScanner.class).keySet());
        Collections.sort(keys);
        for (final String fqn : keys) {
            List<String> typePaths;
            int i;
            for (typePaths = Lists.newArrayList(fqn.split("\\.")), i = 0; i < Math.min(typePaths.size(), prevPaths.size()) && typePaths.get(i).equals(prevPaths.get(i)); ++i) {}
            for (int j = prevPaths.size(); j > i; --j) {
                sb.append(Utils.repeat("\t", --indent)).append("}\n");
            }
            for (int j = i; j < typePaths.size() - 1; ++j) {
                sb.append(Utils.repeat("\t", indent++)).append("public interface ").append(this.getNonDuplicateName(typePaths.get(j), typePaths, j)).append(" extends IPackage").append(" {\n");
            }
            final String className = typePaths.get(typePaths.size() - 1);
            final List<String> fields = (List<String>)Lists.newArrayList();
            final Multimap<String, String> methods = (Multimap<String, String>)Multimaps.newSetMultimap(new HashMap<Object, Collection<Object>>(), (Supplier<? extends Set<Object>>)new Supplier<Set<String>>() {
                public Set<String> get() {
                    return (Set<String>)Sets.newHashSet();
                }
            });
            for (final String element : reflections.getStore().get(TypeElementsScanner.class, fqn)) {
                if (element.contains("(")) {
                    if (element.startsWith("<")) {
                        continue;
                    }
                    final int i2 = element.indexOf(40);
                    final String name = element.substring(0, i2);
                    final String params = element.substring(i2 + 1, element.indexOf(")"));
                    String paramsDescriptor = "";
                    if (params.length() != 0) {
                        paramsDescriptor = "_" + params.replace('.', '$').replace(", ", "_").replace("[]", "$$");
                    }
                    final String normalized = name + paramsDescriptor;
                    methods.put(name, normalized);
                }
                else {
                    fields.add(element);
                }
            }
            sb.append(Utils.repeat("\t", indent++)).append("public interface ").append(this.getNonDuplicateName(className, typePaths, typePaths.size() - 1)).append(" extends IClass").append(" {\n");
            if (!fields.isEmpty()) {
                for (final String field : fields) {
                    sb.append(Utils.repeat("\t", indent)).append("public interface ").append(this.getNonDuplicateName(field, typePaths)).append(" extends IField").append(" {}\n");
                }
            }
            if (!methods.isEmpty()) {
                for (final Map.Entry<String, String> entry : methods.entries()) {
                    final String simpleName = entry.getKey();
                    final String normalized2 = entry.getValue();
                    String methodName = (methods.get(simpleName).size() == 1) ? simpleName : normalized2;
                    methodName = this.getNonDuplicateName(methodName, fields);
                    sb.append(Utils.repeat("\t", indent)).append("public interface ").append(this.getNonDuplicateName(methodName, typePaths)).append(" extends IMethod").append(" {}\n");
                }
            }
            prevPaths = typePaths;
        }
        for (int k = prevPaths.size(); k >= 1; --k) {
            sb.append(Utils.repeat("\t", k)).append("}\n");
        }
        return sb.toString();
    }
    
    private String getNonDuplicateName(final String candidate, final List<String> prev, final int offset) {
        for (int i = 0; i < offset; ++i) {
            if (candidate.equals(prev.get(i))) {
                return this.getNonDuplicateName(candidate + "_", prev, offset);
            }
        }
        return candidate;
    }
    
    private String getNonDuplicateName(final String candidate, final List<String> prev) {
        return this.getNonDuplicateName(candidate, prev, prev.size());
    }
    
    public static Class<?> resolveClassOf(final Class<? extends IElement> element) throws ClassNotFoundException {
        Class<?> cursor = element;
        final List<Class<? extends IElement>> path = (List<Class<? extends IElement>>)Lists.newArrayList();
        while (cursor != null && IElement.class.isAssignableFrom(cursor)) {
            path.add((Class<? extends IElement>)cursor);
            cursor = cursor.getDeclaringClass();
        }
        Collections.reverse(path);
        int i = 1;
        final List<String> ognl = (List<String>)Lists.newArrayList();
        while (i < path.size() && (IPackage.class.isAssignableFrom(path.get(i)) || IClass.class.isAssignableFrom(path.get(i)))) {
            ognl.add(path.get(i).getSimpleName());
            ++i;
        }
        final String classOgnl = Joiner.on(".").join(ognl).replace(".$", "$");
        return Class.forName(classOgnl);
    }
    
    public static Class<?> resolveClass(final Class<? extends IClass> aClass) {
        try {
            return resolveClassOf(aClass);
        }
        catch (Exception e) {
            throw new ReflectionsException("could not resolve to class " + aClass.getName(), e);
        }
    }
    
    public static Field resolveField(final Class<? extends IField> aField) {
        try {
            final String name = aField.getSimpleName();
            return resolveClassOf(aField).getDeclaredField(name);
        }
        catch (Exception e) {
            throw new ReflectionsException("could not resolve to field " + aField.getName(), e);
        }
    }
    
    public static Method resolveMethod(final Class<? extends IMethod> aMethod) {
        final String methodOgnl = aMethod.getSimpleName();
        try {
            String methodName;
            Class<?>[] paramTypes;
            if (methodOgnl.contains("_")) {
                methodName = methodOgnl.substring(0, methodOgnl.indexOf("_"));
                final String[] params = methodOgnl.substring(methodOgnl.indexOf("_") + 1).split("_");
                paramTypes = (Class<?>[])new Class[params.length];
                for (int i = 0; i < params.length; ++i) {
                    final String typeName = params[i].replace("$$", "[]").replace('$', '.');
                    paramTypes[i] = ReflectionUtils.forName(typeName, new ClassLoader[0]);
                }
            }
            else {
                methodName = methodOgnl;
                paramTypes = null;
            }
            return resolveClassOf(aMethod).getDeclaredMethod(methodName, paramTypes);
        }
        catch (Exception e) {
            throw new ReflectionsException("could not resolve to method " + aMethod.getName(), e);
        }
    }
    
    public interface IMethod extends IElement
    {
    }
    
    public interface IElement
    {
    }
    
    public interface IField extends IElement
    {
    }
    
    public interface IClass extends IElement
    {
    }
    
    public interface IPackage extends IElement
    {
    }
}
