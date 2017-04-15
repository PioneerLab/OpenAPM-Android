// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import java.util.NoSuchElementException;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.io.Serializable;
import java.util.Map;
import java.util.AbstractMap;

class ConcurrentReaderHashMap extends AbstractMap implements Map, Cloneable, Serializable
{
    protected final BarrierLock barrierLock;
    protected transient Object lastWrite;
    public static int DEFAULT_INITIAL_CAPACITY;
    private static final int MINIMUM_CAPACITY = 4;
    private static final int MAXIMUM_CAPACITY = 1073741824;
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;
    protected transient Entry[] table;
    protected transient int count;
    protected int threshold;
    protected float loadFactor;
    protected transient Set keySet;
    protected transient Set entrySet;
    protected transient Collection values;
    
    protected final void recordModification(final Object x) {
        synchronized (this.barrierLock) {
            this.lastWrite = x;
        }
    }
    
    protected final Entry[] getTableForReading() {
        synchronized (this.barrierLock) {
            return this.table;
        }
    }
    
    private int p2capacity(final int initialCapacity) {
        int result;
        if (initialCapacity > 1073741824 || initialCapacity < 0) {
            result = 1073741824;
        }
        else {
            for (result = 4; result < initialCapacity; result <<= 1) {}
        }
        return result;
    }
    
    private static int hash(final Object x) {
        final int h = x.hashCode();
        return (h << 7) - h + (h >>> 9) + (h >>> 17);
    }
    
    protected boolean eq(final Object x, final Object y) {
        return x == y || x.equals(y);
    }
    
    public ConcurrentReaderHashMap(final int initialCapacity, final float loadFactor) {
        this.barrierLock = new BarrierLock();
        this.keySet = null;
        this.entrySet = null;
        this.values = null;
        if (loadFactor <= 0.0f) {
            throw new IllegalArgumentException("Illegal Load factor: " + loadFactor);
        }
        this.loadFactor = loadFactor;
        final int cap = this.p2capacity(initialCapacity);
        this.table = new Entry[cap];
        this.threshold = (int)(cap * loadFactor);
    }
    
    public ConcurrentReaderHashMap(final int initialCapacity) {
        this(initialCapacity, 0.75f);
    }
    
    public ConcurrentReaderHashMap() {
        this(ConcurrentReaderHashMap.DEFAULT_INITIAL_CAPACITY, 0.75f);
    }
    
    public ConcurrentReaderHashMap(final Map t) {
        this(Math.max((int)(t.size() / 0.75f) + 1, 16), 0.75f);
        this.putAll(t);
    }
    
    public synchronized int size() {
        return this.count;
    }
    
    public synchronized boolean isEmpty() {
        return this.count == 0;
    }
    
    public Object get(final Object key) {
        final int hash = hash(key);
        Entry[] tab = this.table;
        int index = hash & tab.length - 1;
        Entry e;
        Entry first = e = tab[index];
        while (true) {
            if (e == null) {
                final Entry[] reread = this.getTableForReading();
                if (tab == reread && first == tab[index]) {
                    return null;
                }
                tab = reread;
                first = (e = tab[index = (hash & tab.length - 1)]);
            }
            else if (e.hash == hash && this.eq(key, e.key)) {
                final Object value = e.value;
                if (value != null) {
                    return value;
                }
                synchronized (this) {
                    tab = this.table;
                }
                first = (e = tab[index = (hash & tab.length - 1)]);
            }
            else {
                e = e.next;
            }
        }
    }
    
    public boolean containsKey(final Object key) {
        return this.get(key) != null;
    }
    
    public Object put(final Object key, final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final int hash = hash(key);
        final Entry[] tab = this.table;
        final int index = hash & tab.length - 1;
        Entry e;
        Entry first;
        for (first = (e = tab[index]); e != null && (e.hash != hash || !this.eq(key, e.key)); e = e.next) {}
        synchronized (this) {
            if (tab == this.table) {
                if (e == null) {
                    if (first == tab[index]) {
                        final Entry newEntry = new Entry(hash, key, value, first);
                        tab[index] = newEntry;
                        if (++this.count >= this.threshold) {
                            this.rehash();
                        }
                        else {
                            this.recordModification(newEntry);
                        }
                        return null;
                    }
                }
                else {
                    final Object oldValue = e.value;
                    if (first == tab[index] && oldValue != null) {
                        e.value = value;
                        return oldValue;
                    }
                }
            }
            return this.sput(key, value, hash);
        }
    }
    
