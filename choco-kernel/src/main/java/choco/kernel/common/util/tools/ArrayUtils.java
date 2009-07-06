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
 *                   N. Jussien    1999-2009      *
 **************************************************/
package choco.kernel.common.util.tools;

import choco.kernel.common.IIndex;
import gnu.trove.TIntArrayList;
import gnu.trove.TLongHashSet;

import java.util.*;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 3 juil. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*/
public class ArrayUtils {
    public static TLongHashSet hs = new TLongHashSet();
    public static ArrayList<Object> c = new ArrayList<Object>();

    public static int[] zeroToN(int n) {
        final int[] r = new int[n];
        for (int i = 0; i < n; i++) {
            r[i] = i;
        }
        return r;
    }

    public static int[] oneToN(int n) {
        final int[] r = new int[n];
        for (int i = 1; i <= n; i++) {
            r[i - 1] = i;
        }
        return r;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] getColumn(final T[][] array, final int column) {
        if (array != null && array.length > 0
                && column >= 0 && array[0].length > column) {
            T[] res = (T[]) java.lang.reflect.Array.newInstance(array[0][column].getClass(), array.length);
            for (int i = 0; i < array.length; i++) {
                res[i] = array[i][column];
            }
            return res;
        }
        return null;
    }

    public static <T> int length(final T[]... arrays) {
        int length = 0;
        for (T[] array : arrays) {
            if (array != null) length += array.length;
        }
        return length;
    }

    public static <T> T get(int index, final T[]... arrays) {
        int shift = index;
        for (T[] tab : arrays) {
            if (shift < tab.length) {
                return tab[shift];
            } else {
                shift -= tab.length;
            }
        }
        return null;
    }

    public static <T> T get(int index, final List<T>... arrays) {
        int shift = index;
        for (List<T> tab : arrays) {
            if (shift < tab.size()) {
                return tab.get(shift);
            } else {
                shift -= tab.size();
            }
        }
        return null;
    }

    /**
     * Append two Arrays
     *
     * @param toAppend array of arrays to append
     * @return a new Array composed of both given in parameters.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] append(T[]... toAppend) {
        int total = length(toAppend);
        T[] ret = (T[]) java.lang.reflect.Array.newInstance(toAppend.getClass().getComponentType().getComponentType(), total);
        int pos = 0;
        for (T[] tab : toAppend) {
            if (tab != null) {
                System.arraycopy(tab, 0, ret, pos, tab.length);
                pos += tab.length;
            }
        }
        return ret;
    }

    /**
     * Reverse all signs of the a given int table.
     *
     * @param tab array to inverse
     */
    public static void inverseSign(int[] tab) {
        for (int i = 0; i < tab.length; i++) {
            tab[i] = -tab[i];
        }
    }

    public static void reverse(int[] tab) {
        int tmp;
        final int n = tab.length;
        for (int i = 0; i < n / 2; i++) {
            tmp = tab[i];
            tab[i] = tab[n - i - 1];
            tab[n - i - 1] = tmp;
        }
    }

    public static <T> void reverse(T[] tab) {
        T tmp;
        final int n = tab.length;
        for (int i = 0; i < n / 2; i++) {
            tmp = tab[i];
            tab[i] = tab[n - i];
            tab[n - i] = tmp;
        }
    }

    /**
     * apply a permuation on an array
     */
    @SuppressWarnings("unchecked")
    public static <T> void permutation(int[] permutation, T[] tab) {
        T[] tmp = (T[]) java.lang.reflect.Array.newInstance(tab[0].getClass(), tab.length);
        System.arraycopy(tab, 0, tmp, 0, tab.length);
        for (int i = 0; i < tab.length; i++) {
            tab[i] = tmp[permutation[i]];
        }
    }

    public static <T> List<T> toList(T[] array) {
        return Arrays.asList(array);
    }

    public static <T> T[] toArray(Class c, List<T> list) {
//        T[] array = (T[])Array.newInstance(c, list.size());
//        return list.toArray(array);
        return list.toArray((T[]) java.lang.reflect.Array.newInstance(c, list.size()));
    }

    public static <T> T[] toArray(ArrayList<T> list) {
        return toArray(list.get(0).getClass(), list);
    }

    public static <T> T[][] transpose(T[][] matrix) {
        T[][] ret = (T[][]) java.lang.reflect.Array.newInstance(matrix.getClass().getComponentType(), matrix[0].length);
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (T[]) java.lang.reflect.Array.newInstance(matrix[0].getClass().getComponentType(), matrix.length);
        }

        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
                ret[j][i] = matrix[i][j];

        return ret;

    }

    public static <T> T[] flatten(T[][] matrix) {
        int sz = 0;
        for (T[] t : matrix) sz += t.length;
        T[] ret = (T[]) java.lang.reflect.Array.newInstance(matrix[0].getClass().getComponentType(), sz);
        int k = 0;
        for (T[] ta : matrix) {
            for (T t : ta)
                ret[k++] = t;
        }
        return ret;
    }

    /**
     * create a new array which contains sorted distinct values;
     *
     * @param values
     * @return
     */
    public static int[] getNonRedundantSortedValues(int[] values) {
        return createNonRedundantSortedValues(new TIntArrayList(values));
    }

    /**
     * create an array which contains sorted distinct values. do not modify the original list
     */
    public static int[] getNonRedundantSortedValues(TIntArrayList values) {
        return createNonRedundantSortedValues((TIntArrayList) values.clone());
    }

    public static int[] createNonRedundantSortedValues(TIntArrayList values) {
        values.sort();
        int offset = 1;
        while (offset < values.size()) {
            while (values.get(offset - 1) == values.get(offset)) {
                values.remove(offset);
                if (offset == values.size()) {
                    break;
                }
            }
            offset++;
        }
        return values.toNativeArray();
    }

    @SuppressWarnings({"unchecked"})
    public static <V extends IIndex> V[] getNonRedundantObjects(Class classe, V[] all) {
        hs.clear();
        c.clear();
        for (V v : all) {
            if (!hs.contains(v.getIndex())) {
                c.add(v);
                hs.add(v.getIndex());
            }
        }
        if (c.size() != all.length) {
            V[] a = (V[]) java.lang.reflect.Array.newInstance(classe, c.size());
            c.toArray(a);
            return a;
        }
        return all;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> T[] sort(Set<T> set) {
        final LinkedList<T> tmpl = new LinkedList<T>(set);
        if (tmpl.isEmpty()) {
            return null;
        } else {
            T[] tmpa = (T[]) java.lang.reflect.Array.newInstance(tmpl.getFirst().getClass(), tmpl.size());
            tmpl.toArray(tmpa);
            Arrays.sort(tmpa);
			return tmpa;
		}
	}
}
