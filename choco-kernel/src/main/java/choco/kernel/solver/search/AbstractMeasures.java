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

import java.util.Collection;

public abstract class AbstractMeasures implements IMeasures {

	
	public abstract Collection<AbstractGlobalSearchLimit> getLimits();
	
	protected int getLimitValue(Limit limit) {
		return getLimitValue(getLimits(), limit);
	}
	
	@Override
	public boolean existsSolution() {
		return getSolutionCount() > 0;
	}

	@Override
	public final int getBackTrackCount() {
		return getLimitValue(Limit.BACKTRACK);
	}

	@Override
	public final int getCpuTimeCount() {
		return getLimitValue(Limit.CPU_TIME);
	}

	@Override
	public final int getFailCount() {
		return getLimitValue(Limit.FAIL);
	}

	@Override
	public int getIterationCount() {
		LOGGER.warning("not yet implemented");
		return -1;
	}

	@Override
	public final int getNodeCount() {
		return getLimitValue(Limit.NODE);
	}


	@Override
	public int getSolutionCount() {
		LOGGER.warning("not yet implemented");
		return -1;
	}

	@Override
	public final int getTimeCount() {
		return getLimitValue(Limit.TIME);
	}
	
	@Override
	public String pretty() {
		final StringBuilder b = new StringBuilder();
		for (AbstractGlobalSearchLimit l : getLimits()) {
			b.append(l.pretty()).append(" ; ");
		}
		return new String(b);
	}

	public static final AbstractGlobalSearchLimit getLimit(Collection<AbstractGlobalSearchLimit> limits, Limit limit) {
		for (AbstractGlobalSearchLimit l : limits) {
			if (l.getType().equals(limit)) {
				return l;
			}
		}
		return null;
	}

	public static final int getLimitValue(Collection<AbstractGlobalSearchLimit> limits, Limit limit) {
		final AbstractGlobalSearchLimit l = getLimit(limits, limit);
		return l == null ? -1 : l.getNbAll();
	}
}