    protected Object sput(final Object key, final Object value, final int hash) {
        final Entry[] tab = this.table;
        final int index = hash & tab.length - 1;
        Entry e;
        Entry first;
        for (first = (e = tab[index]); e != null; e = e.next) {
            if (e.hash == hash && this.eq(key, e.key)) {
                final Object oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }
        final Entry newEntry = new Entry(hash, key, value, first);
        tab[index] = newEntry;
        if (++this.count >= this.threshold) {
            this.rehash();
        }
        else {
            this.recordModification(newEntry);
        }
        return null;
    }
    
    protected void rehash() {
        final Entry[] oldTable = this.table;
        final int oldCapacity = oldTable.length;
        if (oldCapacity >= 1073741824) {
            this.threshold = Integer.MAX_VALUE;
            return;
        }
        final int newCapacity = oldCapacity << 1;
        final int mask = newCapacity - 1;
        this.threshold = (int)(newCapacity * this.loadFactor);
        final Entry[] newTable = new Entry[newCapacity];
        for (final Entry e : oldTable) {
            if (e != null) {
                final int idx = e.hash & mask;
                final Entry next = e.next;
                if (next == null) {
                    newTable[idx] = e;
                }
                else {
                    Entry lastRun = e;
                    int lastIdx = idx;
                    for (Entry last = next; last != null; last = last.next) {
                        final int k = last.hash & mask;
                        if (k != lastIdx) {
                            lastIdx = k;
                            lastRun = last;
                        }
                    }
                    newTable[lastIdx] = lastRun;
                    for (Entry p = e; p != lastRun; p = p.next) {
                        final int k = p.hash & mask;
                        newTable[k] = new Entry(p.hash, p.key, p.value, newTable[k]);
                    }
                }
            }
        }
        this.recordModification(this.table = newTable);
    }
    
    public Object remove(final Object key) {
        final int hash = hash(key);
        final Entry[] tab = this.table;
        final int index = hash & tab.length - 1;
        Entry e;
        Entry first;
        for (first = (e = (e = tab[index])); e != null && (e.hash != hash || !this.eq(key, e.key)); e = e.next) {}
        synchronized (this) {
            if (tab == this.table) {
                if (e == null) {
                    if (first == tab[index]) {
                        return null;
                    }
                }
                else {
                    final Object oldValue = e.value;
                    if (first == tab[index] && oldValue != null) {
                        e.value = null;
                        --this.count;
                        Entry head = e.next;
                        for (Entry p = first; p != e; p = p.next) {
                            head = new Entry(p.hash, p.key, p.value, head);
                        }
                        this.recordModification(tab[index] = head);
                        return oldValue;
                    }
                }
            }
            return this.sremove(key, hash);
        }
    }
    
    protected Object sremove(final Object key, final int hash) {
        final Entry[] tab = this.table;
        final int index = hash & tab.length - 1;
        Entry e;
        for (Entry first = e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash && this.eq(key, e.key)) {
                final Object oldValue = e.value;
                e.value = null;
                --this.count;
                Entry head = e.next;
                for (Entry p = first; p != e; p = p.next) {
                    head = new Entry(p.hash, p.key, p.value, head);
                }
                this.recordModification(tab[index] = head);
                return oldValue;
            }
        }
        return null;
    }
    
