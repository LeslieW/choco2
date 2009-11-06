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

import java.util.logging.Level;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.variables.Var;




public abstract class AbstractOptimize extends AbstractGlobalSearchStrategy {
	/**
	 * a boolean indicating whether we want to maximize (true) or minimize (false) the objective variable
	 */
	public final boolean doMaximize;

	/**
	 * the objective variable
	 */
	public final Var objective;
	
	/**
	 * the bounding object, record objective value and compute target bound.
	 */
	protected final IObjectiveManager bounds;

	/**
	 * constructor
	 * @param maximize maximization or minimization ?
	 * @param solver the solver
	 */
	protected AbstractOptimize(IObjectiveManager bounds, boolean maximize) {
		super(bounds.getObjective().getSolver());
		this.bounds = bounds;
		objective = bounds.getObjective();
		doMaximize = maximize;
	}

	
	public Var getObjective() {
		return objective;
	}
	
	public final Number getObjectiveValue() {
		return existsSolution() ? bounds.getObjectiveValue() : (Number) null;
	}
	
	
	public final IObjectiveManager getObjectiveManager() {
		return bounds;
	}


	@Override
	public void newFeasibleRootState() {
		super.newFeasibleRootState();
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


	@Override
	public Boolean nextSolution() {
		if( bounds.isTargetInfeasible()) {
			//the search is finished as the optimum has been proven by the bounding mechanism.
			return Boolean.FALSE;
		}else {
			//otherwise, continue the search.
			return super.nextSolution();
		}
	}


	
}
