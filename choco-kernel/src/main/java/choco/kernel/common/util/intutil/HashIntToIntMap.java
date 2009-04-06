/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.kernel.common.util.intutil;

import choco.kernel.common.util.IntIterator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 26 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
@Deprecated // see trove4j librairy
public class HashIntToIntMap implements  Cloneable, Serializable
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
        protected static int HASH=0;
        protected static int KEY=1;
        protected static int VAL=2;
        protected static int NIDX=3;

        // transient Entry[] table;
        transient int[][] table;
        transient ArrayList<int[]> nexts = new ArrayList<int[]>();

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


        public HashIntToIntMap(int initialCapacity, float loadFactor) {
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
            table = new int[capacity][];
            init();
        }


        public HashIntToIntMap(int initialCapacity) {
            this(initialCapacity, DEFAULT_LOAD_FACTOR);
        }


        public HashIntToIntMap() {
            this.loadFactor = DEFAULT_LOAD_FACTOR;
            threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
            table = new int[DEFAULT_INITIAL_CAPACITY][];
            init();
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

        public int get(int key) {

            int hash = hash(key);
            for (int[] e = table[indexFor(hash, table.length)];
                 e != null;
                 e = nexts.get(e[NIDX])) {
                int k;
                if (e[HASH] == hash && ((k = e[KEY]) == key || key==(k)))
                    return e[VAL];
            }
            return Integer.MAX_VALUE;
        }



        public boolean containsKey(int key) {
            return getEntry(key) != null;
        }


        final int[] getEntry(int key) {
            int hash = hash(key);// == null) ? 0 : hash(key.hashCode());
            for (int[] e = table[indexFor(hash, table.length)];
                 e != null;
                 e = nexts.get(e[NIDX])) {
                int k;
                if (e[HASH] == hash &&
                        ((k = e[KEY]) == key || (key==(k))))
                    return e;
            }
            return null;
        }


        public int put(int key, int value) {

            int hash = hash(key);
            int i = indexFor(hash, table.length);
            for (int[] e = table[i]; e != null; e = nexts.get(e[NIDX])) {
                int k;
                if (e[HASH] == hash && ((k = e[KEY]) == key || key==(k))) {
                    int oldValue = e[VAL];
                    e[VAL] = value;
                    return oldValue;
                }
            }

            modCount++;
            addEntry(hash, key, value, i);
            return Integer.MAX_VALUE;
        }




        private void putForCreate(int key, int value) {
            int hash = hash(key);
            int i = indexFor(hash, table.length);


            for (int[] e = table[i]; e != null; e = nexts.get(e[NIDX])) {
                int k;
                if (e[HASH] == hash &&
                        ((k = e[KEY]) == key || (key==(k)))) {
                    e[VAL] = value;
                    return;
                }
            }

            createEntry(hash, key, value, i);
        }

        private void putAllForCreate(HashIntToIntMap m) {
            for (Iterator<int[]> i = m.entrySet().iterator(); i.hasNext(); ) {
                int[] e = i.next();
                putForCreate(e[KEY], e[VAL]);
            }
        }


        void resize(int newCapacity) {
            int[][] oldTable = table;
            int oldCapacity = oldTable.length;
            if (oldCapacity == MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return;
            }

            int[][] newTable = new int[newCapacity][];
            transfer(newTable);
            table = newTable;
            threshold = (int)(newCapacity * loadFactor);
        }


        void transfer(int[][] newTable) {
            int[][] src = table;
            int newCapacity = newTable.length;
            for (int j = 0; j < src.length; j++) {
                int[] e = src[j];
                if (e != null) {
                    src[j] = null;
                    do {
                        int[] next = nexts.get(e[NIDX]);
                        int i = indexFor(e[HASH], newCapacity);
                        nexts.set(e[NIDX], newTable[i]);
                        newTable[i] = e;
                        e = next;
                    } while (e != null);
                }
            }
        }


        public void putAll(HashIntToIntMap m) {
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

            for (Iterator<int[]> i = m.entrySet().iterator(); i.hasNext(); ) {
                int[] e = i.next();
                put(e[KEY], e[VAL]);
            }
        }


        public int remove(int key) {
            int[] e = removeEntryForKey(key);
            return (e == null ? null : e[VAL]);
        }


        final int[] removeEntryForKey(int key) {
            int hash = hash(key);
            int i = indexFor(hash, table.length);
            int[] prev = table[i];
            int[] e = prev;

            while (e != null) {
                int[] next = nexts.get(e[NIDX]);
                int k;
                if (e[HASH] == hash &&
                        ((k = e[KEY]) == key || key==(k))) {
                    modCount++;
                    size--;
                    if (prev == e)
                        table[i] = next;
                    else
                        nexts.set(prev[NIDX],next);
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
        final int[] removeMapping(Object o) {
            if (!(o instanceof int[]))
                return null;

            int[] entry = (int[]) o;
            int key = entry[KEY];
            int hash = hash(key);// == null) ? 0 : hash(key.hashCode());
            int i = indexFor(hash, table.length);
            int[] prev = table[i];
            int[] e = prev;

            while (e != null) {
                int[] next = nexts.get(e[NIDX]);
                if (e[HASH] == hash && Arrays.equals(e, entry)) {
                    modCount++;
                    size--;
                    if (prev == e)
                        table[i] = next;
                    else
                        nexts.set(prev[NIDX],next);
                    return e;
                }
                prev = e;
                e = next;
            }

            return e;
        }


        public void clear() {
            modCount++;
            int[][] tab = table;
            for (int i = 0; i < tab.length; i++)
                tab[i] = null;
            size = 0;
        }

        public boolean containsValue(int value) {
            if (value == Integer.MAX_VALUE)
                return containsNullValue();

            int[][] tab = table;
            for (int i = 0; i < tab.length ; i++)
                for (int[] e = tab[i] ; e != null ; e = nexts.get(e[NIDX]))
                    if (value == e[VAL])
                        return true;
            return false;
        }

        /**
         * Special-case code for containsValue with null argument
         */
        private boolean containsNullValue() {
            int[][] tab = table;
            for (int i = 0; i < tab.length ; i++)
                for (int[] e = tab[i] ; e != null ; e = nexts.get(e[NIDX]))
                    if (e[VAL] == Integer.MAX_VALUE)
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
            HashIntToIntMap result = null;
            try {
                result = (HashIntToIntMap) super.clone();
            } catch (CloneNotSupportedException e) {
                // assert false;
            }
            result.table = new int[table.length][];
            result.entrySet = null;
            result.modCount = 0;
            result.size = 0;
            result.init();
            result.putAllForCreate(this);

            return result;
        }




        void addEntry(int hash, int key, int value, int bucketIndex) {
            int[] e = table[bucketIndex];
            nexts.add(e);
            table[bucketIndex] = new int[]{hash, key, value, nexts.size()-1};
            if (size++ >= threshold)
                resize(2 * table.length);
        }

        void createEntry(int hash, int key, int value, int bucketIndex) {
            int[] e = table[bucketIndex];
            nexts.add(e);
            table[bucketIndex] = new int[]{hash, key, value, nexts.size()-1};
            size++;
        }

        private abstract class HashIterator<V> implements Iterator<V> {
            int[] next;	// next entry to return
            int expectedModCount;	// For fast-fail
            int index;		// current slot
            int[] current;	// current entry

            HashIterator() {
                expectedModCount = modCount;
                if (size > 0) { // advance to first entry
                    int[][] t = table;
                    while (index < t.length && (next = t[index++]) == null)
                        ;
                }
            }

            public final boolean hasNext() {
                return next != null;
            }

            final int[] nextEntry() {
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                int[] e = next;
                if (e == null)
                    throw new NoSuchElementException();

                if ((next = nexts.get(e[NIDX])) == null) {
                    int[][] t = table;
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
                int k = current[KEY];
                current = null;
                HashIntToIntMap.this.removeEntryForKey(k);
                expectedModCount = modCount;
            }

        }
        private abstract class HashIntIterator implements IntIterator {
            int[] next;	// next entry to return
            int expectedModCount;	// For fast-fail
            int index;		// current slot
            int[] current;	// current entry

            HashIntIterator() {
                expectedModCount = modCount;
                if (size > 0) { // advance to first entry
                    int[][] t = table;
                    while (index < t.length && (next = t[index++]) == null)
                        ;
                }
            }

            public final boolean hasNext() {
                return next != null;
            }

            final int[] nextEntry() {
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                int[] e = next;
                if (e == null)
                    throw new NoSuchElementException();

                if ((next = nexts.get(e[NIDX])) == null) {
                    int[][] t = table;
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
                int k = current[KEY];
                current = null;
                HashIntToIntMap.this.removeEntryForKey(k);
                expectedModCount = modCount;
            }

        }

        private final class ValueIterator extends HashIntIterator {
            public int next() {
                return nextEntry()[VAL];
            }
        }

        private final class KeyIterator extends HashIntIterator {
            public int next() {
                return nextEntry()[KEY];
            }
        }

        private final class EntryIterator extends HashIterator<int[]> {
            public int[] next() {
                return nexts.get(nextEntry()[NIDX]);
            }
        }

        // Subclass overrides these to alter behavior of views' iterator() method
        IntIterator newKeyIterator()   {
            return new KeyIterator();
        }
        IntIterator newValueIterator()   {
            return new ValueIterator();
        }
        Iterator<int[]> newEntryIterator()   {
            return new EntryIterator();
        }


        // Views

        private transient Set<int[]> entrySet = null;
        transient volatile IntSet        keySet = null;
        transient volatile IntCollection values = null;


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
                return HashIntToIntMap.this.removeEntryForKey(o) != null;
            }
            public void clear() {
                HashIntToIntMap.this.clear();
            }
        }


        public IntCollection values() {
            IntCollection vs = values;
            return (vs != null ? vs : (values = new Values()));
        }

        private final class Values extends AbstractIntCollection {
            public IntIterator iterator() {
                return newValueIterator();
            }
            public int size() {
                return size;
            }
            public boolean contains(int o) {
                return containsValue(o);
            }
            public void clear() {
                HashIntToIntMap.this.clear();
            }
        }

        public Set<int[]> entrySet() {
            return entrySet0();
        }

        private Set<int[]> entrySet0() {
            Set<int[]> es = entrySet;
            return es != null ? es : (entrySet = new EntrySet());
        }

        private final class EntrySet extends AbstractSet<int[]> {
            public Iterator<int[]> iterator() {
                return newEntryIterator();
            }
            public boolean contains(Object o) {
                if (!(o instanceof int[]))
                    return false;
                int[] e = (int[]) o;
                int[] candidate = getEntry(e[KEY]);
                return candidate != null && Arrays.equals(candidate, e);
            }
            public boolean remove(Object o) {
                return removeMapping(o) != null;
            }
            public int size() {
                return size;
            }
            public void clear() {
                HashIntToIntMap.this.clear();
            }
        }


        private void writeObject(ObjectOutputStream s)
                throws IOException
        {
            Iterator<int[]> i =
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
                    int[] e = i.next();
                    s.writeObject(e[KEY]);
                    s.writeObject(e[VAL]);
                }
            }
        }

        private static final long serialVersionUID = 362498820763181265L;

        /**
         * Reconstitute the <tt>HashMap</tt> instance from a stream (i.e.,
         * deserialize it).
         */
        private void readObject(ObjectInputStream s)
                throws IOException, ClassNotFoundException
        {
            // Read in the threshold, loadfactor, and any hidden stuff
            s.defaultReadObject();

            // Read in number of buckets and allocate the bucket array;
            int numBuckets = s.readInt();
            table = new int[numBuckets][];

            init();  // Give subclass a chance to do its thing.

            // Read in size (number of Mappings)
            int size = s.readInt();

            // Read the keys and values, and put the mappings in the HashIntMap
            for (int i=0; i<size; i++) {
                int key = (Integer) s.readObject();
                int value = (Integer) s.readObject();
                putForCreate(key, value);
            }
        }

        // These methods are used when serializing HashSets
        int   capacity()     { return table.length; }
        float loadFactor()   { return loadFactor;   }



    }
