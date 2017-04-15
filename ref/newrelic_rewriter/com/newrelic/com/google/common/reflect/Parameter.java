// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.reflect;

import java.util.Arrays;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.Type;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.GenericDeclaration;
import javax.annotation.Nullable;
import java.util.Iterator;
import com.newrelic.com.google.common.base.Preconditions;
import java.lang.annotation.Annotation;
import com.newrelic.com.google.common.collect.ImmutableList;
import com.newrelic.com.google.common.annotations.Beta;
import java.lang.reflect.AnnotatedElement;

@Beta
public final class Parameter implements AnnotatedElement
{
    private final Invokable<?, ?> declaration;
    private final int position;
    private final TypeToken<?> type;
    private final ImmutableList<Annotation> annotations;
    
    Parameter(final Invokable<?, ?> declaration, final int position, final TypeToken<?> type, final Annotation[] annotations) {
        this.declaration = declaration;
        this.position = position;
        this.type = type;
        this.annotations = ImmutableList.copyOf(annotations);
    }
    
    public TypeToken<?> getType() {
        return this.type;
    }
    
    public Invokable<?, ?> getDeclaringInvokable() {
        return this.declaration;
    }
    
    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
        return this.getAnnotation(annotationType) != null;
    }
    
    @Nullable
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        Preconditions.checkNotNull(annotationType);
        for (final Annotation annotation : this.annotations) {
            if (annotationType.isInstance(annotation)) {
                return annotationType.cast(annotation);
            }
        }
        return null;
    }
    
    @Override
    public Annotation[] getAnnotations() {
        return this.getDeclaredAnnotations();
    }
    
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.annotations.toArray(new Annotation[this.annotations.size()]);
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj instanceof Parameter) {
            final Parameter that = (Parameter)obj;
            return this.position == that.position && this.declaration.equals(that.declaration);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.position;
    }
    
    @Override
    public String toString() {
        return this.type + " arg" + this.position;
    }
}
