// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections;

import com.newrelic.com.google.common.collect.Lists;
import java.util.ArrayList;
import com.newrelic.org.reflections.util.ClasspathHelper;
import java.util.Arrays;
import java.lang.annotation.Annotation;
import javax.annotation.Nullable;
import java.lang.reflect.Member;
import com.newrelic.com.google.common.collect.Iterables;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.lang.reflect.Field;
import java.util.Iterator;
import com.newrelic.com.google.common.collect.ImmutableSet;
import com.newrelic.com.google.common.collect.Collections2;
import java.util.Collection;
import com.newrelic.com.google.common.base.Predicates;
import com.newrelic.com.google.common.collect.Sets;
import java.util.Set;
import com.newrelic.com.google.common.base.Predicate;
import java.util.List;

public abstract class ReflectionUtils
{
    private static List<String> primitiveNames;
    private static List<Class> primitiveTypes;
    private static List<String> primitiveDescriptors;
    
    public static Set<Class<?>> getAllSuperTypes(final Class<?> type, final Predicate<? super Class<?>> predicate) {
        final Set<Class<?>> result = (Set<Class<?>>)Sets.newHashSet();
        if (type != null) {
            result.add(type);
            result.addAll(getAllSuperTypes(type.getSuperclass(), Predicates.alwaysTrue()));
            for (final Class<?> inter : type.getInterfaces()) {
                result.addAll(getAllSuperTypes(inter, Predicates.alwaysTrue()));
            }
        }
        return (Set<Class<?>>)ImmutableSet.copyOf((Collection<?>)Collections2.filter((Collection<? extends E>)result, (Predicate<? super E>)predicate));
    }
    
    public static Set<Class<?>> getAllSuperTypes(final Iterable<? extends Class<?>> types, final Predicate<? super Class<?>> predicate) {
        final Set<Class<?>> result = (Set<Class<?>>)Sets.newHashSet();
        for (final Class<?> type : types) {
            result.addAll(getAllSuperTypes(type, predicate));
        }
        return result;
    }
    
    public static Set<Field> getAllFields(final Class<?> type, final Predicate<? super Field> predicate) {
        final Set<Field> result = (Set<Field>)Sets.newHashSet();
        for (final Class<?> t : getAllSuperTypes(type, Predicates.alwaysTrue())) {
            Collections.addAll(result, t.getDeclaredFields());
        }
        return (Set<Field>)ImmutableSet.copyOf((Collection<?>)Collections2.filter((Collection<? extends E>)result, (Predicate<? super E>)predicate));
    }
    
    public static Set<Field> getAllFields(final Iterable<? extends Class<?>> types, final Predicate<? super Field> predicate) {
        final Set<Field> result = (Set<Field>)Sets.newHashSet();
        for (final Class<?> type : types) {
            result.addAll(getAllFields(type, predicate));
        }
        return result;
    }
    
    public static Set<Method> getAllMethods(final Class<?> type, final Predicate<? super Method> predicate) {
        final Set<Method> result = (Set<Method>)Sets.newHashSet();
        for (final Class<?> t : getAllSuperTypes(type, Predicates.alwaysTrue())) {
            Collections.addAll(result, t.isInterface() ? t.getMethods() : t.getDeclaredMethods());
        }
        return (Set<Method>)ImmutableSet.copyOf((Collection<?>)Collections2.filter((Collection<? extends E>)result, (Predicate<? super E>)predicate));
    }
    
    public static Set<Method> getAllMethods(final Iterable<? extends Class<?>> types, final Predicate<? super Method> predicate) {
        final Set<Method> result = (Set<Method>)Sets.newHashSet();
        for (final Class<?> type : types) {
            result.addAll(getAllMethods(type, predicate));
        }
        return (Set<Method>)ImmutableSet.copyOf((Collection<?>)result);
    }
    
    public static <T extends AnnotatedElement> Set<T> getAll(final Iterable<? extends T> elements, final Predicate<? super T> predicate) {
        return (Set<T>)ImmutableSet.copyOf((Iterable<?>)Iterables.filter((Iterable<? extends E>)elements, (Predicate<? super E>)predicate));
    }
    
