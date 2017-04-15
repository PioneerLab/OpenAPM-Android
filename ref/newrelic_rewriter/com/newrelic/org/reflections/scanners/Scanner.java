// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.scanners;

import com.newrelic.org.reflections.vfs.Vfs;
import com.newrelic.com.google.common.base.Predicate;
import com.newrelic.com.google.common.collect.Multimap;
import com.newrelic.org.reflections.Configuration;

public interface Scanner
{
    void setConfiguration(final Configuration p0);
    
    Multimap<String, String> getStore();
    
    void setStore(final Multimap<String, String> p0);
    
    Scanner filterResultsBy(final Predicate<String> p0);
    
    boolean acceptsInput(final String p0);
    
    void scan(final Vfs.File p0);
    
    boolean acceptResult(final String p0);
}
