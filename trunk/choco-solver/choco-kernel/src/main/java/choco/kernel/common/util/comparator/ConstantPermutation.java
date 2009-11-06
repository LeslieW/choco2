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

import choco.kernel.model.variables.integer.IntegerConstantVariable;

public class ConstantPermutation extends AbstractSortingPermutation {

	protected final IntegerConstantVariable[] elements;

	public ConstantPermutation(IntegerConstantVariable[] elements, boolean reverse) {
		super(elements.length);
		this.elements = elements;
		this.sort(reverse);
	}

	@Override
	public int compare(Integer o1, Integer o2) {
		return elements[o1].compareTo(elements[o2]);
	}


}
