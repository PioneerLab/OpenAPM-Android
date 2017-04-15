// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.IllegalAddException;
import com.newrelic.org.dom4j.Node;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.AbstractList;

public class ContentListFacade extends AbstractList
{
    private List branchContent;
    private AbstractBranch branch;
    
    public ContentListFacade(final AbstractBranch branch, final List branchContent) {
        this.branch = branch;
        this.branchContent = branchContent;
    }
    
    public boolean add(final Object object) {
        this.branch.childAdded(this.asNode(object));
        return this.branchContent.add(object);
    }
    
    public void add(final int index, final Object object) {
        this.branch.childAdded(this.asNode(object));
        this.branchContent.add(index, object);
    }
    
    public Object set(final int index, final Object object) {
        this.branch.childAdded(this.asNode(object));
        return this.branchContent.set(index, object);
    }
    
    public boolean remove(final Object object) {
        this.branch.childRemoved(this.asNode(object));
        return this.branchContent.remove(object);
    }
    
    public Object remove(final int index) {
        final Object object = this.branchContent.remove(index);
        if (object != null) {
            this.branch.childRemoved(this.asNode(object));
        }
        return object;
    }
    
    public boolean addAll(final Collection collection) {
        int count = this.branchContent.size();
        final Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            this.add(iter.next());
            ++count;
        }
        return count == this.branchContent.size();
    }
    
    public boolean addAll(int index, final Collection collection) {
        int count = this.branchContent.size();
        final Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            this.add(index++, iter.next());
            --count;
        }
        return count == this.branchContent.size();
    }
    
    public void clear() {
        for (final Object object : this) {
            this.branch.childRemoved(this.asNode(object));
        }
        this.branchContent.clear();
    }
    
    public boolean removeAll(final Collection c) {
        for (final Object object : c) {
            this.branch.childRemoved(this.asNode(object));
        }
        return this.branchContent.removeAll(c);
    }
    
    public int size() {
        return this.branchContent.size();
    }
    
    public boolean isEmpty() {
        return this.branchContent.isEmpty();
    }
    
    public boolean contains(final Object o) {
        return this.branchContent.contains(o);
    }
    
    public Object[] toArray() {
        return this.branchContent.toArray();
    }
    
    public Object[] toArray(final Object[] a) {
        return this.branchContent.toArray(a);
    }
    
    public boolean containsAll(final Collection c) {
        return this.branchContent.containsAll(c);
    }
    
    public Object get(final int index) {
        return this.branchContent.get(index);
    }
    
    public int indexOf(final Object o) {
        return this.branchContent.indexOf(o);
    }
    
    public int lastIndexOf(final Object o) {
        return this.branchContent.lastIndexOf(o);
    }
    
    protected Node asNode(final Object object) {
        if (object instanceof Node) {
            return (Node)object;
        }
        throw new IllegalAddException("This list must contain instances of Node. Invalid type: " + object);
    }
    
    protected List getBackingList() {
        return this.branchContent;
    }
}
