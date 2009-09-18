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
package choco.cp.solver.configure;

import choco.IPretty;
import choco.kernel.solver.search.restart.GeometricalRestartStrategy;
import choco.kernel.solver.search.restart.LubyRestartStrategy;
import choco.kernel.solver.search.restart.UniversalRestartStrategy;
/**
 * @author Arnaud Malapert</br> 
 * @since 27 juil. 2009 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public class RestartConfiguration implements IPretty {

	/**
	 * do we want to learn nogood from restart <br>
	 * @see Lecoutre, C.; Sais, L.; Tabary, S. & Vidal, <br>
	 * Nogood Recording from Restarts </br>
	 * IJCAI 2007 Proceedings of the 20th International Joint Conference on Artificial Intelligence, Hyderabad, India, January 6-12, 2007, 2007, 131-136
	 * 
	 * 
	 */
	public boolean recordNogoodFromRestart = false;


	/**
	 * Do we want to restart a new search after each solution. This is relevant
	 * in the context of optimization
	 */
	public boolean restartAfterEachSolution = false;

	/**
	 * should we reinitialize the search (branching) after a restart. 
	 * For example, this is relevant with Dom/WDeg search heuristics.
	 */
	public boolean initializeSearchAfterRestart = true;

	/**
	 * Do we want to apply a universal restart policy.
	 * @see Luby; Sinclair & Zuckerman <br>
	 * Optimal Speedup of Las Vegas Algorithms <br>
	 * IPL: Information Processing Letters, 1993, 47, 173-180
	 */
	public UniversalRestartStrategy restartStrategy;


	public RestartConfiguration() {
		super();
	}

	public final boolean isRecordNogoodFromRestart() {
		return recordNogoodFromRestart;
	}

	public final void setRecordNogoodFromRestart(boolean recordNogoodFromRestart) {
		this.recordNogoodFromRestart = recordNogoodFromRestart;
	}

	public final boolean isRestartAfterEachSolution() {
		return restartAfterEachSolution;
	}

	public final void setRestartAfterEachSolution(boolean restartAfterEachSolution) {
		this.restartAfterEachSolution = restartAfterEachSolution;
	}


	public final void setLubyRestartPolicy(int base, int grow) {
		this.restartStrategy = new LubyRestartStrategy(base, grow);
	}

	public final void setGeometricalRestartPolicy(int base, double grow) {
		this.restartStrategy = new GeometricalRestartStrategy(base, grow);
	}

	public final void setRestartStrategy(UniversalRestartStrategy restartPolicy) {
		this.restartStrategy = restartPolicy;
	}



	public final boolean isInitializingSearchAfterRestart() {
		return initializeSearchAfterRestart;
	}

	public final void setInitializeSearchAfterRestart(
			boolean reintializeSearchAfterRestart) {
		this.initializeSearchAfterRestart = reintializeSearchAfterRestart;
	}

	public final UniversalRestartStrategy getRestartStrategy() {
		return restartStrategy;
	}

	public final void cancelRestarts() {
		restartStrategy = null;
		restartAfterEachSolution = false;
		initializeSearchAfterRestart = true;
		setRecordNogoodFromRestart(false);
	}

	public final void reset() {
		cancelRestarts();
		recordNogoodFromRestart = false;
	}

	@Override
	public String pretty() {
		if( restartAfterEachSolution || restartStrategy != null) {
			final StringBuilder b = new StringBuilder();
			if(restartStrategy != null) b.append(restartStrategy.pretty()).append(' ');
			if(restartAfterEachSolution) b.append("FROM_SOLUTION ");
			if( recordNogoodFromRestart) b.append("NOGOOD_RECORDING ");
			return b.toString();
		} else return "NO_RESTARTS";
	}

	@Override
	public String toString() {
		return pretty();
	}




}
