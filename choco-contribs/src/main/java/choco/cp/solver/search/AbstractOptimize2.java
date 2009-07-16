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
package choco.cp.solver.search;

import java.util.logging.Level;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.variables.Var;




public abstract class AbstractOptimize2 extends AbstractGlobalSearchStrategy {
	/**
	 * a boolean indicating whether we want to maximize (true) or minize (false) the objective variable
	 */
	public final boolean doMaximize;

	public final Var objective;
	
	protected final IBoundsManager bounds;

	/**
	 * constructor
	 * @param maximize maximization or minimization ?
	 * @param solver the solver
	 */
	protected AbstractOptimize2(IBoundsManager bounds, boolean maximize) {
		super(bounds.getObjective().getSolver());
		this.bounds = bounds;
		objective = bounds.getObjective();
		doMaximize = maximize;
	}

	
	public Var getObjective() {
		return objective;
	}
	
	public final Number getObjectiveValue() {
		return bounds.getObjectiveValue();
	}
	
	
	public final IBoundsManager getBounds() {
		return bounds;
	}


	@Override
	public void newTreeSearch() throws ContradictionException {
		super.newTreeSearch();
		bounds.initBounds();
	}



	@Override
	public void writeSolution(Solution sol) {
		super.writeSolution(sol);
		bounds.writeObjective(sol);
	}


	@Override
	public void recordSolution() {
		if(LOGGER.isLoggable(Level.FINE)) {
			LOGGER.log(Level.FINE, "solution with cost {1}", new Object[]{ Integer.valueOf(-1), getObjective()});
		}
		super.recordSolution();
		bounds.setBound();
		bounds.setTargetBound();
	}

	
	/**
	 * we use  targetBound data structures for the optimization cuts
	 */
	@Override
	public void postDynamicCut() throws ContradictionException {
		bounds.postTargetBound();
	}

	@Override
	public void endTreeSearch() {
		if (LOGGER.isLoggable(Level.CONFIG)) {
			LOGGER.log(Level.CONFIG, "{1} => {2}", new Object[]{-1, doMaximize ? "maximize" : "minimize", getObjective()});
		}
		super.endTreeSearch();
	}


}