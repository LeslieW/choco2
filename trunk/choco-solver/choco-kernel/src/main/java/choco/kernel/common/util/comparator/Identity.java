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
 * identity permutation.
 * @author Arnaud Malapert</br>
 * @since 4 déc. 2008 <b>version</b> 2.0.1</br>
 * @version 2.0.1</br>
 */
public class Identity implements IPermutation {

	public final static IPermutation SINGLETON = new Identity();

	private Identity() {
		super();
	}

	@Override
	public int[] applyPermutation(int[] source) {
		return source;
	}

	@Override
	public <T> void applyPermutation(T[] source, T[] dest) {
		if(source.length != dest.length) {
			throw new ArrayIndexOutOfBoundsException("the two arguments should have the same length.");
		}else {
			System.arraycopy(source, 0, dest, 0, source.length);
		}
	}
	
	

	@Override
	public <T> void applyPermutation(List<T> source, T[] dest) {
		source.toArray(dest);		
	}

	@Override
	public int getOriginalIndex(int idx) {
		return idx;
	}

	@Override
	public int getPermutationIndex(int idx) {
		return idx;
	}

	@Override
	public boolean isIdentity() {
		return true;
	}

	@Override
	public String toString() {
		return "IDENTITY";
	}





}
