// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.scanners;

import com.newrelic.org.reflections.adapters.MetadataAdapter;
import com.newrelic.org.reflections.ReflectionsException;
import com.newrelic.org.reflections.vfs.Vfs;
import com.newrelic.com.google.common.base.Predicates;
import com.newrelic.com.google.common.base.Predicate;
import com.newrelic.com.google.common.collect.Multimap;
import com.newrelic.org.reflections.Configuration;

public abstract class AbstractScanner implements Scanner
{
    private Configuration configuration;
    private Multimap<String, String> store;
    private Predicate<String> resultFilter;
    
    public AbstractScanner() {
        this.resultFilter = Predicates.alwaysTrue();
    }
    
    public boolean acceptsInput(final String file) {
        return file.endsWith(".class");
    }
    
    public void scan(final Vfs.File file) {
        try {
            final Object classObject = this.getMetadataAdapter().getOfCreateClassObject(file);
            this.scan(classObject);
        }
        catch (Exception e) {
            throw new ReflectionsException("could not create class file from " + file.getName(), e);
        }
    }
    
    public abstract void scan(final Object p0);
    
    public Configuration getConfiguration() {
        return this.configuration;
    }
    
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }
    
    public Multimap<String, String> getStore() {
        return this.store;
    }
    
    public void setStore(final Multimap<String, String> store) {
        this.store = store;
    }
    
    public Predicate<String> getResultFilter() {
        return this.resultFilter;
    }
    
    public void setResultFilter(final Predicate<String> resultFilter) {
        this.resultFilter = resultFilter;
    }
    
    public Scanner filterResultsBy(final Predicate<String> filter) {
        this.setResultFilter(filter);
        return this;
    }
    
    public boolean acceptResult(final String fqn) {
        return fqn != null && this.resultFilter.apply(fqn);
    }
    
    protected MetadataAdapter getMetadataAdapter() {
        return this.configuration.getMetadataAdapter();
    }
    
    public boolean equals(final Object o) {
        return this == o || (o != null && this.getClass() == o.getClass());
    }
    
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
