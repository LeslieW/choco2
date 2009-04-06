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
package choco.kernel.common.opres.ssp;

import java.util.BitSet;



/**
 * @author Arnaud Malapert
 *
 */
public abstract class AbstractSubsetSumSolver {

	protected final static int NONE=-1;

	public final int[] sizes;

	public Long capacity;


	protected long objective=0;

	public AbstractSubsetSumSolver(int[] sizes, long capacity) {
		super();
		this.sizes = sizes;
		this.setCapacity(capacity);
	}



	public void reset() {
		objective=0;
	}

	public final int[] getSizes() {
		return sizes;
	}

	public void setCapacity(Long capacity) {
		this.capacity = capacity;
	}


	public final long getCapacity() {
		return capacity;
	}

	public abstract String getName();

	public abstract long run();



	public final long getObjective() {
		return objective;
	}


	public abstract BitSet getSolution();

	@Override
	public String toString() {
		return getName();
	}


}
