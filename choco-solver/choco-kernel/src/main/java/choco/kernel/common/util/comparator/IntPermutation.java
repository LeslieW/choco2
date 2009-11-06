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


public class IntPermutation extends AbstractSortingPermutation {

	protected final int[] elements;

	public IntPermutation(int[] elements, boolean reverse) {
		super(elements.length);
		this.elements = elements;
		this.sort(reverse);
	}

	@Override
	public int compare(Integer o1, Integer o2) {
		if(elements[o1]>elements[o2]) {return 1;}
		else if(elements[o1]<elements[o2]) {return -1;}
		else {return 0;}
	}
}
