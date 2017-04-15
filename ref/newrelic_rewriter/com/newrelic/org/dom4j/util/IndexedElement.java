// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.util;

import java.util.ArrayList;
import java.util.HashMap;
import com.newrelic.org.dom4j.Node;
import java.util.Iterator;
import com.newrelic.org.dom4j.tree.BackedList;
import java.util.List;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Attribute;
import com.newrelic.org.dom4j.QName;
import java.util.Map;
import com.newrelic.org.dom4j.tree.DefaultElement;

public class IndexedElement extends DefaultElement
{
    private Map elementIndex;
    private Map attributeIndex;
    
    public IndexedElement(final String name) {
        super(name);
    }
    
    public IndexedElement(final QName qname) {
        super(qname);
    }
    
    public IndexedElement(final QName qname, final int attributeCount) {
        super(qname, attributeCount);
    }
    
    public Attribute attribute(final String name) {
        return this.attributeIndex().get(name);
    }
    
    public Attribute attribute(final QName qName) {
        return this.attributeIndex().get(qName);
    }
    
    public Element element(final String name) {
        return this.asElement(this.elementIndex().get(name));
    }
    
    public Element element(final QName qName) {
        return this.asElement(this.elementIndex().get(qName));
    }
    
    public List elements(final String name) {
        return this.asElementList(this.elementIndex().get(name));
    }
    
    public List elements(final QName qName) {
        return this.asElementList(this.elementIndex().get(qName));
    }
    
    protected Element asElement(final Object object) {
        if (object instanceof Element) {
            return (Element)object;
        }
        if (object != null) {
            final List list = (List)object;
            if (list.size() >= 1) {
                return list.get(0);
            }
        }
        return null;
    }
    
    protected List asElementList(final Object object) {
        if (object instanceof Element) {
            return this.createSingleResultList(object);
        }
        if (object != null) {
            final List list = (List)object;
            final BackedList answer = this.createResultList();
            for (int i = 0, size = list.size(); i < size; ++i) {
                answer.addLocal(list.get(i));
            }
            return answer;
        }
        return this.createEmptyList();
    }
    
    protected Iterator asElementIterator(final Object object) {
        return this.asElementList(object).iterator();
    }
    
    protected void addNode(final Node node) {
        super.addNode(node);
        if (this.elementIndex != null && node instanceof Element) {
            this.addToElementIndex((Element)node);
        }
        else if (this.attributeIndex != null && node instanceof Attribute) {
            this.addToAttributeIndex((Attribute)node);
        }
    }
    
    protected boolean removeNode(final Node node) {
        if (super.removeNode(node)) {
            if (this.elementIndex != null && node instanceof Element) {
                this.removeFromElementIndex((Element)node);
            }
            else if (this.attributeIndex != null && node instanceof Attribute) {
                this.removeFromAttributeIndex((Attribute)node);
            }
            return true;
        }
        return false;
    }
    
    protected Map attributeIndex() {
        if (this.attributeIndex == null) {
            this.attributeIndex = this.createAttributeIndex();
            final Iterator iter = this.attributeIterator();
            while (iter.hasNext()) {
                this.addToAttributeIndex(iter.next());
            }
        }
        return this.attributeIndex;
    }
    
    protected Map elementIndex() {
        if (this.elementIndex == null) {
            this.elementIndex = this.createElementIndex();
            final Iterator iter = this.elementIterator();
            while (iter.hasNext()) {
                this.addToElementIndex(iter.next());
            }
        }
        return this.elementIndex;
    }
    
    protected Map createAttributeIndex() {
        final Map answer = this.createIndex();
        return answer;
    }
    
    protected Map createElementIndex() {
        final Map answer = this.createIndex();
        return answer;
    }
    
    protected void addToElementIndex(final Element element) {
        final QName qName = element.getQName();
        final String name = qName.getName();
        this.addToElementIndex(qName, element);
        this.addToElementIndex(name, element);
    }
    
    protected void addToElementIndex(final Object key, final Element value) {
        final Object oldValue = this.elementIndex.get(key);
        if (oldValue == null) {
            this.elementIndex.put(key, value);
        }
        else if (oldValue instanceof List) {
            final List list = (List)oldValue;
            list.add(value);
        }
        else {
            final List list = this.createList();
            list.add(oldValue);
            list.add(value);
            this.elementIndex.put(key, list);
        }
    }
    
    protected void removeFromElementIndex(final Element element) {
        final QName qName = element.getQName();
        final String name = qName.getName();
        this.removeFromElementIndex(qName, element);
        this.removeFromElementIndex(name, element);
    }
    
    protected void removeFromElementIndex(final Object key, final Element value) {
        final Object oldValue = this.elementIndex.get(key);
        if (oldValue instanceof List) {
            final List list = (List)oldValue;
            list.remove(value);
        }
        else {
            this.elementIndex.remove(key);
        }
    }
    
    protected void addToAttributeIndex(final Attribute attribute) {
        final QName qName = attribute.getQName();
        final String name = qName.getName();
        this.addToAttributeIndex(qName, attribute);
        this.addToAttributeIndex(name, attribute);
    }
    
    protected void addToAttributeIndex(final Object key, final Attribute value) {
        final Object oldValue = this.attributeIndex.get(key);
        if (oldValue != null) {
            this.attributeIndex.put(key, value);
        }
    }
    
    protected void removeFromAttributeIndex(final Attribute attribute) {
        final QName qName = attribute.getQName();
        final String name = qName.getName();
        this.removeFromAttributeIndex(qName, attribute);
        this.removeFromAttributeIndex(name, attribute);
    }
    
    protected void removeFromAttributeIndex(final Object key, final Attribute value) {
        final Object oldValue = this.attributeIndex.get(key);
        if (oldValue != null && oldValue.equals(value)) {
            this.attributeIndex.remove(key);
        }
    }
    
    protected Map createIndex() {
        return new HashMap();
    }
    
    protected List createList() {
        return new ArrayList();
    }
}