    public static <T extends Member> Predicate<T> withName(final String name) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                return input != null && input.getName().equals(name);
            }
        };
    }
    
    public static <T extends Member> Predicate<T> withPrefix(final String prefix) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                return input != null && input.getName().startsWith(prefix);
            }
        };
    }
    
    public static <T extends AnnotatedElement> Predicate<T> withAnnotation(final Class<? extends Annotation> annotation) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                return input != null && input.isAnnotationPresent(annotation);
            }
        };
    }
    
    public static <T extends AnnotatedElement> Predicate<T> withAnnotations(final Class<? extends Annotation>... annotations) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                return input != null && Arrays.equals(annotations, input.getAnnotations());
            }
        };
    }
    
    public static <T extends AnnotatedElement> Predicate<T> withAnnotation(final Annotation annotation) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                return input != null && input.isAnnotationPresent(annotation.annotationType()) && ReflectionUtils.areAnnotationMembersMatching(input.getAnnotation(annotation.annotationType()), annotation);
            }
        };
    }
    
    public static <T extends AnnotatedElement> Predicate<T> withAnnotations(final Annotation... annotations) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                if (input != null) {
                    final Annotation[] inputAnnotations = input.getAnnotations();
                    if (inputAnnotations.length == annotations.length) {
                        for (int i = 0; i < inputAnnotations.length; ++i) {
                            if (!ReflectionUtils.areAnnotationMembersMatching(inputAnnotations[i], annotations[i])) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
        };
    }
    
    public static Predicate<Method> withParameters(final Class<?>... types) {
        return new Predicate<Method>() {
            public boolean apply(@Nullable final Method input) {
                return input != null && Arrays.equals(input.getParameterTypes(), types);
            }
        };
    }
    
    public static Predicate<Method> withParametersAssignableTo(final Class... types) {
        return new Predicate<Method>() {
            public boolean apply(@Nullable final Method input) {
                if (input != null) {
                    final Class<?>[] parameterTypes = input.getParameterTypes();
                    if (parameterTypes.length == types.length) {
                        for (int i = 0; i < parameterTypes.length; ++i) {
                            if (!types[i].isAssignableFrom(parameterTypes[i])) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        };
    }
    
    public static Predicate<Method> withParametersCount(final int count) {
        return new Predicate<Method>() {
            public boolean apply(@Nullable final Method input) {
                return input != null && input.getParameterTypes().length == count;
            }
        };
    }
    
    public static Predicate<Method> withParameterAnnotations(final Annotation... annotations) {
        return new Predicate<Method>() {
            public boolean apply(@Nullable final Method input) {
                if (input != null && annotations != null) {
                    final Annotation[][] parameterAnnotations = input.getParameterAnnotations();
                    if (annotations.length != parameterAnnotations.length) {
                        return false;
                    }
                    for (int i = 0; i < parameterAnnotations.length; ++i) {
                        boolean any = false;
                        for (final Annotation annotation : parameterAnnotations[i]) {
                            if (ReflectionUtils.areAnnotationMembersMatching(annotations[i], annotation)) {
                                any = true;
                                break;
                            }
                        }
                        if (!any) {
                            return false;
                        }
                    }
                }
                return true;
            }
        };
    }
    
    public static Predicate<Method> withParameterAnnotations(final Class<? extends Annotation>... annotationClasses) {
        return new Predicate<Method>() {
            public boolean apply(@Nullable final Method input) {
                if (input != null && annotationClasses != null) {
                    final Annotation[][] parameterAnnotations = input.getParameterAnnotations();
                    if (annotationClasses.length != parameterAnnotations.length) {
                        return false;
                    }
                    for (int i = 0; i < parameterAnnotations.length; ++i) {
                        boolean any = false;
                        for (final Annotation annotation : parameterAnnotations[i]) {
                            if (annotationClasses[i].equals(annotation.annotationType())) {
                                any = true;
                                break;
                            }
                        }
                        if (!any) {
                            return false;
                        }
                    }
                }
                return true;
            }
        };
    }
    
    public static <T> Predicate<Field> withType(final Class<T> type) {
        return new Predicate<Field>() {
            public boolean apply(@Nullable final Field input) {
                return input != null && input.getType().equals(type);
            }
        };
    }
    
    public static <T> Predicate<Field> withTypeAssignableTo(final Class<T> type) {
        return new Predicate<Field>() {
            public boolean apply(@Nullable final Field input) {
                return input != null && type.isAssignableFrom(input.getType());
            }
        };
    }
    
    public static <T> Predicate<Method> withReturnType(final Class<T> type) {
        return new Predicate<Method>() {
            public boolean apply(@Nullable final Method input) {
                return input != null && input.getReturnType().equals(type);
            }
        };
    }
    
    public static <T> Predicate<Method> withReturnTypeAssignableTo(final Class<T> type) {
        return new Predicate<Method>() {
            public boolean apply(@Nullable final Method input) {
                return input != null && type.isAssignableFrom(input.getReturnType());
            }
        };
    }
    
    public static <T extends Member> Predicate<T> withModifier(final int mod) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                return input != null && (input.getModifiers() & mod) != 0x0;
            }
        };
    }
    
    public static boolean areAnnotationMembersMatching(final Annotation annotation1, final Annotation annotation2) {
        if (annotation2 != null && annotation1.annotationType() == annotation2.annotationType()) {
            for (final Method method : annotation1.annotationType().getDeclaredMethods()) {
                try {
                    if (!method.invoke(annotation1, new Object[0]).equals(method.invoke(annotation2, new Object[0]))) {
                        return false;
                    }
                }
                catch (Exception e) {
                    throw new ReflectionsException(String.format("could not invoke method %s on annotation %s", method.getName(), annotation1.annotationType()), e);
                }
            }
            return true;
        }
        return false;
    }
    
    public static <T extends AnnotatedElement> Set<T> getMatchingAnnotations(final Set<T> annotatedElements, final Annotation annotation) {
        final Set<T> result = (Set<T>)Sets.newHashSet();
        for (final T annotatedElement : annotatedElements) {
            final Annotation annotation2 = annotatedElement.getAnnotation(annotation.annotationType());
            if (areAnnotationMembersMatching(annotation, annotation2)) {
                result.add(annotatedElement);
            }
        }
        return result;
    }
    
    public static Class<?> forName(final String typeName, final ClassLoader... classLoaders) {
        if (getPrimitiveNames().contains(typeName)) {
            return getPrimitiveTypes().get(getPrimitiveNames().indexOf(typeName));
        }
        String type;
        if (typeName.contains("[")) {
            final int i = typeName.indexOf("[");
            type = typeName.substring(0, i);
            final String array = typeName.substring(i).replace("]", "");
            if (getPrimitiveNames().contains(type)) {
                type = getPrimitiveDescriptors().get(getPrimitiveNames().indexOf(type));
            }
            else {
                type = "L" + type + ";";
            }
            type = array + type;
        }
        else {
            type = typeName;
        }
        final ClassLoader[] arr$ = ClasspathHelper.classLoaders(classLoaders);
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final ClassLoader classLoader = arr$[i$];
            try {
                return Class.forName(type, false, classLoader);
            }
            catch (Throwable e) {
                ++i$;
                continue;
            }
            break;
        }
        return null;
    }
    
    public static <T> List<Class<? extends T>> forNames(final Iterable<String> classes, final ClassLoader... classLoaders) {
        final List<Class<? extends T>> result = new ArrayList<Class<? extends T>>();
        for (final String className : classes) {
            result.add((Class<? extends T>)forName(className, classLoaders));
        }
        return result;
    }
    
    public static List<String> names(final Iterable<Class<?>> types) {
        final List<String> result = (List<String>)Lists.newArrayList();
        for (final Class<?> type : types) {
            result.add(type.getName());
        }
        return result;
    }
    
    public static List<String> getPrimitiveNames() {
        return (ReflectionUtils.primitiveNames == null) ? (ReflectionUtils.primitiveNames = Lists.newArrayList("boolean", "char", "byte", "short", "int", "long", "float", "double", "void")) : ReflectionUtils.primitiveNames;
    }
    
    public static List<Class> getPrimitiveTypes() {
        return (ReflectionUtils.primitiveTypes == null) ? (ReflectionUtils.primitiveTypes = (List<Class>)Lists.newArrayList(Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE)) : ReflectionUtils.primitiveTypes;
    }
    
    public static List<String> getPrimitiveDescriptors() {
        return (ReflectionUtils.primitiveDescriptors == null) ? (ReflectionUtils.primitiveDescriptors = Lists.newArrayList("Z", "C", "B", "S", "I", "J", "F", "D", "V")) : ReflectionUtils.primitiveDescriptors;
    }
}
