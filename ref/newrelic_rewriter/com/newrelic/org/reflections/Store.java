// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections;

import java.lang.annotation.Inherited;
import java.util.regex.Pattern;
import com.newrelic.com.google.common.collect.Collections2;
import com.newrelic.com.google.common.base.Predicate;
import com.newrelic.org.reflections.scanners.ResourcesScanner;
import com.newrelic.org.reflections.scanners.FieldAnnotationsScanner;
import com.newrelic.org.reflections.scanners.MethodAnnotationsScanner;
import com.newrelic.org.reflections.scanners.TypeAnnotationsScanner;
import com.newrelic.org.reflections.scanners.SubTypesScanner;
import java.util.HashSet;
import java.util.Iterator;
import com.newrelic.com.google.common.collect.Sets;
import javax.annotation.Nullable;
import com.newrelic.org.reflections.scanners.Scanner;
import com.newrelic.com.google.common.collect.Multimaps;
import java.util.Collection;
import com.newrelic.com.google.common.collect.SetMultimap;
import java.util.HashMap;
import java.util.Set;
import com.newrelic.com.google.common.base.Supplier;
import com.newrelic.com.google.common.collect.Multimap;
import java.util.Map;

public class Store
{
    private final Map<String, Multimap<String, String>> storeMap;
    private final transient boolean concurrent;
    private static final transient Supplier<Set<String>> setSupplier;
    
    protected Store() {
        this(false);
    }
    
    protected Store(final boolean concurrent) {
        this.concurrent = concurrent;
        this.storeMap = new HashMap<String, Multimap<String, String>>();
    }
    
    private SetMultimap<String, String> createMultimap() {
        return this.concurrent ? Multimaps.synchronizedSetMultimap((SetMultimap<String, String>)Multimaps.newSetMultimap((Map<K, Collection<V>>)new HashMap<Object, Collection<V>>(), (Supplier<? extends Set<V>>)Store.setSupplier)) : Multimaps.newSetMultimap(new HashMap<String, Collection<String>>(), Store.setSupplier);
    }
    
    public Multimap<String, String> getOrCreate(String indexName) {
        if (indexName.contains(".")) {
            indexName = indexName.substring(indexName.lastIndexOf(".") + 1);
        }
        Multimap<String, String> mmap = this.storeMap.get(indexName);
        if (mmap == null) {
            this.storeMap.put(indexName, mmap = this.createMultimap());
        }
        return mmap;
    }
    
    @Nullable
    public Multimap<String, String> get(final Class<? extends Scanner> scannerClass) {
        return this.storeMap.get(scannerClass.getSimpleName());
    }
    
    public Set<String> get(final Class<? extends Scanner> scannerClass, final String... keys) {
        final Set<String> result = (Set<String>)Sets.newHashSet();
        final Multimap<String, String> map = this.get(scannerClass);
        if (map != null) {
            for (final String key : keys) {
                result.addAll(map.get(key));
            }
        }
        return result;
    }
    
    public Set<String> get(final Class<? extends Scanner> scannerClass, final Iterable<String> keys) {
        final Set<String> result = (Set<String>)Sets.newHashSet();
        final Multimap<String, String> map = this.get(scannerClass);
        if (map != null) {
            for (final String key : keys) {
                result.addAll(map.get(key));
            }
        }
        return result;
    }
    
    public Map<String, Multimap<String, String>> getStoreMap() {
        return this.storeMap;
    }
    
    void merge(final Store outer) {
        if (outer != null) {
            for (final String indexName : outer.storeMap.keySet()) {
                this.getOrCreate(indexName).putAll(outer.storeMap.get(indexName));
            }
        }
    }
    
    public Integer getKeysCount() {
        Integer keys = 0;
        for (final Multimap<String, String> multimap : this.storeMap.values()) {
            keys += multimap.keySet().size();
        }
        return keys;
    }
    
    public Integer getValuesCount() {
        Integer values = 0;
        for (final Multimap<String, String> multimap : this.storeMap.values()) {
            values += multimap.size();
        }
        return values;
    }
    
    public Set<String> getSubTypesOf(final String type) {
        final Set<String> result = new HashSet<String>();
        final Set<String> subTypes = this.get(SubTypesScanner.class, type);
        result.addAll(subTypes);
        for (final String subType : subTypes) {
            result.addAll(this.getSubTypesOf(subType));
        }
        return result;
    }
    
    public Set<String> getTypesAnnotatedWithDirectly(final String annotation) {
        return this.get(TypeAnnotationsScanner.class, annotation);
    }
    
    public Set<String> getTypesAnnotatedWith(final String annotation) {
        return this.getTypesAnnotatedWith(annotation, true);
    }
    
    public Set<String> getTypesAnnotatedWith(final String annotation, final boolean honorInherited) {
        final Set<String> result = new HashSet<String>();
        if (this.isAnnotation(annotation)) {
            final Set<String> types = this.getTypesAnnotatedWithDirectly(annotation);
            final Set<String> inherited = this.getInheritedSubTypes(types, annotation, honorInherited);
            result.addAll(inherited);
        }
        return result;
    }
    
    public Set<String> getInheritedSubTypes(final Iterable<String> types, final String annotation, final boolean honorInherited) {
        final Set<String> result = (Set<String>)Sets.newHashSet((Iterable<?>)types);
        if (honorInherited && this.isInheritedAnnotation(annotation)) {
            for (final String type : types) {
                if (this.isClass(type)) {
                    result.addAll(this.getSubTypesOf(type));
                }
            }
        }
        else if (!honorInherited) {
            for (final String type : types) {
                if (this.isAnnotation(type)) {
                    result.addAll(this.getTypesAnnotatedWith(type, false));
                }
                else {
                    result.addAll(this.getSubTypesOf(type));
                }
            }
        }
        return result;
    }
    
    public Set<String> getMethodsAnnotatedWith(final String annotation) {
        return this.get(MethodAnnotationsScanner.class, annotation);
    }
    
    public Set<String> getFieldsAnnotatedWith(final String annotation) {
        return this.get(FieldAnnotationsScanner.class, annotation);
    }
    
    public Set<String> getResources(final String key) {
        return this.get(ResourcesScanner.class, key);
    }
    
    public Set<String> getResources(final Predicate<String> namePredicate) {
        final Multimap<String, String> mmap = this.get(ResourcesScanner.class);
        if (mmap != null) {
            return this.get(ResourcesScanner.class, Collections2.filter(mmap.keySet(), namePredicate));
        }
        return (Set<String>)Sets.newHashSet();
    }
    
    public Set<String> getResources(final Pattern pattern) {
        return this.getResources(new Predicate<String>() {
            public boolean apply(final String input) {
                return pattern.matcher(input).matches();
            }
        });
    }
    
    public boolean isClass(final String type) {
        return !this.isInterface(type);
    }
    
    public boolean isInterface(final String aClass) {
        return ReflectionUtils.forName(aClass, new ClassLoader[0]).isInterface();
    }
    
    public boolean isAnnotation(final String typeAnnotatedWith) {
        final Multimap<String, String> mmap = this.get(TypeAnnotationsScanner.class);
        return mmap != null && mmap.keySet().contains(typeAnnotatedWith);
    }
    
    public boolean isInheritedAnnotation(final String typeAnnotatedWith) {
        final Multimap<String, String> mmap = this.get(TypeAnnotationsScanner.class);
        return mmap != null && mmap.get(Inherited.class.getName()).contains(typeAnnotatedWith);
    }
    
    static {
        setSupplier = new Supplier<Set<String>>() {
            public Set<String> get() {
                return (Set<String>)Sets.newHashSet();
            }
        };
    }
}
