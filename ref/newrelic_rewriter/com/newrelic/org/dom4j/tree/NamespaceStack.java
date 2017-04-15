// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.QName;
import java.util.HashMap;
import com.newrelic.org.dom4j.Namespace;
import java.util.Map;
import java.util.ArrayList;
import com.newrelic.org.dom4j.DocumentFactory;

public class NamespaceStack
{
    private DocumentFactory documentFactory;
    private ArrayList namespaceStack;
    private ArrayList namespaceCacheList;
    private Map currentNamespaceCache;
    private Map rootNamespaceCache;
    private Namespace defaultNamespace;
    
    public NamespaceStack() {
        this.namespaceStack = new ArrayList();
        this.namespaceCacheList = new ArrayList();
        this.rootNamespaceCache = new HashMap();
        this.documentFactory = DocumentFactory.getInstance();
    }
    
    public NamespaceStack(final DocumentFactory documentFactory) {
        this.namespaceStack = new ArrayList();
        this.namespaceCacheList = new ArrayList();
        this.rootNamespaceCache = new HashMap();
        this.documentFactory = documentFactory;
    }
    
    public void push(final Namespace namespace) {
        this.namespaceStack.add(namespace);
        this.namespaceCacheList.add(null);
        this.currentNamespaceCache = null;
        final String prefix = namespace.getPrefix();
        if (prefix == null || prefix.length() == 0) {
            this.defaultNamespace = namespace;
        }
    }
    
    public Namespace pop() {
        return this.remove(this.namespaceStack.size() - 1);
    }
    
    public int size() {
        return this.namespaceStack.size();
    }
    
    public void clear() {
        this.namespaceStack.clear();
        this.namespaceCacheList.clear();
        this.rootNamespaceCache.clear();
        this.currentNamespaceCache = null;
    }
    
    public Namespace getNamespace(final int index) {
        return this.namespaceStack.get(index);
    }
    
    public Namespace getNamespaceForPrefix(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        for (int i = this.namespaceStack.size() - 1; i >= 0; --i) {
            final Namespace namespace = this.namespaceStack.get(i);
            if (prefix.equals(namespace.getPrefix())) {
                return namespace;
            }
        }
        return null;
    }
    
    public String getURI(final String prefix) {
        final Namespace namespace = this.getNamespaceForPrefix(prefix);
        return (namespace != null) ? namespace.getURI() : null;
    }
    
    public boolean contains(final Namespace namespace) {
        final String prefix = namespace.getPrefix();
        Namespace current = null;
        if (prefix == null || prefix.length() == 0) {
            current = this.getDefaultNamespace();
        }
        else {
            current = this.getNamespaceForPrefix(prefix);
        }
        return current != null && (current == namespace || namespace.getURI().equals(current.getURI()));
    }
    
    public QName getQName(String namespaceURI, String localName, String qualifiedName) {
        if (localName == null) {
            localName = qualifiedName;
        }
        else if (qualifiedName == null) {
            qualifiedName = localName;
        }
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        String prefix = "";
        final int index = qualifiedName.indexOf(":");
        if (index > 0) {
            prefix = qualifiedName.substring(0, index);
            if (localName.trim().length() == 0) {
                localName = qualifiedName.substring(index + 1);
            }
        }
        else if (localName.trim().length() == 0) {
            localName = qualifiedName;
        }
        final Namespace namespace = this.createNamespace(prefix, namespaceURI);
        return this.pushQName(localName, qualifiedName, namespace, prefix);
    }
    
    public QName getAttributeQName(String namespaceURI, String localName, String qualifiedName) {
        if (qualifiedName == null) {
            qualifiedName = localName;
        }
        final Map map = this.getNamespaceCache();
        QName answer = map.get(qualifiedName);
        if (answer != null) {
            return answer;
        }
        if (localName == null) {
            localName = qualifiedName;
        }
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        Namespace namespace = null;
        String prefix = "";
        final int index = qualifiedName.indexOf(":");
        if (index > 0) {
            prefix = qualifiedName.substring(0, index);
            namespace = this.createNamespace(prefix, namespaceURI);
            if (localName.trim().length() == 0) {
                localName = qualifiedName.substring(index + 1);
            }
        }
        else {
            namespace = Namespace.NO_NAMESPACE;
            if (localName.trim().length() == 0) {
                localName = qualifiedName;
            }
        }
        answer = this.pushQName(localName, qualifiedName, namespace, prefix);
        map.put(qualifiedName, answer);
        return answer;
    }
    
    public void push(final String prefix, String uri) {
        if (uri == null) {
            uri = "";
        }
        final Namespace namespace = this.createNamespace(prefix, uri);
        this.push(namespace);
    }
    
    public Namespace addNamespace(final String prefix, final String uri) {
        final Namespace namespace = this.createNamespace(prefix, uri);
        this.push(namespace);
        return namespace;
    }
    
    public Namespace pop(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        Namespace namespace = null;
        for (int i = this.namespaceStack.size() - 1; i >= 0; --i) {
            final Namespace ns = this.namespaceStack.get(i);
            if (prefix.equals(ns.getPrefix())) {
                this.remove(i);
                namespace = ns;
                break;
            }
        }
        if (namespace == null) {
            System.out.println("Warning: missing namespace prefix ignored: " + prefix);
        }
        return namespace;
    }
    
    public String toString() {
        return super.toString() + " Stack: " + this.namespaceStack.toString();
    }
    
    public DocumentFactory getDocumentFactory() {
        return this.documentFactory;
    }
    
    public void setDocumentFactory(final DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }
    
    public Namespace getDefaultNamespace() {
        if (this.defaultNamespace == null) {
            this.defaultNamespace = this.findDefaultNamespace();
        }
        return this.defaultNamespace;
    }
    
    protected QName pushQName(final String localName, final String qualifiedName, final Namespace namespace, final String prefix) {
        if (prefix == null || prefix.length() == 0) {
            this.defaultNamespace = null;
        }
        return this.createQName(localName, qualifiedName, namespace);
    }
    
    protected QName createQName(final String localName, final String qualifiedName, final Namespace namespace) {
        return this.documentFactory.createQName(localName, namespace);
    }
    
    protected Namespace createNamespace(final String prefix, final String namespaceURI) {
        return this.documentFactory.createNamespace(prefix, namespaceURI);
    }
    
    protected Namespace findDefaultNamespace() {
        for (int i = this.namespaceStack.size() - 1; i >= 0; --i) {
            final Namespace namespace = this.namespaceStack.get(i);
            if (namespace != null) {
                final String prefix = namespace.getPrefix();
                if (prefix == null || namespace.getPrefix().length() == 0) {
                    return namespace;
                }
            }
        }
        return null;
    }
    
    protected Namespace remove(final int index) {
        final Namespace namespace = this.namespaceStack.remove(index);
        this.namespaceCacheList.remove(index);
        this.defaultNamespace = null;
        this.currentNamespaceCache = null;
        return namespace;
    }
    
    protected Map getNamespaceCache() {
        if (this.currentNamespaceCache == null) {
            final int index = this.namespaceStack.size() - 1;
            if (index < 0) {
                this.currentNamespaceCache = this.rootNamespaceCache;
            }
            else {
                this.currentNamespaceCache = this.namespaceCacheList.get(index);
                if (this.currentNamespaceCache == null) {
                    this.currentNamespaceCache = new HashMap();
                    this.namespaceCacheList.set(index, this.currentNamespaceCache);
                }
            }
        }
        return this.currentNamespaceCache;
    }
}
