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
 * @(#)HashIntSet.java	1.37 06/04/21
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package choco.kernel.common.util.intutil;

import choco.kernel.common.util.IntIterator;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 12, 2008
 * Time: 1:12:37 PM
 */
@Deprecated // see trove4j librairy
public class HashIntSet
        extends AbstractIntSet
        implements IntSet, Cloneable, java.io.Serializable
{
    static final long serialVersionUID = -5024744406713321676L;

    private transient HashIntToIntMap map;

    private static final int PRESENT = 0;


    public HashIntSet() {
        map = new HashIntToIntMap();
    }

    public HashIntSet(IntCollection c) {
        map = new HashIntToIntMap(Math.max((int) (c.size()/.75f) + 1, 16));
        addAll(c);
    }


    public HashIntSet(int initialCapacity, float loadFactor) {
        map = new HashIntToIntMap(initialCapacity, loadFactor);
    }


    public HashIntSet(int initialCapacity) {
        map = new HashIntToIntMap(initialCapacity);
    }


    /*  HashIntSet(int initialCapacity, float loadFactor, boolean dummy) {
       map = new LinkedHashIntToIntMap<Object>(initialCapacity, loadFactor);
   } */


    public IntIterator iterator() {
        return map.keySet().iterator();
    }


    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }


    public boolean contains(int o) {
        return map.containsKey(o);
    }


    public boolean add(int e) {
        return map.put(e, PRESENT)==Integer.MAX_VALUE;
    }


    public boolean remove(int o) {
        return map.remove(o)==PRESENT;
    }

    public void clear() {
        map.clear();
    }

    public Object clone() {
        try {
            HashIntSet newSet = (HashIntSet) super.clone();
            newSet.map = (HashIntToIntMap) map.clone();
            return newSet;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }


    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out HashMap capacity and load factor
        s.writeInt(map.capacity());
        s.writeFloat(map.loadFactor());

        // Write out size
        s.writeInt(map.size());

        // Write out all elements in the proper order.
        for (IntIterator i=map.keySet().iterator(); i.hasNext(); )
            s.writeObject(i.next());
    }

    /**
     * Reconstitute the <tt>HashSet</tt> instance from a stream (that is,
     * deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in HashMap capacity and load factor and create backing HashMap
        int capacity = s.readInt();
        float loadFactor = s.readFloat();
        map = new HashIntToIntMap(capacity, loadFactor);

        // Read in size
        int size = s.readInt();

        // Read in all elements in the proper order.
        for (int i=0; i<size; i++) {
            int e = (Integer) s.readObject();
            map.put(e, PRESENT);
        }
    }
    public static void main(String[] args) {

        for (int i = 0 ;i < 120 ; i++)
        {
            LOGGER.info(String.valueOf(HashIntToIntMap.hash(i)));

        }
    }
}
