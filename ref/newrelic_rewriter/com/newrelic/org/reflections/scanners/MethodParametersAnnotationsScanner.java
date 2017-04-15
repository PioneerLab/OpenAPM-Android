// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.scanners;

import java.util.Iterator;
import java.util.List;

public class MethodParametersAnnotationsScanner extends AbstractScanner
{
    public void scan(final Object cls) {
        final String className = this.getMetadataAdapter().getClassName(cls);
        final List<Object> methods = this.getMetadataAdapter().getMethods(cls);
        for (final Object method : methods) {
            final List<String> parameters = this.getMetadataAdapter().getParameterNames(method);
            for (int parameterIndex = 0; parameterIndex < parameters.size(); ++parameterIndex) {
                final List<String> parameterAnnotations = this.getMetadataAdapter().getParameterAnnotationNames(method, parameterIndex);
                for (final String parameterAnnotation : parameterAnnotations) {
                    if (this.acceptResult(parameterAnnotation)) {
                        this.getStore().put(parameterAnnotation, String.format("%s.%s:%s %s", className, method, parameters.get(parameterIndex), parameterAnnotation));
                    }
                }
            }
        }
    }
}
