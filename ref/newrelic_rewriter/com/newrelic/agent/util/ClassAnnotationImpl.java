// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.util;

public class ClassAnnotationImpl extends AnnotationImpl implements ClassAnnotation
{
    private final String className;
    
    public ClassAnnotationImpl(final String className, final String name) {
        super(name);
        this.className = className;
    }
    
    @Override
    public String getClassName() {
        return this.className;
    }
}
