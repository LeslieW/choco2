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
 * @(#)AbstractIntMap.java	1.50 06/06/16
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package choco.kernel.common.util.intutil;

import choco.kernel.common.util.IntIterator;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;



/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 12, 2008
 * Time: 1:12:37 PM
 */
@Deprecated // see trove4j librairy
public abstract class AbstractIntMap<V> implements IntMap<V> {

    protected AbstractIntMap() {
    }

    public int size() {
        return entrySet().size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }


    public boolean containsValue(Object value) {
        Iterator<Entry<V>> i = entrySet().iterator();
        if (value==null) {
            while (i.hasNext()) {
                Entry<V> e = i.next();
                if (e.getValue()==null)
                    return true;
            }
        } else {
            while (i.hasNext()) {
                Entry<V> e = i.next();
                if (value.equals(e.getValue()))
                    return true;
            }
        }
        return false;
    }

    public boolean containsKey(int key) {

        for (Entry<V> vEntry : entrySet()) {
            Entry<V> e = vEntry;
            if (key == (e.getKey()))
                return true;
        }

        return false;
    }


    public V get(int key) {
        Iterator<Entry<V>> i = entrySet().iterator();

        while (i.hasNext()) {
            Entry<V> e = i.next();
            if (key==(e.getKey()))
                return e.getValue();
        }

        return null;
    }


    public V put(int key, V value) {
        throw new UnsupportedOperationException();
    }


    public V remove(int key) {
        Iterator<Entry<V>> i = entrySet().iterator();
        Entry<V> correctEntry = null;

        while (correctEntry==null && i.hasNext()) {
            Entry<V> e = i.next();
            if (key==(e.getKey()))
                correctEntry = e;

        }

        V oldValue = null;
        if (correctEntry !=null) {
            oldValue = correctEntry.getValue();
            i.remove();
        }
        return oldValue;
    }



    public void putAll(IntMap<? extends V> m) {
        for (Entry<? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }


    public void clear() {
        entrySet().clear();
    }


    transient volatile IntSet        keySet = null;
    transient volatile Collection<V> values = null;


    public IntSet keySet() {
        if (keySet == null) {
            keySet = new AbstractIntSet() {
                public IntIterator iterator() {
                    return new IntIterator() {
                        private Iterator<Entry<V>> i = entrySet().iterator();

                        public boolean hasNext() {
                            return i.hasNext();
                        }

                        public int next() {
                            return i.next().getKey();
                        }

                        public void remove() {
                            i.remove();
                        }
                    };
                }

                public int size() {
                    return AbstractIntMap.this.size();
                }

                public boolean contains(int k) {
                    return AbstractIntMap.this.containsKey(k);
                }
            };
        }
        return keySet;
    }


    public Collection<V> values() {
        if (values == null) {
            values = new AbstractCollection<V>() {
                public Iterator<V> iterator() {
                    return new Iterator<V>() {
                        private Iterator<Entry<V>> i = entrySet().iterator();

                        public boolean hasNext() {
                            return i.hasNext();
                        }

                        public V next() {
                            return i.next().getValue();
                        }

                        public void remove() {
                            i.remove();
                        }
                    };
                }

                public int size() {
                    return AbstractIntMap.this.size();
                }

                public boolean contains(Object v) {
                    return AbstractIntMap.this.containsValue(v);
                }
            };
        }
        return values;
    }

    public abstract Set<Entry<V>> entrySet();


    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof IntMap))
            return false;
        IntMap<V> m = (IntMap<V>) o;
        if (m.size() != size())
            return false;

        try {
            Iterator<Entry<V>> i = entrySet().iterator();
            while (i.hasNext()) {
                Entry<V> e = i.next();
                int key = e.getKey();
                V value = e.getValue();
                if (value == null) {
                    if (!(m.get(key)==null && m.containsKey(key)))
                        return false;
                } else {
                    if (!value.equals(m.get(key)))
                        return false;
                }
            }
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }

        return true;
    }


    public int hashCode() {
        int h = 0;
        Iterator<Entry<V>> i = entrySet().iterator();
        while (i.hasNext())
            h += i.next().hashCode();
        return h;
    }


    public String toString() {
        Iterator<Entry<V>> i = entrySet().iterator();
        if (! i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<V> e = i.next();
            int key = e.getKey();
            V value = e.getValue();
            sb.append(key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (! i.hasNext())
                return sb.append('}').toString();
            sb.append(", ");
        }
    }

    /**
     * Returns a shallow copy of this <tt>AbstractMap</tt> instance: the keys
     * and values themselves are not cloned.
     *
     * @return a shallow copy of this map
     */
    protected Object clone() throws CloneNotSupportedException {
        AbstractIntMap<V> result = (AbstractIntMap<V>)super.clone();
        result.keySet = null;
        result.values = null;
        return result;
    }


    private static boolean eq(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }


    public static class SimpleEntry<V>
            implements Entry<V>, java.io.Serializable
    {
        private static final long serialVersionUID = -8499721149061103585L;

        private final int key;
        private V value;


        public SimpleEntry(int key, V value) {
            this.key   = key;
            this.value = value;
        }


        public SimpleEntry(Entry<? extends V> entry) {
            this.key   = entry.getKey();
            this.value = entry.getValue();
        }


        public int getKey() {
            return key;
        }


        public V getValue() {
            return value;
        }


        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }


        public boolean equals(Object o) {
            if (!(o instanceof Entry))
                return false;
            Entry e = (Entry)o;
            return eq(key, e.getKey()) && eq(value, e.getValue());
        }


        public int hashCode() {
            return (key ) ^
                    (value == null ? 0 : value.hashCode());
        }


        public String toString() {
            return key + "=" + value;
        }

    }


    public static class SimpleImmutableEntry<V>
            implements Entry<V>, java.io.Serializable
    {
        private static final long serialVersionUID = 7138329143949025153L;

        private final int key;
        private final V value;


        public SimpleImmutableEntry(int key, V value) {
            this.key   = key;
            this.value = value;
        }


        public SimpleImmutableEntry(Entry<? extends V> entry) {
            this.key   = entry.getKey();
            this.value = entry.getValue();
        }


        public int getKey() {
            return key;
        }


        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }


        public boolean equals(Object o) {
            if (!(o instanceof Entry))
                return false;
            Entry e = (Entry)o;
            return eq(key, e.getKey()) && eq(value, e.getValue());
        }


        public int hashCode() {
            return (key) ^
                    (value == null ? 0 : value.hashCode());
        }


        public String toString() {
            return key + "=" + value;
        }

    }

}
