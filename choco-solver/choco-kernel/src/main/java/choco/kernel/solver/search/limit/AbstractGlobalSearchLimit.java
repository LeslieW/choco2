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
package choco.kernel.solver.search.limit;

import choco.IPretty;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;


/**
 * An abstract class for limiting tree search (imposing conditions on depth, ...)
 */
public abstract class AbstractGlobalSearchLimit implements IPretty {

	/**
	 * the strategy that delegates the limit checking task to such AbstractGlobalSearchLimit objects
	 */
	protected final AbstractGlobalSearchStrategy strategy;

	/**
	 * for pretty printing
	 */
	protected final String unit;

	/**
	 * type of limit
	 */
	protected final Limit type;

	/**
	 * maximal value limitting the search exploration
	 */
	protected int nbMax;
	
	
	public AbstractGlobalSearchLimit(AbstractGlobalSearchStrategy theStrategy,int theLimit, String unit) {
		strategy = theStrategy;
		nbMax = theLimit;
		this.type=null;
		this.unit=unit;
	}

	public AbstractGlobalSearchLimit(AbstractGlobalSearchStrategy theStrategy,int theLimit, Limit type) {
		strategy = theStrategy;
		nbMax = theLimit;
		this.type=type;
		this.unit= type.getUnit();
	}


	public final AbstractGlobalSearchStrategy getSearchStrategy() {
		return strategy;
	}
	
	
	@Override
	public String toString() {
		return pretty();
	}



	public String pretty() {
		StringBuilder b = new StringBuilder();
		b.append(getNb());
		if (nbMax != Integer.MAX_VALUE) {
			b.append('/').append(nbMax);
		}
		b.append(' ').append(unit);
		return new String(b);
	}

	/**
	 * get the current counter
	 */

	public abstract int getNb();

	/**
	 * @return the limit value
	 */
	public final int getNbMax() {
		return nbMax;
	}

	public void setNbMax(int nbMax) {
		this.nbMax = nbMax;
	}

	public final Limit getType() {
		return type;
	}

	
	public final String getUnit() {
		return unit;
	}

	
}



