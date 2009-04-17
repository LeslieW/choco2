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

import choco.kernel.solver.search.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.Limit;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * check the total amount of CPU time (user + system).
 * @author Arnaud Malapert
 *
 */
public class CpuTimeLimit extends AbstractGlobalSearchLimit {


	private long nanostart=Long.MIN_VALUE;

	private final ThreadMXBean thd;
	/**
	 * @param theStrategy
	 * @param theLimit (ms)
	 */
	public CpuTimeLimit(AbstractGlobalSearchStrategy theStrategy, int theLimit) {
		super(theStrategy, theLimit, Limit.CPU_TIME);
		thd=ManagementFactory.getThreadMXBean();
	}



	/**
	 * @see choco.kernel.solver.search.AbstractGlobalSearchLimit#reset(boolean)
	 */
	@Override
	public void reset(boolean first) {
		super.reset(first);
		nanostart=thd.getCurrentThreadCpuTime();
	}

	private boolean check() {
		long c=thd.getCurrentThreadCpuTime();
		nb= (int) ( (c-nanostart) /1E6) ; //nano second -> ms
		return ((nb + nbTot) < nbMax);
	}

	/**
	 * @see choco.kernel.solver.search.GlobalSearchLimit#endNode(choco.kernel.solver.search.AbstractGlobalSearchStrategy)
	 */
	@Override
	public boolean endNode(AbstractGlobalSearchStrategy strategy) {
		return check();
	}

	/**
	 * @see choco.kernel.solver.search.GlobalSearchLimit#newNode(choco.kernel.solver.search.AbstractGlobalSearchStrategy)
	 */
	@Override
	public boolean newNode(AbstractGlobalSearchStrategy strategy) {
		return check();
	}

}
