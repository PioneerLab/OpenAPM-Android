// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.util;

import com.newrelic.com.google.common.collect.ObjectArrays;
import com.newrelic.org.reflections.serializers.XmlSerializer;
import java.util.concurrent.Executors;
import com.newrelic.org.reflections.adapters.JavassistAdapter;
import java.util.Iterator;
import java.util.List;
import com.newrelic.org.reflections.Reflections;
import java.util.Collection;
import com.newrelic.com.google.common.collect.Lists;
import com.newrelic.com.google.common.collect.Sets;
import com.newrelic.org.reflections.scanners.SubTypesScanner;
import com.newrelic.org.reflections.scanners.TypeAnnotationsScanner;
import java.util.concurrent.ExecutorService;
import com.newrelic.org.reflections.serializers.Serializer;
import com.newrelic.com.google.common.base.Predicate;
import com.newrelic.org.reflections.adapters.MetadataAdapter;
import java.net.URL;
import com.newrelic.org.reflections.scanners.Scanner;
import java.util.Set;
import com.newrelic.org.reflections.Configuration;

public class ConfigurationBuilder implements Configuration
{
    private final Set<Scanner> scanners;
    private Set<URL> urls;
    private MetadataAdapter metadataAdapter;
    private Predicate<String> inputsFilter;
    private Serializer serializer;
    private ExecutorService executorService;
    private ClassLoader[] classLoaders;
    
    public ConfigurationBuilder() {
        this.scanners = Sets.newHashSet(new TypeAnnotationsScanner(), new SubTypesScanner());
        this.urls = (Set<URL>)Sets.newHashSet();
    }
    
    public static ConfigurationBuilder build(final Object... params) {
        final ConfigurationBuilder builder = new ConfigurationBuilder();
        final List<Object> parameters = Lists.newArrayList();
        for (final Object param : params) {
            if (param != null) {
                if (param.getClass().isArray()) {
                    for (final Object p : (Object[])param) {
                        if (p != null) {
                            parameters.add(p);
                        }
                    }
                }
                else if (param instanceof Iterable) {
                    for (final Object p2 : (Iterable)param) {
                        if (p2 != null) {
                            parameters.add(p2);
                        }
                    }
                }
                else {
                    parameters.add(param);
                }
            }
        }
        final List<ClassLoader> loaders = (List<ClassLoader>)Lists.newArrayList();
        for (final Object param2 : parameters) {
            if (param2 instanceof ClassLoader) {
                loaders.add((ClassLoader)param2);
            }
        }
        final ClassLoader[] classLoaders = (ClassLoader[])(loaders.isEmpty() ? null : ((ClassLoader[])loaders.toArray(new ClassLoader[loaders.size()])));
        final FilterBuilder filter = new FilterBuilder();
        final List<Scanner> scanners = (List<Scanner>)Lists.newArrayList();
        for (final Object param3 : parameters) {
            if (param3 instanceof String) {
                builder.addUrls(ClasspathHelper.forPackage((String)param3, classLoaders));
                filter.include(FilterBuilder.prefix((String)param3));
            }
            else if (param3 instanceof Class) {
                builder.addUrls(ClasspathHelper.forClass((Class<?>)param3, classLoaders));
                filter.includePackage((Class<?>)param3);
            }
            else if (param3 instanceof Scanner) {
                scanners.add((Scanner)param3);
            }
            else if (param3 instanceof URL) {
                builder.addUrls((URL)param3);
            }
            else {
                if (param3 instanceof ClassLoader) {
                    continue;
                }
                if (Reflections.log == null) {
                    continue;
                }
                Reflections.log.warn("could not use param " + param3);
            }
        }
        builder.filterInputsBy(filter);
        if (!scanners.isEmpty()) {
            builder.setScanners((Scanner[])scanners.toArray(new Scanner[scanners.size()]));
        }
        if (!loaders.isEmpty()) {
            builder.addClassLoaders(loaders);
        }
        return builder;
    }
    
    public Set<Scanner> getScanners() {
        return this.scanners;
    }
    
    public ConfigurationBuilder setScanners(final Scanner... scanners) {
        this.scanners.clear();
        return this.addScanners(scanners);
    }
    
    public ConfigurationBuilder addScanners(final Scanner... scanners) {
        this.scanners.addAll(Sets.newHashSet(scanners));
        return this;
    }
    
    public Set<URL> getUrls() {
        return this.urls;
    }
    
    public ConfigurationBuilder setUrls(final Collection<URL> urls) {
        this.urls = (Set<URL>)Sets.newHashSet((Iterable<?>)urls);
        return this;
    }
    
    public ConfigurationBuilder setUrls(final URL... urls) {
        this.urls = Sets.newHashSet(urls);
        return this;
    }
    
    public ConfigurationBuilder addUrls(final Collection<URL> urls) {
        this.urls.addAll(urls);
        return this;
    }
    
    public ConfigurationBuilder addUrls(final URL... urls) {
        this.urls.addAll(Sets.newHashSet(urls));
        return this;
    }
    
    public MetadataAdapter getMetadataAdapter() {
        return (this.metadataAdapter != null) ? this.metadataAdapter : (this.metadataAdapter = new JavassistAdapter());
    }
    
    public ConfigurationBuilder setMetadataAdapter(final MetadataAdapter metadataAdapter) {
        this.metadataAdapter = metadataAdapter;
        return this;
    }
    
    public boolean acceptsInput(final String inputFqn) {
        return this.inputsFilter == null || this.inputsFilter.apply(inputFqn);
    }
    
    public ConfigurationBuilder filterInputsBy(final Predicate<String> inputsFilter) {
        this.inputsFilter = inputsFilter;
        return this;
    }
    
    public ExecutorService getExecutorService() {
        return this.executorService;
    }
    
    public ConfigurationBuilder setExecutorService(final ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }
    
    public ConfigurationBuilder useParallelExecutor() {
        return this.useParallelExecutor(Runtime.getRuntime().availableProcessors());
    }
    
    public ConfigurationBuilder useParallelExecutor(final int availableProcessors) {
        this.setExecutorService(Executors.newFixedThreadPool(availableProcessors));
        return this;
    }
    
    public Serializer getSerializer() {
        return (this.serializer != null) ? this.serializer : (this.serializer = new XmlSerializer());
    }
    
    public ConfigurationBuilder setSerializer(final Serializer serializer) {
        this.serializer = serializer;
        return this;
    }
    
    public ClassLoader[] getClassLoaders() {
        return this.classLoaders;
    }
    
    public ConfigurationBuilder addClassLoader(final ClassLoader classLoader) {
        return this.addClassLoaders(classLoader);
    }
    
    public ConfigurationBuilder addClassLoaders(final ClassLoader... classLoaders) {
        this.classLoaders = ((this.classLoaders == null) ? classLoaders : ObjectArrays.concat(this.classLoaders, classLoaders, ClassLoader.class));
        return this;
    }
    
    public ConfigurationBuilder addClassLoaders(final Collection<ClassLoader> classLoaders) {
        return this.addClassLoaders((ClassLoader[])classLoaders.toArray(new ClassLoader[classLoaders.size()]));
    }
}
