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
import choco.kernel.common.util.disposable.Disposable;
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
public final class ArrayUtils {

    private ArrayUtils() {
        super();
    }

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

    public static int[] linspace(int begin, int end) {
        if (end > begin) {
            int[] r = new int[end - begin];
            for (int i = begin; i < end; i++) {
                r[i - begin] = i;
            }
            return r;
        } else return null;
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

    public static <T> boolean contains(T[] array, T obj) {
        for (T elem : array) {
            if (elem.equals(obj)) return true;
        }
        return false;
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
        final int n = tab.length - 1;
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

    public static <T> T[] flattenSubMatrix(int iMin, int iLength, int jMin, int jLength, T[][] matrix) {
        T[] ret = (T[]) java.lang.reflect.Array.newInstance(matrix[0].getClass().getComponentType(), iLength * jLength);
        for (int i = 0, k = 0; i < iLength; i++, k += jLength)
            System.arraycopy(matrix[iMin + i], jMin, ret, k, jLength);
        return ret;
    }

    public static int[] flatten(int[][] matrix) {
        int sz = 0;
        for (int[] t : matrix) sz += t.length;
        final int[] ret = new int[sz];
        int k = 0;
        for (int[] ta : matrix) {
            for (int t : ta)
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
    public static <V extends IIndex> V[] getNonRedundantObjects(V[] all) {
        final DLongHashSet hashSet = DLongHashSet.getHashSet();
        final DArrayList list = DArrayList.getArrayList();
        final TLongHashSet thashset = hashSet.get();
        final ArrayList alist = list.get();
        try {
            for (V v : all) {
                if (!thashset.contains(v.getIndex())) {
                    alist.add(v);
                    thashset.add(v.getIndex());
                }
            }
            if (alist.size() != all.length) {
                V[] a = (V[]) java.lang.reflect.Array.newInstance((Class<? extends V[]>) all.getClass().getComponentType(), alist.size());
                alist.toArray(a);
                return a;
            }
            return all;
        } finally {
            hashSet.dispose();
            list.dispose();
        }
    }

    @SuppressWarnings({"unchecked"})
    public static <V extends IIndex> V[] getNonRedundantObjects(Class classe, V[] all) {
        final DLongHashSet hashSet = DLongHashSet.getHashSet();
        final DArrayList list = DArrayList.getArrayList();
        final TLongHashSet thashset = hashSet.get();
        final ArrayList alist = list.get();
        try {
            for (V v : all) {
                if (!thashset.contains(v.getIndex())) {
                    alist.add(v);
                    thashset.add(v.getIndex());
                }
            }
            if (alist.size() != all.length) {
                V[] a = (V[]) java.lang.reflect.Array.newInstance(classe, alist.size());
                alist.toArray(a);
                return a;
            }
            return all;
        } finally {
            hashSet.dispose();
            list.dispose();
        }
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

    public static int[][][][] swallowCopy(int[][][][] arr) {
        int s0 = arr.length;
        int[][][][] copy = new int[s0][][][];
        for (int i = s0 - 1; i >= 0; i--) {
            int s1 = arr[i].length;
            copy[i] = new int[s1][][];
            for (int j = s1 - 1; j >= 0; j--) {
                int s2 = arr[i][j].length;
                copy[i][j] = new int[s2][];
                for (int k = s2 - 1; k >= 0; k--) {
                    int s3 = arr[i][j][k].length;
                    copy[i][j][k] = new int[s3];
                    System.arraycopy(arr[i][j][k], 0, copy[i][j][k], 0, s3);
                }
            }
        }
        return copy;

    }

    public static int[][][] swallowCopy(int[][][] arr) {
        int s0 = arr.length;
        int[][][] copy = new int[s0][][];
        for (int i = s0 - 1; i >= 0; i--) {
            int s1 = arr[i].length;
            copy[i] = new int[s1][];
            for (int j = s1 - 1; j >= 0; j--) {
                int s2 = arr[i][j].length;
                copy[i][j] = new int[s2];

                System.arraycopy(arr[i][j], 0, copy[i][j], 0, s2);
            }
        }
        return copy;

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final class DLongHashSet extends Disposable {
        private static class Holder {
            private Holder() {
            }

            private static final Queue<DLongHashSet> container = Disposable.createContainer();
        }

        private final TLongHashSet hashset;

        public synchronized static DLongHashSet getHashSet() {
            DLongHashSet hs;
            try {
                hs = Holder.container.remove();
            } catch (NoSuchElementException e) {
                hs = build();
            }
            hs.init();
            return hs;
        }

        private static DLongHashSet build() {
            return new DLongHashSet();
        }

        private DLongHashSet() {
            super();
            hashset = new TLongHashSet();
        }

        @Override
        public void init() {
            super.init();
            hashset.clear();
        }

        public TLongHashSet get() {
            return hashset;
        }

        @Override
        public Queue getContainer() {
            return Holder.container;
        }
    }

    private static final class DArrayList extends Disposable {
        private static class Holder {
            private Holder() {
            }

            private static final Queue<DArrayList> container = Disposable.createContainer();
        }

        private final ArrayList<Object> arrayList;

        public synchronized static DArrayList getArrayList() {
            DArrayList al;
            try {
                al = Holder.container.remove();
            } catch (NoSuchElementException e) {
                al = build();
            }
            al.init();
            return al;
        }

        private static DArrayList build() {
            return new DArrayList();
        }

        private DArrayList() {
            super();
            arrayList = new ArrayList<Object>(16);
        }

        @Override
        public void init() {
            super.init();
            arrayList.clear();
        }

        public ArrayList<Object> get() {
            return arrayList;
        }

        @Override
        public Queue getContainer() {
            return Holder.container;
        }
    }
}
