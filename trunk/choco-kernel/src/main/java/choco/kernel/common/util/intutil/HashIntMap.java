/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
/*
 *  @(#)HashIntMap.java	1.73 07/03/13
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package choco.kernel.common.util.intutil;

import choco.kernel.common.util.IntIterator;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 12, 2008
 * Time: 1:12:37 PM
 */
@Deprecated // see trove4j librairy
public class HashIntMap<V>
    extends AbstractIntMap<V>
    implements IntMap<V>, Cloneable, Serializable
{

    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    transient Entry[] table;

    /**
     * The number of key-value mappings contained in this map.
     */
    transient int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     * @serial
     */
    int threshold;

    /**
     * The load factor for the hash table.
     *
     * @serial
     */
    final float loadFactor;

    
    transient volatile int modCount;


    public HashIntMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);

        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while (capacity < initialCapacity)
            capacity <<= 1;

        this.loadFactor = loadFactor;
        threshold = (int)(capacity * loadFactor);
        table = new Entry[capacity];
        init();
    }


    public HashIntMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }


    public HashIntMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new Entry[DEFAULT_INITIAL_CAPACITY];
        init();
    }


    public HashIntMap(IntMap<? extends V> m) {
        this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,
                      DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
        putAllForCreate(m);
    }

    // internal utilities


    void init() {
    }

    static int hash(int h) {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }


    static int indexFor(int h, int length) {
        return h & (length-1);
    }


    public int size() {
        return size;
    }


    public boolean isEmpty() {
        return size == 0;
    }

    public V get(int key) {

        int hash = hash(key);
        for (Entry<V> e = table[indexFor(hash, table.length)];
             e != null;
             e = e.next) {
            int k;
            if (e.hash == hash && ((k = e.key) == key || key==(k)))
                return e.value;
        }
        return null;
    }



    public boolean containsKey(int key) {
        return getEntry(key) != null;
    }


    final Entry<V> getEntry(int key) {
        int hash = hash(key);// == null) ? 0 : hash(key.hashCode());
        for (Entry<V> e = table[indexFor(hash, table.length)];
             e != null;
             e = e.next) {
            int k;
            if (e.hash == hash &&
                ((k = e.key) == key || (key==(k))))
                return e;
        }
        return null;
    }


    public V put(int key, V value) {

        int hash = hash(key);
        int i = indexFor(hash, table.length);
        for (Entry<V> e = table[i]; e != null; e = e.next) {
            int k;
            if (e.hash == hash && ((k = e.key) == key || key==(k))) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, key, value, i);
        return null;
    }




    private void putForCreate(int key, V value) {
        int hash = hash(key);
        int i = indexFor(hash, table.length);


        for (Entry<V> e = table[i]; e != null; e = e.next) {
            int k;
            if (e.hash == hash &&
                ((k = e.key) == key || (key==(k)))) {
                e.value = value;
                return;
            }
        }

        createEntry(hash, key, value, i);
    }

    private void putAllForCreate(IntMap<? extends V> m) {
        for (Iterator<? extends IntMap.Entry<? extends V>> i = m.entrySet().iterator(); i.hasNext(); ) {
            IntMap.Entry<? extends V> e = i.next();
            putForCreate(e.getKey(), e.getValue());
        }
    }


    void resize(int newCapacity) {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int)(newCapacity * loadFactor);
    }


    void transfer(Entry[] newTable) {
        Entry[] src = table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++) {
            Entry<V> e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    Entry<V> next = e.next;
                    int i = indexFor(e.hash, newCapacity);
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }


    public void putAll(IntMap<? extends V> m) {
        int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0)
            return;


        if (numKeysToBeAdded > threshold) {
            int targetCapacity = (int)(numKeysToBeAdded / loadFactor + 1);
            if (targetCapacity > MAXIMUM_CAPACITY)
                targetCapacity = MAXIMUM_CAPACITY;
            int newCapacity = table.length;
            while (newCapacity < targetCapacity)
                newCapacity <<= 1;
            if (newCapacity > table.length)
                resize(newCapacity);
        }

        for (Iterator<? extends IntMap.Entry<? extends V>> i = m.entrySet().iterator(); i.hasNext(); ) {
            IntMap.Entry<? extends V> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }


    public V remove(int key) {
        Entry<V> e = removeEntryForKey(key);
        return (e == null ? null : e.value);
    }


    final Entry<V> removeEntryForKey(int key) {
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        Entry<V> prev = table[i];
        Entry<V> e = prev;

        while (e != null) {
            Entry<V> next = e.next;
            int k;
            if (e.hash == hash &&
                ((k = e.key) == key || key==(k))) {
                modCount++;
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    /**
     * Special version of remove for EntrySet.
     */
    final Entry<V> removeMapping(Object o) {
        if (!(o instanceof IntMap.Entry))
            return null;

        IntMap.Entry<V> entry = (IntMap.Entry<V>) o;
        int key = entry.getKey();
        int hash = hash(key);// == null) ? 0 : hash(key.hashCode());
        int i = indexFor(hash, table.length);
        Entry<V> prev = table[i];
        Entry<V> e = prev;

        while (e != null) {
            Entry<V> next = e.next;
            if (e.hash == hash && e.equals(entry)) {
                modCount++;
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }


    public void clear() {
        modCount++;
        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++)
            tab[i] = null;
        size = 0;
    }

    public boolean containsValue(Object value) {
	if (value == null)
            return containsNullValue();

	Entry[] tab = table;
        for (int i = 0; i < tab.length ; i++)
            for (Entry e = tab[i] ; e != null ; e = e.next)
                if (value.equals(e.value))
                    return true;
	return false;
    }

    /**
     * Special-case code for containsValue with null argument
     */
    private boolean containsNullValue() {
	Entry[] tab = table;
        for (int i = 0; i < tab.length ; i++)
            for (Entry e = tab[i] ; e != null ; e = e.next)
                if (e.value == null)
                    return true;
	return false;
    }

    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and
     * values themselves are not cloned.
     *
     * @return a shallow copy of this map
     */
    public Object clone() {
        HashIntMap<V> result = null;
	try {
	    result = (HashIntMap<V>)super.clone();
	} catch (CloneNotSupportedException e) {
	    // assert false;
	}
        result.table = new Entry[table.length];
        result.entrySet = null;
        result.modCount = 0;
        result.size = 0;
        result.init();
        result.putAllForCreate(this);

        return result;
    }

    static class Entry<V> implements IntMap.Entry<V> {
        final int key;
        V value;
        Entry<V> next;
        final int hash;

        /**
         * Creates new entry.
         */
        Entry(int h, int k, V v, Entry<V> n) {
            value = v;
            next = n;
            key = k;
            hash = h;
        }

        public final int getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final V setValue(V newValue) {
	    V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (!(o instanceof IntMap.Entry))
                return false;
            IntMap.Entry e = (IntMap.Entry)o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2)))
                    return true;
            }
            return false;
        }

        public final int hashCode() {
            return (key) ^
                   (value==null ? 0 : value.hashCode());
        }

        public final String toString() {
            return getKey() + "=" + getValue();
        }

        /**
         * This method is invoked whenever the value in an entry is
         * overwritten by an invocation of put(k,v) for a key k that's already
         * in the HashMap.
         */
        void recordAccess(HashIntMap<V> m) {
        }

        /**
         * This method is invoked whenever the entry is
         * removed from the table.
         */
        void recordRemoval(HashIntMap<V> m) {
        }
    }


    void addEntry(int hash, int key, V value, int bucketIndex) {
	Entry<V> e = table[bucketIndex];
        table[bucketIndex] = new Entry<V>(hash, key, value, e);
        if (size++ >= threshold)
            resize(2 * table.length);
    }

    void createEntry(int hash, int key, V value, int bucketIndex) {
	Entry<V> e = table[bucketIndex];
        table[bucketIndex] = new Entry<V>(hash, key, value, e);
        size++;
    }

    private abstract class HashIterator<E> implements Iterator<E> {
        Entry<V> next;	// next entry to return
        int expectedModCount;	// For fast-fail
        int index;		// current slot
        Entry<V> current;	// current entry

        HashIterator() {
            expectedModCount = modCount;
            if (size > 0) { // advance to first entry
                Entry[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
        }

        public final boolean hasNext() {
            return next != null;
        }

        final Entry<V> nextEntry() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Entry<V> e = next;
            if (e == null)
                throw new NoSuchElementException();

            if ((next = e.next) == null) {
                Entry[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
	    current = e;
            return e;
        }

        public void remove() {
            if (current == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            int k = current.key;
            current = null;
            HashIntMap.this.removeEntryForKey(k);
            expectedModCount = modCount;
        }

    }
    private abstract class HashIntIterator implements IntIterator {
        Entry<V> next;	// next entry to return
        int expectedModCount;	// For fast-fail
        int index;		// current slot
        Entry<V> current;	// current entry

        HashIntIterator() {
            expectedModCount = modCount;
            if (size > 0) { // advance to first entry
                Entry[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
        }

        public final boolean hasNext() {
            return next != null;
        }

        final Entry<V> nextEntry() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Entry<V> e = next;
            if (e == null)
                throw new NoSuchElementException();

            if ((next = e.next) == null) {
                Entry[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
	    current = e;
            return e;
        }

        public void remove() {
            if (current == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            int k = current.key;
            current = null;
            HashIntMap.this.removeEntryForKey(k);
            expectedModCount = modCount;
        }

    }

    private final class ValueIterator extends HashIterator<V> {
        public V next() {
            return nextEntry().value;
        }
    }

    private final class KeyIterator extends HashIntIterator {
        public int next() {
            return nextEntry().getKey();
        }
    }

    private final class EntryIterator extends HashIterator<IntMap.Entry<V>> {
        public IntMap.Entry<V> next() {
            return nextEntry();
        }
    }

    // Subclass overrides these to alter behavior of views' iterator() method
    IntIterator newKeyIterator()   {
        return new KeyIterator();
    }
    Iterator<V> newValueIterator()   {
        return new ValueIterator();
    }
    Iterator<IntMap.Entry<V>> newEntryIterator()   {
        return new EntryIterator();
    }


    // Views

    private transient Set<IntMap.Entry<V>> entrySet = null;


    public IntSet keySet() {
        IntSet ks = keySet;
        return (ks != null ? ks : (keySet = new KeySet()));
    }

    private final class KeySet extends AbstractIntSet {
        public IntIterator iterator() {
            return newKeyIterator();
        }
        public int size() {
            return size;
        }
        public boolean contains(int o) {
            return containsKey(o);
        }
        public boolean remove(int o) {
            return HashIntMap.this.removeEntryForKey(o) != null;
        }
        public void clear() {
            HashIntMap.this.clear();
        }
    }


    public Collection<V> values() {
        Collection<V> vs = values;
        return (vs != null ? vs : (values = new Values()));
    }

    private final class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return newValueIterator();
        }
        public int size() {
            return size;
        }
        public boolean contains(Object o) {
            return containsValue(o);
        }
        public void clear() {
            HashIntMap.this.clear();
        }
    }

    public Set<IntMap.Entry<V>> entrySet() {
	return entrySet0();
    }

    private Set<IntMap.Entry<V>> entrySet0() {
        Set<IntMap.Entry<V>> es = entrySet;
        return es != null ? es : (entrySet = new EntrySet());
    }

    private final class EntrySet extends AbstractSet<IntMap.Entry<V>> {
        public Iterator<IntMap.Entry<V>> iterator() {
            return newEntryIterator();
        }
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            IntMap.Entry<V> e = (IntMap.Entry<V>) o;
            Entry<V> candidate = getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }
        public boolean remove(Object o) {
            return removeMapping(o) != null;
        }
        public int size() {
            return size;
        }
        public void clear() {
            HashIntMap.this.clear();
        }
    }


    private void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
	Iterator<IntMap.Entry<V>> i =
	    (size > 0) ? entrySet0().iterator() : null;

	// Write out the threshold, loadfactor, and any hidden stuff
	s.defaultWriteObject();

	// Write out number of buckets
	s.writeInt(table.length);

	// Write out size (number of Mappings)
	s.writeInt(size);

        // Write out keys and values (alternating)
	if (i != null) {
	    while (i.hasNext()) {
		IntMap.Entry<V> e = i.next();
		s.writeObject(e.getKey());
		s.writeObject(e.getValue());
	    }
        }
    }

    private static final long serialVersionUID = 362498820763181265L;

    /**
     * Reconstitute the <tt>HashMap</tt> instance from a stream (i.e.,
     * deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s)
         throws IOException, ClassNotFoundException
    {
	// Read in the threshold, loadfactor, and any hidden stuff
	s.defaultReadObject();

	// Read in number of buckets and allocate the bucket array;
	int numBuckets = s.readInt();
	table = new Entry[numBuckets];

        init();  // Give subclass a chance to do its thing.

	// Read in size (number of Mappings)
	int size = s.readInt();

	// Read the keys and values, and put the mappings in the HashIntMap
	for (int i=0; i<size; i++) {
	    int key = (Integer) s.readObject();
	    V value = (V) s.readObject();
	    putForCreate(key, value);
	}
    }

    // These methods are used when serializing HashSets
    int   capacity()     { return table.length; }
    float loadFactor()   { return loadFactor;   }
}