    public boolean containsValue(final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final Entry[] tab = this.getTableForReading();
        for (int i = 0; i < tab.length; ++i) {
            for (Entry e = tab[i]; e != null; e = e.next) {
                if (value.equals(e.value)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean contains(final Object value) {
        return this.containsValue(value);
    }
    
    public synchronized void putAll(final Map t) {
        final int n = t.size();
        if (n == 0) {
            return;
        }
        while (n >= this.threshold) {
            this.rehash();
        }
        for (final Map.Entry entry : t.entrySet()) {
            final Object key = entry.getKey();
            final Object value = entry.getValue();
            this.put(key, value);
        }
    }
    
    public synchronized void clear() {
        final Entry[] tab = this.table;
        for (int i = 0; i < tab.length; ++i) {
            for (Entry e = tab[i]; e != null; e = e.next) {
                e.value = null;
            }
            tab[i] = null;
        }
        this.count = 0;
        this.recordModification(tab);
    }
    
    public synchronized Object clone() {
        try {
            final ConcurrentReaderHashMap t = (ConcurrentReaderHashMap)super.clone();
            t.keySet = null;
            t.entrySet = null;
            t.values = null;
            final Entry[] tab = this.table;
            t.table = new Entry[tab.length];
            final Entry[] ttab = t.table;
            for (int i = 0; i < tab.length; ++i) {
                Entry first = null;
                for (Entry e = tab[i]; e != null; e = e.next) {
                    first = new Entry(e.hash, e.key, e.value, first);
                }
                ttab[i] = first;
            }
            return t;
        }
        catch (CloneNotSupportedException e2) {
            throw new InternalError();
        }
    }
    
    public Set keySet() {
        final Set ks = this.keySet;
        return (ks != null) ? ks : (this.keySet = new KeySet());
    }
    
    public Collection values() {
        final Collection vs = this.values;
        return (vs != null) ? vs : (this.values = new Values());
    }
    
    public Set entrySet() {
        final Set es = this.entrySet;
        return (es != null) ? es : (this.entrySet = new EntrySet());
    }
    
    protected synchronized boolean findAndRemoveEntry(final Map.Entry entry) {
        final Object key = entry.getKey();
        final Object v = this.get(key);
        if (v != null && v.equals(entry.getValue())) {
            this.remove(key);
            return true;
        }
        return false;
    }
    
    public Enumeration keys() {
        return new KeyIterator();
    }
    
    public Enumeration elements() {
        return new ValueIterator();
    }
    
    private synchronized void writeObject(final ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.table.length);
        s.writeInt(this.count);
        for (int index = this.table.length - 1; index >= 0; --index) {
            for (Entry entry = this.table[index]; entry != null; entry = entry.next) {
                s.writeObject(entry.key);
                s.writeObject(entry.value);
            }
        }
    }
    
    private synchronized void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        final int numBuckets = s.readInt();
        this.table = new Entry[numBuckets];
        for (int size = s.readInt(), i = 0; i < size; ++i) {
            final Object key = s.readObject();
            final Object value = s.readObject();
            this.put(key, value);
        }
    }
    
    public synchronized int capacity() {
        return this.table.length;
    }
    
    public float loadFactor() {
        return this.loadFactor;
    }
    
    static {
        ConcurrentReaderHashMap.DEFAULT_INITIAL_CAPACITY = 32;
    }
    
    protected static class BarrierLock implements Serializable
    {
    }
    
    private class KeySet extends AbstractSet
    {
        public Iterator iterator() {
            return new KeyIterator();
        }
        
        public int size() {
            return ConcurrentReaderHashMap.this.size();
        }
        
        public boolean contains(final Object o) {
            return ConcurrentReaderHashMap.this.containsKey(o);
        }
        
        public boolean remove(final Object o) {
            return ConcurrentReaderHashMap.this.remove(o) != null;
        }
        
        public void clear() {
            ConcurrentReaderHashMap.this.clear();
        }
    }
    
    private class Values extends AbstractCollection
    {
        public Iterator iterator() {
            return new ValueIterator();
        }
        
        public int size() {
            return ConcurrentReaderHashMap.this.size();
        }
        
        public boolean contains(final Object o) {
            return ConcurrentReaderHashMap.this.containsValue(o);
        }
        
        public void clear() {
            ConcurrentReaderHashMap.this.clear();
        }
    }
    
    private class EntrySet extends AbstractSet
    {
        public Iterator iterator() {
            return new HashIterator();
        }
        
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)o;
            final Object v = ConcurrentReaderHashMap.this.get(entry.getKey());
            return v != null && v.equals(entry.getValue());
        }
        
        public boolean remove(final Object o) {
            return o instanceof Map.Entry && ConcurrentReaderHashMap.this.findAndRemoveEntry((Map.Entry)o);
        }
        
        public int size() {
            return ConcurrentReaderHashMap.this.size();
        }
        
        public void clear() {
            ConcurrentReaderHashMap.this.clear();
        }
    }
    
    protected static class Entry implements Map.Entry
    {
        protected final int hash;
        protected final Object key;
        protected final Entry next;
        protected volatile Object value;
        
        Entry(final int hash, final Object key, final Object value, final Entry next) {
            this.hash = hash;
            this.key = key;
            this.next = next;
            this.value = value;
        }
        
        public Object getKey() {
            return this.key;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public Object setValue(final Object value) {
            if (value == null) {
                throw new NullPointerException();
            }
            final Object oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry e = (Map.Entry)o;
            return this.key.equals(e.getKey()) && this.value.equals(e.getValue());
        }
        
        public int hashCode() {
            return this.key.hashCode() ^ this.value.hashCode();
        }
        
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
    
    protected class HashIterator implements Iterator, Enumeration
    {
        protected final Entry[] tab;
        protected int index;
        protected Entry entry;
        protected Object currentKey;
        protected Object currentValue;
        protected Entry lastReturned;
        
        protected HashIterator() {
            this.entry = null;
            this.lastReturned = null;
            this.tab = ConcurrentReaderHashMap.this.getTableForReading();
            this.index = this.tab.length - 1;
        }
        
        public boolean hasMoreElements() {
            return this.hasNext();
        }
        
        public Object nextElement() {
            return this.next();
        }
        
        public boolean hasNext() {
            do {
                if (this.entry != null) {
                    final Object v = this.entry.value;
                    if (v != null) {
                        this.currentKey = this.entry.key;
                        this.currentValue = v;
                        return true;
                    }
                    this.entry = this.entry.next;
                }
                while (this.entry == null && this.index >= 0) {
                    this.entry = this.tab[this.index--];
                }
            } while (this.entry != null);
            final Object o = null;
            this.currentValue = o;
            this.currentKey = o;
            return false;
        }
        
        protected Object returnValueOfNext() {
            return this.entry;
        }
        
        public Object next() {
            if (this.currentKey == null && !this.hasNext()) {
                throw new NoSuchElementException();
            }
            final Object result = this.returnValueOfNext();
            this.lastReturned = this.entry;
            final Object o = null;
            this.currentValue = o;
            this.currentKey = o;
            this.entry = this.entry.next;
            return result;
        }
        
        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            ConcurrentReaderHashMap.this.remove(this.lastReturned.key);
            this.lastReturned = null;
        }
    }
    
    protected class KeyIterator extends HashIterator
    {
        protected Object returnValueOfNext() {
            return this.currentKey;
        }
    }
    
    protected class ValueIterator extends HashIterator
    {
        protected Object returnValueOfNext() {
            return this.currentValue;
        }
    }
}
