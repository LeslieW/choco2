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
package choco.cp.solver.search.limit;

import java.util.logging.Level;

import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.limit.AbstractLimitManager;
import choco.kernel.solver.search.limit.Limit;


public class LimitManager extends AbstractLimitManager {

	
	/**
	 * Initialize a default limit manager monitoring:
	 * <ul>
	 * <li>time,</li>
	 * <li>node</li>
	 * </ul>
	 */
	public LimitManager() {
		super();
		monitorLimit(Limit.TIME, true);
		monitorLimit(Limit.NODE, true);
	}

	
	@Override
	public AbstractGlobalSearchLimit makeLimit(AbstractGlobalSearchStrategy strategy, Limit type, int theLimit) {
		switch (type) {
		case NODE:
			return theLimit == Integer.MAX_VALUE ? new NodeCount(strategy) : new NodeLimit(strategy, theLimit);
		case BACKTRACK:
			return theLimit == Integer.MAX_VALUE ? new BackTrackCount(strategy) : new BackTrackLimit(strategy, theLimit);
		case FAIL: return new FailLimit(strategy, theLimit);
		case TIME:
			return (theLimit == Integer.MAX_VALUE ? new TimeCount(strategy) : new TimeLimit(strategy, theLimit));
		default:
			LOGGER.log(Level.WARNING, "cant create limit {0}", type);
		return null;
		}
	}
}
