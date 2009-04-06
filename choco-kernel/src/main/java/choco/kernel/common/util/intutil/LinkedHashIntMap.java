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
 * @(#)LinkedHashIntMap.java	1.26 06/04/21
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package choco.kernel.common.util.intutil;

import choco.kernel.common.util.IntIterator;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 12, 2008
 * Time: 1:12:37 PM
 */
@Deprecated // see trove4j librairy
public class LinkedHashIntMap<V>
    extends HashIntMap<V>
    implements IntMap<V>
{

    private static final long serialVersionUID = 3801124242820219131L;

    /**
     * The head of the doubly linked list.
     */
    private transient Entry<V> header;

    /**
     * The iteration ordering method for this linked hash map: <tt>true</tt>
     * for access-order, <tt>false</tt> for insertion-order.
     *
     * @serial
     */
    private final boolean accessOrder;

    /**
     * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance
     * with the specified initial capacity and load factor.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     */
    public LinkedHashIntMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        accessOrder = false;
    }

    /**
     * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance
     * with the specified initial capacity and a default load factor (0.75).
     *
     * @param  initialCapacity the initial capacity
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    public LinkedHashIntMap(int initialCapacity) {
	super(initialCapacity);
        accessOrder = false;
    }

    /**
     * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance
     * with the default initial capacity (16) and load factor (0.75).
     */
    public LinkedHashIntMap() {
	super();
        accessOrder = false;
    }

    /**
     * Constructs an insertion-ordered <tt>LinkedHashMap</tt> instance with
     * the same mappings as the specified map.  The <tt>LinkedHashMap</tt>
     * instance is created with a default load factor (0.75) and an initial
     * capacity sufficient to hold the mappings in the specified map.
     *
     * @param  m the map whose mappings are to be placed in this map
     * @throws NullPointerException if the specified map is null
     */
    public LinkedHashIntMap(IntMap<? extends V> m) {
        super(m);
        accessOrder = false;
    }

    /**
     * Constructs an empty <tt>LinkedHashMap</tt> instance with the
     * specified initial capacity, load factor and ordering mode.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @param  accessOrder     the ordering mode - <tt>true</tt> for
     *         access-order, <tt>false</tt> for insertion-order
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     */
    public LinkedHashIntMap(int initialCapacity,
			 float loadFactor,
                         boolean accessOrder) {
        super(initialCapacity, loadFactor);
        this.accessOrder = accessOrder;
    }

    /**
     * Called by superclass constructors and pseudoconstructors (clone,
     * readObject) before any entries are inserted into the map.  Initializes
     * the chain.
     */
    void init() {
        header = new Entry<V>(-1, Integer.MAX_VALUE, null, null);
        header.before = header.after = header;
    }

    /**
     * Transfers all entries to new table array.  This method is called
     * by superclass resize.  It is overridden for performance, as it is
     * faster to iterate using our linked list.
     */
    void transfer(HashIntMap.Entry[] newTable) {
        int newCapacity = newTable.length;
        for (Entry<V> e = header.after; e != header; e = e.after) {
            int index = indexFor(e.hash, newCapacity);
            e.next = newTable[index];
            newTable[index] = e;
        }
    }


    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value
     */
    public boolean containsValue(Object value) {
        // Overridden to take advantage of faster iterator
        if (value==null) {
            for (Entry e = header.after; e != header; e = e.after)
                if (e.value==null)
                    return true;
        } else {
            for (Entry e = header.after; e != header; e = e.after)
                if (value.equals(e.value))
                    return true;
        }
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     */
    public V get(int key) {
        Entry<V> e = (Entry<V>)getEntry(key);
        if (e == null)
            return null;
        e.recordAccess(this);
        return e.value;
    }

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    public void clear() {
        super.clear();
        header.before = header.after = header;
    }

    /**
     * LinkedHashMap entry.
     */
    private static class Entry<V> extends HashIntMap.Entry<V> {
        // These fields comprise the doubly linked list used for iteration.
        Entry<V> before, after;

	Entry(int hash, int key, V value, HashIntMap.Entry<V> next) {
            super(hash, key, value, next);
        }

        /**
         * Removes this entry from the linked list.
         */
        private void remove() {
            before.after = after;
            after.before = before;
        }

        /**
         * Inserts this entry before the specified existing entry in the list.
         */
        private void addBefore(Entry<V> existingEntry) {
            after  = existingEntry;
            before = existingEntry.before;
            before.after = this;
            after.before = this;
        }

        /**
         * This method is invoked by the superclass whenever the value
         * of a pre-existing entry is read by Map.get or modified by Map.set.
         * If the enclosing Map is access-ordered, it moves the entry
         * to the end of the list; otherwise, it does nothing.
         */
        void recordAccess(HashIntMap<V> m) {
            LinkedHashIntMap<V> lm = (LinkedHashIntMap<V>)m;
            if (lm.accessOrder) {
                lm.modCount++;
                remove();
                addBefore(lm.header);
            }
        }

        void recordRemoval(HashIntMap<V> m) {
            remove();
        }
    }

    private abstract class LinkedHashIterator<T> implements Iterator<T> {
	Entry<V> nextEntry    = header.after;
	Entry<V> lastReturned = null;

	/**
	 * The modCount value that the iterator believes that the backing
	 * List should have.  If this expectation is violated, the iterator
	 * has detected concurrent modification.
	 */
	int expectedModCount = modCount;

	public boolean hasNext() {
            return nextEntry != header;
	}

	public void remove() {
	    if (lastReturned == null)
		throw new IllegalStateException();
	    if (modCount != expectedModCount)
		throw new ConcurrentModificationException();

            LinkedHashIntMap.this.remove(lastReturned.key);
            lastReturned = null;
            expectedModCount = modCount;
	}

	Entry<V> nextEntry() {
	    if (modCount != expectedModCount)
		throw new ConcurrentModificationException();
            if (nextEntry == header)
                throw new NoSuchElementException();

            Entry<V> e = lastReturned = nextEntry;
            nextEntry = e.after;
            return e;
	}
    }
    private abstract class LinkedHashIntIterator implements IntIterator {
	Entry<V> nextEntry    = header.after;
	Entry<V> lastReturned = null;

	/**
	 * The modCount value that the iterator believes that the backing
	 * List should have.  If this expectation is violated, the iterator
	 * has detected concurrent modification.
	 */
	int expectedModCount = modCount;

	public boolean hasNext() {
            return nextEntry != header;
	}

	public void remove() {
	    if (lastReturned == null)
		throw new IllegalStateException();
	    if (modCount != expectedModCount)
		throw new ConcurrentModificationException();

            LinkedHashIntMap.this.remove(lastReturned.key);
            lastReturned = null;
            expectedModCount = modCount;
	}

	Entry<V> nextEntry() {
	    if (modCount != expectedModCount)
		throw new ConcurrentModificationException();
            if (nextEntry == header)
                throw new NoSuchElementException();

            Entry<V> e = lastReturned = nextEntry;
            nextEntry = e.after;
            return e;
	}
    }

    private class KeyIterator extends LinkedHashIntIterator {
	public int next() { return nextEntry().getKey(); }
    }

    private class ValueIterator extends LinkedHashIterator<V> {
	public V next() { return nextEntry().value; }
    }

    private class EntryIterator extends LinkedHashIterator<IntMap.Entry<V>> {
	public IntMap.Entry<V> next() { return nextEntry(); }
    }

    // These Overrides alter the behavior of superclass view iterator() methods
    IntIterator newKeyIterator()   { return new KeyIterator();   }
    Iterator<V> newValueIterator() { return new ValueIterator(); }
    Iterator<IntMap.Entry<V>> newEntryIterator() { return new EntryIterator(); }

    /**
     * This override alters behavior of superclass put method. It causes newly
     * allocated entry to get inserted at the end of the linked list and
     * removes the eldest entry if appropriate.
     */
    void addEntry(int hash, int key, V value, int bucketIndex) {
        createEntry(hash, key, value, bucketIndex);

        // Remove eldest entry if instructed, else grow capacity if appropriate
        Entry<V> eldest = header.after;
        if (removeEldestEntry(eldest)) {
            removeEntryForKey(eldest.key);
        } else {
            if (size >= threshold)
                resize(2 * table.length);
        }
    }

    /**
     * This override differs from addEntry in that it doesn't resize the
     * table or remove the eldest entry.
     */
    void createEntry(int hash, int key, V value, int bucketIndex) {
        HashIntMap.Entry<V> old = table[bucketIndex];
	Entry<V> e = new Entry<V>(hash, key, value, old);
        table[bucketIndex] = e;
        e.addBefore(header);
        size++;
    }


    protected boolean removeEldestEntry(IntMap.Entry<V> eldest) {
        return false;
    }
}
