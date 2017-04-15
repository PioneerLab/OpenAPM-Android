// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.util;

import javax.annotation.Nullable;
import com.newrelic.org.slf4j.LoggerFactory;
import com.newrelic.org.slf4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import com.newrelic.org.reflections.ReflectionsException;
import com.newrelic.org.reflections.ReflectionUtils;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.io.File;

public abstract class Utils
{
    public static String repeat(final String string, final int times) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; ++i) {
            sb.append(string);
        }
        return sb.toString();
    }
    
    public static boolean isEmpty(final String s) {
        return s == null || s.length() == 0;
    }
    
    public static boolean isEmpty(final Object[] objects) {
        return objects == null || objects.length == 0;
    }
    
    public static File prepareFile(final String filename) {
        final File file = new File(filename);
        final File parent = file.getAbsoluteFile().getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        return file;
    }
    
    public static Method getMethodFromDescriptor(final String descriptor, final ClassLoader... classLoaders) throws ReflectionsException {
        final int p0 = descriptor.indexOf(40);
        final String methodKey = descriptor.substring(0, p0);
        final String methodParameters = descriptor.substring(p0 + 1, descriptor.length() - 1);
        final int p2 = methodKey.lastIndexOf(46);
        final String className = methodKey.substring(methodKey.lastIndexOf(32) + 1, p2);
        final String methodName = methodKey.substring(p2 + 1);
        Class<?>[] parameterTypes = null;
        if (!isEmpty(methodParameters)) {
            final String[] parameterNames = methodParameters.split(", ");
            final List<Class<?>> result = new ArrayList<Class<?>>(parameterNames.length);
            for (final String className2 : parameterNames) {
                result.add(ReflectionUtils.forName(className2, new ClassLoader[0]));
            }
            parameterTypes = result.toArray(new Class[result.size()]);
        }
        final Class<?> aClass = ReflectionUtils.forName(className, classLoaders);
        try {
            if (descriptor.contains("<init>")) {
                return null;
            }
            return aClass.getDeclaredMethod(methodName, parameterTypes);
        }
        catch (NoSuchMethodException e) {
            throw new ReflectionsException("Can't resolve method named " + methodName, e);
        }
    }
    
    public static Field getFieldFromString(final String field, final ClassLoader... classLoaders) {
        final String className = field.substring(0, field.lastIndexOf(46));
        final String fieldName = field.substring(field.lastIndexOf(46) + 1);
        try {
            return ReflectionUtils.forName(className, classLoaders).getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e) {
            throw new ReflectionsException("Can't resolve field named " + fieldName, e);
        }
    }
    
    public static void close(final InputStream closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Nullable
    public static Logger findLogger(final Class<?> aClass) {
        try {
            Class.forName("com.newrelic.org.slf4j.impl.StaticLoggerBinder");
            return LoggerFactory.getLogger(aClass);
        }
        catch (Throwable e) {
            return null;
        }
    }
}
