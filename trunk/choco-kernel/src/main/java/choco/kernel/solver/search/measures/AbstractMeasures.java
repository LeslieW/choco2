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
package choco.kernel.solver.search.measures;

import java.util.Collection;
import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.Limit;

public abstract class AbstractMeasures implements ISearchMeasures {

	/**
	 * an object for logging trace statements
	 */
	public final static Logger LOGGER = ChocoLogging.getSearchLogger();
	
	protected abstract Collection<AbstractGlobalSearchLimit> getLimits();
	
	public final int getLimitValue(Limit limit) {
		return AbstractGlobalSearchLimit.getLimitValue(getLimits(), limit);
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
	public final int getTimeCount() {
		return getLimitValue(Limit.TIME);
	}
	

}
