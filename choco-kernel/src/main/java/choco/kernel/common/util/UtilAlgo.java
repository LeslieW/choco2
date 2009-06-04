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
package choco.kernel.common.util;

import static java.lang.reflect.Array.newInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.kernel.solver.variables.integer.IntDomainVar;



/*
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 31 oct. 2006
 * Since : Choco 2.0.0
 *
 */
public class UtilAlgo {

	/**
	 * A quickSort algorithm for sorting a table of variable according
	 * to a table of integers.
	 * @param a : the integer table to be sorted
	 * @param vs : the intvar table to be sorted according a
     * @param left
     * @param right
	 */
	public static void quicksort(int[] a, IntDomainVar[] vs, int left, int right) {
		if (right <= left) {
			return;
		}
		int i = partition(a, vs, left, right);
		quicksort(a, vs, left, i - 1);
		quicksort(a, vs, i + 1, right);
	}

	private static int partition(int[] a, IntDomainVar[] vs, int left, int right) {
		int i = left - 1;
		int j = right;
		while (true) {
			while (a[++i] < a[right]) {
                // a[right] acts as sentinel
			}
			while (a[right] < a[--j]) {
				if (j == left) {
					break;           // don't go out-of-bounds
				}
			}
			if (i >= j) {
				break;                  // check if pointers cross
			}
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
		int total = ChocoUtil.length(toAppend);
		T[] ret = (T[]) newInstance(toAppend.getClass().getComponentType().getComponentType(),total);
		int pos = 0 ;
		for (T[] tab : toAppend) {
			if(tab != null) {
				System.arraycopy(tab,0,ret,pos,tab.length);
				pos += tab.length;
			}
		}
		return ret;
	}




	/**
	 * Reverse a table of integer and variables (use for api on linear combination)
     * @param tab array of integer to reverse
     * @param vs array of variables to reverse
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
     * @param tab array to inverse
     */
	public static void inverseSign(int[] tab) {
		for (int i = 0; i < tab.length; i++) {
			tab[i] = - tab[i];
		}
	}

	public static void reverse(int[] tab) {
		int tmp;
		final int n=tab.length;
		for (int i = 0; i < n/2; i++) {
			tmp=tab[i];
			tab[i]=tab[n-i-1];
			tab[n-i-1]=tmp;
		}
	}

	public static <T> void reverse(T[] tab) {
		T tmp;
		final int n=tab.length;
		for (int i = 0; i < n/2; i++) {
			tmp=tab[i];
			tab[i]=tab[n-i];
			tab[n-i]=tmp;
		}
	}
	/**
	 * apply a permuation on an array
	 */
	@SuppressWarnings("unchecked")
	public static <T> void permutation(int[] permutation,T[] tab) {
		T[] tmp= (T[]) newInstance(tab[0].getClass(),tab.length);
		System.arraycopy(tab, 0, tmp, 0, tab.length);
		for (int i = 0; i < tab.length; i++) {
			tab[i]=tmp[permutation[i]];
		}
	}

    public static <T> List<T> toList(T[] array){
        return Arrays.asList(array);
    }

    public static <T> T[] toArray(Class c, List<T> list){
//        T[] array = (T[])Array.newInstance(c, list.size());
//        return list.toArray(array);
        return list.toArray((T[]) newInstance(c, list.size()));
    }

	public static <T> T[] toArray(ArrayList<T> list){
		return toArray(list.get(0).getClass(), list);
	}

	public static <T> T[][] transpose(T[][] matrix)
	{
		T[][] ret = (T[][]) newInstance(matrix.getClass().getComponentType(),matrix[0].length);
		for (int i  = 0 ; i < ret.length ;i++)
		{
			ret[i] = (T[]) newInstance(matrix[0].getClass().getComponentType(),matrix.length);
		}

		for (int i = 0 ; i < matrix.length ; i++)
			for (int j = 0 ; j < matrix[i].length ; j++)
				ret[j][i] = matrix[i][j];

		return ret;

	}

	public static <T> T[] flatten(T[][] matrix)
	{
		int sz = 0;
		for (T[] t : matrix) sz+=t.length;
		T[] ret = (T[]) newInstance(matrix[0].getClass().getComponentType(),sz);
		int k = 0 ;
		for (T[] ta : matrix)
		{
			for (T t : ta )
				ret[k++] = t;
		}
		return ret;
	}


	/**
	 * Convert a regexp formed with integer charachter into a char formed regexp
	 * for instance, "12%12%" which stands for 1 followed by 2 followed by 12 would be misinterpreted by regular
	 * regular expression parser. We use here the asci code to encode everything as a single char.
	 * Due to char encoding limits, we cannot parse int greater than 2^16-1
	 * @param strRegExp a regexp of integer
	 * @return a char regexp
	 */
	public static String toCharExp(String strRegExp) {
		StringBuffer b = new StringBuffer();
		for (int i =0 ;i < strRegExp.length() ;i++)
		{
			char c = strRegExp.charAt(i);
			if (c == '<')
			{
				int out = strRegExp.indexOf('>',i+1);
				int tmp = Integer.parseInt(strRegExp.substring(i+1,out));
				b.append((char)Automaton.getCharFromInt(tmp));
				i = out;
			}
			else if (Character.isDigit(c))
			{
				b.append((char) Automaton.getCharFromInt(Character.getNumericValue(c)));

			}
			else
			{
				b.append(c);
			}
		}

		return b.toString();

	}

	/**
	 * Transform a char regexp into an int regexp w.r.t. the asci code of each character.
	 * @param charExp a char regexp
	 * @return an int regexp
	 */
	public static String toIntExp (String charExp)
	{
		StringBuffer b = new StringBuffer();
		for (int i = 0 ; i < charExp.length() ; i++)
		{
			char c = charExp.charAt(i);
			if (c == '(' || c == ')' || c == '*' || c == '+' || c == '|')
			{
				b.append(c);
			}
			else
			{
				int n = (int) c;
				if (n >= 35) n--;
				if (n < 10) b.append(n);
				else b.append('<').append(n).append('>');
			}
		}

		return b.toString();
	}


}
