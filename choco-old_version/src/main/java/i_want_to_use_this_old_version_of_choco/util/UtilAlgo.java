package i_want_to_use_this_old_version_of_choco.util;

import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

import java.lang.reflect.Array;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 31 oct. 2006
 * Time: 14:39:37
 * To change this template use File | Settings | File Templates.
 */
public class UtilAlgo {

    /**
     * A quickSort algorithm for sorting a table of variable according
     * to a table of integers.
     * @param a : the integer table to be sorted
     * @param vs : the intvar table to be sorted according a
     */
    public static void quicksort(int[] a, IntDomainVar[] vs, int left, int right) {
        if (right <= left) return;
        int i = partition(a, vs, left, right);
        quicksort(a, vs, left, i - 1);
        quicksort(a, vs, i + 1, right);
    }

    private static int partition(int[] a, IntDomainVar[] vs, int left, int right) {
        int i = left - 1;
        int j = right;
        while (true) {
            while (a[++i] < a[right])      // find item on left to swap
                ;                               // a[right] acts as sentinel
            while (a[right] < a[--j])      // find item on right to swap
                if (j == left) break;           // don't go out-of-bounds
            if (i >= j) break;                  // check if pointers cross
            exch(a, vs, i, j);                    // swap two elements into place
        }
        exch(a, vs, i, right);                      // swap with partition element
        return i;
    }

    private static void exch(int[] a, IntDomainVar[] vs, int i, int j) {
        int swap = a[i];
        IntDomainVar vswap = vs[i];
        a[i] = a[j];
        vs[i] = vs[j];
        a[j] = swap;
        vs[j] = vswap;
    }

    /**
     * Append two Arrays
     * @param toAppend array of arrays to append
     * @return a new Array composed of both given in parameters.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] append(T[]... toAppend)
    {
        int total = 0;
        for (T[] tab : toAppend)
            total+=tab.length;
        T[] ret = (T[]) Array.newInstance(toAppend[0].getClass().getComponentType(),total);
        int pos = 0 ;
        for (T[] tab : toAppend)
        {
            System.arraycopy(tab,0,ret,pos,tab.length);
            pos += tab.length;
        }
        return ret;
    }


    /**
     * Reverse a table of integer and variables (use for api on linear combination)
     */
    public static void reverse(int[] tab, IntDomainVar[] vs) {
        int[] revtab = new int[tab.length];
        IntDomainVar[] revvs = new IntDomainVar[vs.length];
        for (int i = 0; i < revtab.length; i++) {
            revtab[i] = tab[revtab.length - 1 - i];
            revvs[i] = vs[revtab.length - 1 - i];
        }
        for (int i = 0; i < revtab.length; i++) {
            tab[i] = revtab[i];
            vs[i] = revvs[i];
        }
    }

    /**
     * Reverse all signs of the a given int table.
     */
    public static void inverseSign(int[] tab) {
        for (int i = 0; i < tab.length; i++) {
            tab[i] = - tab[i];
        }
    }

    public static void main(String[] args) {
        Problem pb = new Problem();
        IntDomainVar[] v1 = pb.makeEnumIntVarArray("a",13,0,1);
        IntDomainVar[] v2 = pb.makeEnumIntVarArray("b",13,0,2);
        IntDomainVar[] v3 = pb.makeEnumIntVarArray("c",13,0,3);

        IntDomainVar[] app = append(v1,v2,v3);
        for (IntDomainVar v : app)
            System.out.println(v.pretty());

    }

}
