// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import java.util.HashMap;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.QName;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.WeakHashMap;
import com.newrelic.org.dom4j.DocumentFactory;
import java.util.Map;

public class QNameCache
{
    protected Map noNamespaceCache;
    protected Map namespaceCache;
    private DocumentFactory documentFactory;
    
    public QNameCache() {
        this.noNamespaceCache = Collections.synchronizedMap(new WeakHashMap<Object, Object>());
        this.namespaceCache = Collections.synchronizedMap(new WeakHashMap<Object, Object>());
    }
    
    public QNameCache(final DocumentFactory documentFactory) {
        this.noNamespaceCache = Collections.synchronizedMap(new WeakHashMap<Object, Object>());
        this.namespaceCache = Collections.synchronizedMap(new WeakHashMap<Object, Object>());
        this.documentFactory = documentFactory;
    }
    
    public List getQNames() {
        final List answer = new ArrayList();
        answer.addAll(this.noNamespaceCache.values());
        for (final Map map : this.namespaceCache.values()) {
            answer.addAll(map.values());
        }
        return answer;
    }
    
    public QName get(String name) {
        QName answer = null;
        if (name != null) {
            answer = this.noNamespaceCache.get(name);
        }
        else {
            name = "";
        }
        if (answer == null) {
            answer = this.createQName(name);
            answer.setDocumentFactory(this.documentFactory);
            this.noNamespaceCache.put(name, answer);
        }
        return answer;
    }
    
    public QName get(String name, final Namespace namespace) {
        final Map cache = this.getNamespaceCache(namespace);
        QName answer = null;
        if (name != null) {
            answer = cache.get(name);
        }
        else {
            name = "";
        }
        if (answer == null) {
            answer = this.createQName(name, namespace);
            answer.setDocumentFactory(this.documentFactory);
            cache.put(name, answer);
        }
        return answer;
    }
    
    public QName get(String localName, final Namespace namespace, final String qName) {
        final Map cache = this.getNamespaceCache(namespace);
        QName answer = null;
        if (localName != null) {
            answer = cache.get(localName);
        }
        else {
            localName = "";
        }
        if (answer == null) {
            answer = this.createQName(localName, namespace, qName);
            answer.setDocumentFactory(this.documentFactory);
            cache.put(localName, answer);
        }
        return answer;
    }
    
    public QName get(final String qualifiedName, final String uri) {
        final int index = qualifiedName.indexOf(58);
        if (index < 0) {
            return this.get(qualifiedName, Namespace.get(uri));
        }
        final String name = qualifiedName.substring(index + 1);
        final String prefix = qualifiedName.substring(0, index);
        return this.get(name, Namespace.get(prefix, uri));
    }
    
    public QName intern(final QName qname) {
        return this.get(qname.getName(), qname.getNamespace(), qname.getQualifiedName());
    }
    
    protected Map getNamespaceCache(final Namespace namespace) {
        if (namespace == Namespace.NO_NAMESPACE) {
            return this.noNamespaceCache;
        }
        Map answer = null;
        if (namespace != null) {
            answer = this.namespaceCache.get(namespace);
        }
        if (answer == null) {
            answer = this.createMap();
            this.namespaceCache.put(namespace, answer);
        }
        return answer;
    }
    
    protected Map createMap() {
        return Collections.synchronizedMap(new HashMap<Object, Object>());
    }
    
    protected QName createQName(final String name) {
        return new QName(name);
    }
    
    protected QName createQName(final String name, final Namespace namespace) {
        return new QName(name, namespace);
    }
    
    protected QName createQName(final String name, final Namespace namespace, final String qualifiedName) {
        return new QName(name, namespace, qualifiedName);
    }
}
