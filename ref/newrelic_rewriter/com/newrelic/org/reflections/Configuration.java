// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections;

import com.newrelic.org.reflections.serializers.Serializer;
import java.util.concurrent.ExecutorService;
import com.newrelic.org.reflections.adapters.MetadataAdapter;
import java.net.URL;
import com.newrelic.org.reflections.scanners.Scanner;
import java.util.Set;

public interface Configuration
{
    Set<Scanner> getScanners();
    
    Set<URL> getUrls();
    
    MetadataAdapter getMetadataAdapter();
    
    boolean acceptsInput(final String p0);
    
    ExecutorService getExecutorService();
    
    Serializer getSerializer();
    
    ClassLoader[] getClassLoaders();
}
