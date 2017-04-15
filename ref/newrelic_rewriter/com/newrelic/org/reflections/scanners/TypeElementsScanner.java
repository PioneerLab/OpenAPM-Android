// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.scanners;

import java.util.Iterator;

public class TypeElementsScanner extends AbstractScanner
{
    private boolean includeFields;
    private boolean includeMethods;
    private boolean publicOnly;
    
    public TypeElementsScanner() {
        this.includeFields = true;
        this.includeMethods = true;
        this.publicOnly = true;
    }
    
    public void scan(final Object cls) {
        if (TypesScanner.isJavaCodeSerializer(this.getMetadataAdapter().getInterfacesNames(cls))) {
            return;
        }
        final String className = this.getMetadataAdapter().getClassName(cls);
        if (this.includeFields) {
            for (final Object field : this.getMetadataAdapter().getFields(cls)) {
                final String fieldName = this.getMetadataAdapter().getFieldName(field);
                this.getStore().put(className, fieldName);
            }
        }
        if (this.includeMethods) {
            for (final Object method : this.getMetadataAdapter().getMethods(cls)) {
                if (!this.publicOnly || this.getMetadataAdapter().isPublic(method)) {
                    this.getStore().put(className, this.getMetadataAdapter().getMethodKey(cls, method));
                }
            }
        }
    }
    
    public TypeElementsScanner includeFields() {
        return this.includeFields(true);
    }
    
    public TypeElementsScanner includeFields(final boolean include) {
        this.includeFields = include;
        return this;
    }
    
    public TypeElementsScanner includeMethods() {
        return this.includeMethods(true);
    }
    
    public TypeElementsScanner includeMethods(final boolean include) {
        this.includeMethods = include;
        return this;
    }
    
    public TypeElementsScanner publicOnly(final boolean only) {
        this.publicOnly = only;
        return this;
    }
    
    public TypeElementsScanner publicOnly() {
        return this.publicOnly(true);
    }
}
