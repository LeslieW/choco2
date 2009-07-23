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
package choco.kernel.common.logging;

import choco.kernel.solver.Solver;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;


public final class WorldFormatter{

	/**
	 * prefixes for log statements (visualize search depth)
	 */
	private final static String[] LOG_PREFIX = { "", ".", "..", "...", "....",
		".....", "......", ".......", "........", ".........", ".........." };
	
	
	private final int worldIndex;
	
	private final int delta;
		
	public WorldFormatter(AbstractGlobalSearchStrategy strategy) {
		this(strategy, 0);
	}
	
	public WorldFormatter(Solver solver, int delta) {
		this(solver.getSearchStrategy(), delta);
	}
	
	public WorldFormatter(Solver solver) {
		this(solver.getSearchStrategy(), 0);
	}
	
	public WorldFormatter(AbstractGlobalSearchStrategy strategy, int delta) {
		this.worldIndex = strategy.getSolver().getWorldIndex();
		this.delta = delta;
	}

	public boolean isLoggable(AbstractGlobalSearchStrategy strategy) {
		return worldIndex <= strategy.getLoggingMaxDepth();
	}
	
	public boolean isLoggable(Solver solver) {
		return isLoggable(solver.getSearchStrategy());
	}

	public int getWorldIndex() {
		return worldIndex;
	}

	@Override
	public String toString() {
		final int tmp = worldIndex + delta;
		return LOG_PREFIX[tmp % (LOG_PREFIX.length)]+"["+tmp+"]";
	}
}

