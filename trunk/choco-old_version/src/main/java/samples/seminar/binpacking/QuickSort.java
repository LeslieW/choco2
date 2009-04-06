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
package samples.seminar.binpacking;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 2 juin 2008
 * Since : Choco 2.0.0
 *
 */
public class QuickSort {

    private int[] a;

    public QuickSort(int[] anArray) {
        a = anArray;
    }

    /**
     * Sorts the array managed by this sorter
     */
    public void sort() {
        sort(0, a.length - 1);
    }

    public void sort(int low, int high) {
        if (low >= high) return;
        int p = partition(low, high);
        sort(low, p);
        sort(p + 1, high);
    }

    private int partition(int low, int high) {
        // First element
        int pivot = a[low];

        // Middle element
        //int middle = (low + high) / 2;
        //int pivot = a[middle];
        int i = low - 1;
        int j = high + 1;
        while (i < j) {
            i++;
            while (a[i] > pivot) i++;
            j--;
            while (a[j] < pivot) j--;
            if (i < j) swap(i, j);
        }
        return j;
    }

    /**
     * Swaps two entries of the array.
     *
     * @param i the first position to swap
     * @param j the second position to swap
     */
    private void swap(int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

}