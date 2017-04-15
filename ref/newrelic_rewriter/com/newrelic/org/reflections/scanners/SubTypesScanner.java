// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.scanners;

import java.util.Iterator;
import com.newrelic.com.google.common.base.Predicate;
import com.newrelic.org.reflections.util.FilterBuilder;

public class SubTypesScanner extends AbstractScanner
{
    public SubTypesScanner() {
        this(true);
    }
    
    public SubTypesScanner(final boolean excludeObjectClass) {
        if (excludeObjectClass) {
            this.filterResultsBy(new FilterBuilder().exclude(Object.class.getName()));
        }
    }
    
    public void scan(final Object cls) {
        final String className = this.getMetadataAdapter().getClassName(cls);
        final String superclass = this.getMetadataAdapter().getSuperclassName(cls);
        if (this.acceptResult(superclass)) {
            this.getStore().put(superclass, className);
        }
        for (final String anInterface : this.getMetadataAdapter().getInterfacesNames(cls)) {
            if (this.acceptResult(anInterface)) {
                this.getStore().put(anInterface, className);
            }
        }
    }
}
