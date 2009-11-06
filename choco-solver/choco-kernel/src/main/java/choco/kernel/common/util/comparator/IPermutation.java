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
package choco.kernel.common.util.comparator;

import java.util.List;

/**
 * Modelize a sorting permutation of a set.
 * @author Arnaud Malapert</br>
 * @since 4 déc. 2008 <b>version</b> 2.0.1</br>
 * @version 2.0.1</br>
 */
public interface IPermutation {


	/**
	 * apply the permutation on the source array
	 * @param source the array to be permuted
	 * @param dest the permuted array
	 */
	public <T> void applyPermutation(T[] source,T[] dest);
	
	public <T> void applyPermutation(List<T> source,T[] dest);

	/**
	 * apply the permutation to the integer array
	 * @return the permuted array
	 */
	public int[] applyPermutation(int[] source);

	/**
	 * retu
	 * @param idx the permutation index
	 * @return the index in the original order
	 */
	public int getOriginalIndex(int idx);

	/**
	 * return
	 * @param idx the index in the original order
	 * @return the index in the permutation
	 */
	public int getPermutationIndex(int idx);

	public boolean isIdentity();


}
