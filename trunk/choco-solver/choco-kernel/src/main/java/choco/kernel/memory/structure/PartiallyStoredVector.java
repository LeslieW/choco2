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
package choco.kernel.memory.structure;

import static choco.kernel.common.Constant.*;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.memory.MemoryException;
import choco.kernel.memory.structure.iterators.PSVIndexIterator;
import choco.kernel.memory.structure.iterators.PSVIterator;

import java.util.Arrays;

/**
 * A class implementing a vector with two kind of storage:
 * standard static storage in an array, and backtrackable storage.
 * By convention, objects with small indices (0 .. 999999) are statically managed
 * as if they were in a standard array.
 * And objects with large indices (1000000 ... ) are "stored" in a backtrackable
 * manner, as if they were in a StoredIntVector
 */
public final class PartiallyStoredVector<E> {
    /**
     * objects stored statically
     */
    private E[] staticObjects;
    /**
     * objects stored dynamically
     */
    private E[] storedObjects;

    /**
     * Number of static objects
     */
    private int nStaticObjects;

    /**
     * number of stored objects
     */
    private final IStateInt nStoredObjects;

    /**
     * Constructor
     *
     * @param env environment where the data structure should be created
     */
    @SuppressWarnings({"unchecked"})
    public PartiallyStoredVector(final IEnvironment env) {
        staticObjects = (E[]) new Object[INITIAL_STATIC_CAPACITY];
        storedObjects = (E[]) new Object[INITIAL_STORED_CAPACITY];
        nStaticObjects = 0;
        nStoredObjects = env.makeInt(0);
    }

    /**
     * Clear datastructures for safe reuses
     */
    public void clear() {
        Arrays.fill(staticObjects, null);
        Arrays.fill(storedObjects, null);
        nStaticObjects = 0;
        nStoredObjects.set(0);
    }

