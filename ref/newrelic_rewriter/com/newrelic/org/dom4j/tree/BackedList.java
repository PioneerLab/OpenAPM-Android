// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.IllegalAddException;
import com.newrelic.org.dom4j.Node;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class BackedList extends ArrayList
{
    private List branchContent;
    private AbstractBranch branch;
    
    public BackedList(final AbstractBranch branch, final List branchContent) {
        this(branch, branchContent, branchContent.size());
    }
    
    public BackedList(final AbstractBranch branch, final List branchContent, final int capacity) {
        super(capacity);
        this.branch = branch;
        this.branchContent = branchContent;
    }
    
    public BackedList(final AbstractBranch branch, final List branchContent, final List initialContent) {
        super(initialContent);
        this.branch = branch;
        this.branchContent = branchContent;
    }
    
    public boolean add(final Object object) {
        this.branch.addNode(this.asNode(object));
        return super.add(object);
    }
    
    public void add(final int index, final Object object) {
        final int size = this.size();
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index value: " + index + " is less than zero");
        }
        if (index > size) {
            throw new IndexOutOfBoundsException("Index value: " + index + " cannot be greater than " + "the size: " + size);
        }
        int realIndex;
        if (size == 0) {
            realIndex = this.branchContent.size();
        }
        else if (index < size) {
            realIndex = this.branchContent.indexOf(this.get(index));
        }
        else {
            realIndex = this.branchContent.indexOf(this.get(size - 1)) + 1;
        }
        this.branch.addNode(realIndex, this.asNode(object));
        super.add(index, object);
    }
    
    public Object set(final int index, final Object object) {
        int realIndex = this.branchContent.indexOf(this.get(index));
        if (realIndex < 0) {
            realIndex = ((index == 0) ? 0 : Integer.MAX_VALUE);
        }
        if (realIndex < this.branchContent.size()) {
            this.branch.removeNode(this.asNode(this.get(index)));
            this.branch.addNode(realIndex, this.asNode(object));
        }
        else {
            this.branch.removeNode(this.asNode(this.get(index)));
            this.branch.addNode(this.asNode(object));
        }
        this.branch.childAdded(this.asNode(object));
        return super.set(index, object);
    }
    
    public boolean remove(final Object object) {
        this.branch.removeNode(this.asNode(object));
        return super.remove(object);
    }
    
    public Object remove(final int index) {
        final Object object = super.remove(index);
        if (object != null) {
            this.branch.removeNode(this.asNode(object));
        }
        return object;
    }
    
    public boolean addAll(final Collection collection) {
        this.ensureCapacity(this.size() + collection.size());
        int count = this.size();
        final Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            this.add(iter.next());
            --count;
        }
        return count != 0;
    }
    
    public boolean addAll(int index, final Collection collection) {
        this.ensureCapacity(this.size() + collection.size());
        int count = this.size();
        final Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            this.add(index++, iter.next());
            --count;
        }
        return count != 0;
    }
    
    public void clear() {
        for (final Object object : this) {
            this.branchContent.remove(object);
            this.branch.childRemoved(this.asNode(object));
        }
        super.clear();
    }
    
    public void addLocal(final Object object) {
        super.add(object);
    }
    
    protected Node asNode(final Object object) {
        if (object instanceof Node) {
            return (Node)object;
        }
        throw new IllegalAddException("This list must contain instances of Node. Invalid type: " + object);
    }
}
