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
package choco.kernel.solver.search;

/**
 * An abstract class for limiting tree search (imposing conditions on depth, ...)
 */
public abstract class AbstractGlobalSearchLimit implements GlobalSearchLimit {

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
	protected int nbMax = Integer.MAX_VALUE;

	/**
	 * a counter who is limited to values below max
	 */
	protected int nb = 0;

	/**
	 * counting for successive tree search
	 */
	protected int nbTot = 0;

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

	@Override
	public String toString() {
		return getNbAll() +" "+unit;
	}

	public void reset(final boolean first) {
		if (first) {
			nbTot = 0;
		} else {
			nbTot += nb;
		}
		nb = 0;
	}

	public String pretty() {
		String res = nbTot + "[+" + nb + "]";
		if (nbMax != Integer.MAX_VALUE) {
			res += "/" + nbMax;
		}
		return res + " " + unit;
	}

	/**
	 * get the current counter
	 */

	public int getNb() {
		return nb;
	}

	/**
	 * get the total counter
	 */

	public int getNbTot() {
		return nbTot;
	}

	/**
	 * the sum of {@link this#getNb()} {@link this#getNbTot()}
	 *
	 */
	public int getNbAll() {
		return getNb() + getNbTot();
	}
	/**
	 * @return the limit value
	 */

	public int getNbMax() {
		return nbMax;
	}

	/**
	 * Sets the limits
	 *
	 * @param nbMax new value of the limit
	 */

	public void setNbMax(final int nbMax) {
		this.nbMax = nbMax;
	}

	public Limit getType() {
		return type;
	}

	public final AbstractGlobalSearchStrategy getStrategy() {
		return strategy;
	}


}

