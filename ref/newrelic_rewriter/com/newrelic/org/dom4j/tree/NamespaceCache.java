// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import java.lang.reflect.Constructor;
import java.lang.ref.WeakReference;
import com.newrelic.org.dom4j.Namespace;
import java.util.Map;

public class NamespaceCache
{
    private static final String CONCURRENTREADERHASHMAP_CLASS = "EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap";
    protected static Map cache;
    protected static Map noPrefixCache;
    
    public Namespace get(final String prefix, final String uri) {
        final Map uriCache = this.getURICache(uri);
        WeakReference ref = uriCache.get(prefix);
        Namespace answer = null;
        if (ref != null) {
            answer = (Namespace)ref.get();
        }
        if (answer == null) {
            synchronized (uriCache) {
                ref = uriCache.get(prefix);
                if (ref != null) {
                    answer = (Namespace)ref.get();
                }
                if (answer == null) {
                    answer = this.createNamespace(prefix, uri);
                    uriCache.put(prefix, new WeakReference<Namespace>(answer));
                }
            }
        }
        return answer;
    }
    
    public Namespace get(final String uri) {
        WeakReference ref = NamespaceCache.noPrefixCache.get(uri);
        Namespace answer = null;
        if (ref != null) {
            answer = (Namespace)ref.get();
        }
        if (answer == null) {
            synchronized (NamespaceCache.noPrefixCache) {
                ref = NamespaceCache.noPrefixCache.get(uri);
                if (ref != null) {
                    answer = (Namespace)ref.get();
                }
                if (answer == null) {
                    answer = this.createNamespace("", uri);
                    NamespaceCache.noPrefixCache.put(uri, new WeakReference<Namespace>(answer));
                }
            }
        }
        return answer;
    }
    
    protected Map getURICache(final String uri) {
        Map answer = NamespaceCache.cache.get(uri);
        if (answer == null) {
            synchronized (NamespaceCache.cache) {
                answer = NamespaceCache.cache.get(uri);
                if (answer == null) {
                    answer = new ConcurrentReaderHashMap();
                    NamespaceCache.cache.put(uri, answer);
                }
            }
        }
        return answer;
    }
    
    protected Namespace createNamespace(final String prefix, final String uri) {
        return new Namespace(prefix, uri);
    }
    
    static {
        try {
            final Class clazz = Class.forName("java.util.concurrent.ConcurrentHashMap");
            final Constructor construct = clazz.getConstructor(Integer.TYPE, Float.TYPE, Integer.TYPE);
            NamespaceCache.cache = construct.newInstance(new Integer(11), new Float(0.75f), new Integer(1));
            NamespaceCache.noPrefixCache = construct.newInstance(new Integer(11), new Float(0.75f), new Integer(1));
        }
        catch (Throwable t1) {
            try {
                final Class clazz2 = Class.forName("EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap");
                NamespaceCache.cache = clazz2.newInstance();
                NamespaceCache.noPrefixCache = clazz2.newInstance();
            }
            catch (Throwable t2) {
                NamespaceCache.cache = new ConcurrentReaderHashMap();
                NamespaceCache.noPrefixCache = new ConcurrentReaderHashMap();
            }
        }
    }
}
