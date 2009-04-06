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
 * the function f_0 as defined in the phd thesis of F. Clautiaux.
 * @author Arnaud Malapert
 *
 */
public class FunctionDFF_f0 extends AbstractDDFF {

	public FunctionDFF_f0(final int[] sizes,final int capacity) {
		super(sizes, capacity);
	}

	public FunctionDFF_f0(final int[] sizes,final int capacity,final BitSet selected) {
		super(sizes, capacity, selected);
	}


	@Override
	protected int applyFunction(final int i,final  int k) {
		if(storedSizes[i]>storedCapacity-k) {
			return storedCapacity;
		}else if(storedSizes[i]<k) {
			return 0;
		}else {return storedSizes[i];}
	}

	@Override
	protected void setParameters(final BitSet params,final  int i) {
		final int k=storedCapacity-storedSizes[i]+1;
		setParameter(params, k);

	}

	@Override
	protected void updateCapacity(final int k) {
		capacity=storedCapacity;
	}

}