    /**
     * Check wether an object is stored.
     * First, try in the array of static objects, then in the array of stored objects.
     * Return true if the PartiallyStoredVector contains the object.
     *
     * @param o the object to look for
     * @return true if it is contained
     */
    public boolean contains(final Object o) {
        for (int i = 0; i < nStaticObjects; i++) {
            if (staticObjects[i].equals(o)) {
                return true;
            }
        }
        for (int i = 0; i < nStoredObjects.get(); i++) {
            if (storedObjects[i].equals(o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a object in the array of static object
     *
     * @param o the object to add
     * @return the indice of this object in the structure
     */
    public int staticAdd(final E o) {
        ensureStaticCapacity(nStaticObjects + 1);
        staticObjects[nStaticObjects++] = o;
        return nStaticObjects - 1;
    }

    /**
     * Insert an object in the array of static object.
     *
     * @param ind indice to insert in
     * @param o   object to insert
     * @return the indice of the last object in the array of static objects
     */
    public int staticInsert(final int ind, final E o) {
        ensureStaticCapacity(nStaticObjects++);
        System.arraycopy(staticObjects, ind, staticObjects, ind+1, nStaticObjects-ind);
        staticObjects[ind] = o;
        return nStaticObjects - 1;
    }

    /**
     * Remove the object placed at indice idx
     *
     * @param idx
     */
    void staticRemove(final int idx) {
        staticObjects[idx] = null;
        if (idx == nStaticObjects - 1) {
            while (staticObjects[nStaticObjects] == null && nStaticObjects > 0) {
                nStaticObjects--;
            }
            if (staticObjects[nStaticObjects] != null) {
                nStaticObjects++;
            }
        }
    }


    /**
     * Remove an object
     *
     * @param o object to remove
     * @return indice of the object
     * @throws choco.kernel.memory.MemoryException
     *          when trying to remove unknown object or stored object
     */
    public int remove(final Object o) {
        for (int i = 0; i < nStaticObjects; i++) {
            final Object staticObject = staticObjects[i];
            if (staticObject == o) {
                staticRemove(i);
                return i;
            }
        }
        throw new MemoryException("impossible to remove the object (a constraint ?) from the static part of the collection (cut manager ?)");
    }

    /**
     * Ensure that the lenght of the array of static objects is always sufficient.
     *
     * @param n the expected size
     */
    @SuppressWarnings({"unchecked"})
    void ensureStaticCapacity(final int n) {
        if (n >= staticObjects.length) {
            int newSize = staticObjects.length;
            while (n >= newSize) {
                newSize = (3 * newSize) / 2;
            }
            final Object[] newStaticObjects = new Object[newSize];
            System.arraycopy(staticObjects, 0, newStaticObjects, 0, staticObjects.length);
            this.staticObjects = (E[]) newStaticObjects;
        }
    }

    /**
     * Add an stored object
     *
     * @param o the object to add
     * @return indice of the object in the structure
     */
    public int add(final E o) {
        ensureStoredCapacity(nStoredObjects.get() + 1);
        storedObjects[nStoredObjects.get()] = o;
        nStoredObjects.add(1);
        return STORED_OFFSET + nStoredObjects.get() - 1;
    }

    /**
     * Insert an stored object
     *
     * @param ind indice where to add object
     * @param o   object to insert
     * @return the size of stored structure
     */
    public int insert(final int ind, final E o) {
        ensureStoredCapacity(nStoredObjects.get() + 1);
        System.arraycopy(storedObjects, ind, storedObjects, ind+1, nStoredObjects.get()-ind);
//
//        for (int i = nStoredObjects.get() + 1; i > ind; i--) {
//            storedObjects[i] = storedObjects[i - 1];
//        }
        storedObjects[ind] = o;
        nStoredObjects.add(1);
        return STORED_OFFSET + nStoredObjects.get() - 1;
    }

    /**
     * Ensure that the stored structure is long enough to add a new element
     *
     * @param n the expected size
     */
    @SuppressWarnings({"unchecked"})
    void ensureStoredCapacity(final int n) {
        if (n >= storedObjects.length) {
            int newSize = storedObjects.length;
            while (n >= newSize) {
                newSize = (3 * newSize) / 2;
            }
            final Object[] newStoredObjects = new Object[newSize];
            System.arraycopy(storedObjects, 0, newStoredObjects, 0, storedObjects.length);
            this.storedObjects = (E[]) newStoredObjects;
        }
    }

    /**
     * Get the index th stored object
     *
     * @param index the indice of the required object
     * @return the 'index'th object
     */
    public E get(final int index) {
        if (index < STORED_OFFSET) {
            return staticObjects[index];
        } else {
            return storedObjects[index - STORED_OFFSET];
        }
    }

    /**
     * Check wether the structure is empty
     *
     * @return true if the structure is empty
     */
    public boolean isEmpty() {
        return ((nStaticObjects == 0) && (nStoredObjects.get() == 0));
    }

    /**
     * Return the number of static and stored objects contained in the structure
     *
     * @return int
     */
    public int size() {
        return (nStaticObjects + nStoredObjects.get());
    }

    public DisposableIntIterator getIndexIterator(){
        return PSVIndexIterator.getIterator(nStaticObjects, staticObjects, nStoredObjects);
    }

    public DisposableIterator getIterator(){
        return PSVIterator.getIterator(nStaticObjects, staticObjects, nStoredObjects, storedObjects);
    }

    /**
     * Check wether the indice idx define a static object
     *
     * @param idx
     * @return
     */
    public static boolean isStaticIndex(final int idx) {
        return idx < STORED_OFFSET;
    }

    /**
     * Return the indexe of an object minus the stored offset
     *
     * @param idx
     * @return
     */
    public static int getSmallIndex(final int idx) {
        if (idx < STORED_OFFSET) {
            return idx;
        } else {
            return idx - STORED_OFFSET;
        }
    }

    public static int getGlobalIndex(final int idx, final boolean isStatic) {
        if (isStatic) {
            return idx;
        } else {
            return idx + STORED_OFFSET;
        }
    }

    /**
     * Return the indice of the last static object
     *
     * @return
     */
    public int getLastStaticIndex() {
        return nStaticObjects - 1;
    }

    /**
     * Return the indice of the first static object
     *
     * @return
     */
    public static int getFirstStaticIndex() {
        return 0;
  }


    /**
     * Return the indice of the last stored object
     *
     * @return
     */
    public int getLastStoredIndex(){
        return nStoredObjects.get()-1;
    }
}
