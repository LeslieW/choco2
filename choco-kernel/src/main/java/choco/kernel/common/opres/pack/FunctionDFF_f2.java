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

package choco.kernel.common.opres.pack;

import java.util.BitSet;


/**
 * the function f_2 as defined in the phd thesis of F. Clautiaux.
 * @author Arnaud Malapert
 *
 */

public class FunctionDFF_f2 extends AbstractDDFF {


	public FunctionDFF_f2(final int[] sizes, final int capacity, final BitSet selected) {
		super(sizes, capacity, selected);
	}

	/**
	 * @param sizes
	 * @param capacity
	 */
	public FunctionDFF_f2(final int[] sizes, final int capacity) {
		super(sizes, capacity);
	}

	@Override
	protected int applyFunction(final int i, final int k) {
		int v;
		if(storedSizes[i]>storedCapacity/2) {
			v=2*(capacity/2-((storedCapacity-storedSizes[i])/k));
		}else if(storedSizes[i]==storedCapacity/2) {v=capacity/2;}
		else {v=2*(storedSizes[i]/k);}
		return v;
	}

	@Override
	protected void setParameters(final BitSet params, final int i) {
		int k=storedCapacity-storedSizes[i]+1;
		setParameter(params, k);
		if(!setParameter(params, k)) {
			k=storedCapacity-storedSizes[i]+1;
			setParameter(params, k);
		}
	}

	@Override
	protected void updateCapacity(final int k) {
		capacity=2*(storedCapacity/k);

	}


}
