// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.scanners;

import com.newrelic.org.reflections.util.Utils;
import com.newrelic.com.google.common.collect.Sets;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.Iterator;
import java.util.List;

public class ConvertersScanner extends AbstractScanner
{
    public void scan(final Object cls) {
        final List<Object> methods = this.getMetadataAdapter().getMethods(cls);
        for (final Object method : methods) {
            final List<String> parameterNames = this.getMetadataAdapter().getParameterNames(method);
            if (parameterNames.size() == 1) {
                final String from = parameterNames.get(0);
                final String to = this.getMetadataAdapter().getReturnTypeName(method);
                if (to.equals("void") || (!this.acceptResult(from) && !this.acceptResult(to))) {
                    continue;
                }
                final String methodKey = this.getMetadataAdapter().getMethodFullKey(cls, method);
                this.getStore().put(getConverterKey(from, to), methodKey);
            }
        }
    }
    
    public static String getConverterKey(final String from, final String to) {
        return from + " to " + to;
    }
    
    public static String getConverterKey(final Class<?> from, final Class<?> to) {
        return getConverterKey(from.getName(), to.getName());
    }
    
    public Set<Method> getConverters(final Class<?> from, final Class<?> to) {
        final Set<Method> result = (Set<Method>)Sets.newHashSet();
        for (final String converter : this.getStore().get(getConverterKey(from, to))) {
            result.add(Utils.getMethodFromDescriptor(converter, this.getConfiguration().getClassLoaders()));
        }
        return result;
    }
}
