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

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.GlobalSearchLimit;

/**
 * An abstract class for limiting tree search (imposing conditions on depth, ...)
 */
public abstract class AbstractGlobalSearchLimit implements GlobalSearchLimit {

	public final static int NEW_NODE = 1;
	
	public final static int END_NODE = 2;
	
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


	protected int limitMask = NEW_NODE + END_NODE;
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

	public final int getLimitMask() {
		return limitMask;
	}
	
	public int getUpdatedNb() {
		return nb;
	}
	
	public final int getUpdatedNbAll() {
		return nbTot + getUpdatedNb();
	}

	@Override
	public String toString() {
		return getNbAll() +" "+unit;
	}


	@Override
	public void initialize() {
		nb = 0;
		nbTot = 0;		
	}

	@Override
	public void reset() {
		nbTot += nb;
		nb = 0;
	}

	public String pretty() {
		StringBuilder b = new StringBuilder();
		b.append(nbTot).append("[+").append(nb).append(']');
		if (nbMax != Integer.MAX_VALUE) {
			b.append('/').append(nbMax);
		}
		b.append(' ').append(unit);
		return new String(b);
	}

	/**
	 * get the current counter
	 */

	public final int getNb() {
		return nb;
	}

	/**
	 * get the total counter
	 */

	public final int getNbTot() {
		return nbTot;
	}

	/**
	 * the sum of {@link this#getNb()} {@link this#getNbTot()}
	 *
	 */
	public final int getNbAll() {
		return nb + nbTot;
	}
	/**
	 * @return the limit value
	 */

	public final int getNbMax() {
		return nbMax;
	}


	public final Limit getType() {
		return type;
	}

	
	public final String getUnit() {
		return unit;
	}

	protected final void raiseContradiction(int nextMove) throws ContradictionException {
		strategy.setEncounteredLimit(this);
		strategy.solver.getPropagationEngine().raiseContradiction(this, ContradictionException.SEARCH_LIMIT, nextMove);
	}
	
	protected final void checkLimit() throws ContradictionException {
		if(  nbTot + nb >= nbMax) {
			raiseContradiction(AbstractGlobalSearchStrategy.STOP);
		}
	}

	@Override
	public final AbstractGlobalSearchStrategy getSearchStrategy() {
		return strategy;
	}

	public static final AbstractGlobalSearchLimit getLimit(Collection<AbstractGlobalSearchLimit> limits, Limit type) {
		for (AbstractGlobalSearchLimit l : limits) {
			if (l.getType().equals(type)) {
				return l;
			}
		}
		return null;
	}

	public static final int getLimitIndex(List<AbstractGlobalSearchLimit> limits, Limit type) {
		final ListIterator<AbstractGlobalSearchLimit> iter = limits.listIterator();
		while(iter.hasNext()) {
			if (iter.next().getType().equals(type)) {
				return Integer.valueOf(iter.previousIndex());
			}
		}
		return -1;
	}

	public static final int getLimitValue(Collection<AbstractGlobalSearchLimit> limits, Limit type) {
		final AbstractGlobalSearchLimit l = getLimit(limits, type);
		return l == null ? -1 : l.getNbAll();
	}




}

