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

import choco.kernel.solver.Configuration;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
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
	protected final IObjectiveManager objManager;

	/**
	 * constructor
	 * @param solver the solver
     * @param maximize maximization or minimization ?
     * @param configuration
     */
	protected AbstractOptimize(Solver solver, IObjectiveManager bounds, boolean maximize) {
		super(solver);
		this.objManager = bounds;
		objective = bounds.getObjective();
		doMaximize = maximize;
	}


	@Override
	public final IObjectiveManager getObjectiveManager() {
		return objManager;
	}

	@Override
	public void newFeasibleRootState() {
		super.newFeasibleRootState();
		objManager.initBounds();
	}


	@Override
	public void writeSolution(Solution sol) {
		super.writeSolution(sol);
		objManager.writeObjective(sol);
	}

	@Override
	public void recordSolution() {
		super.recordSolution();
		objManager.setBound();
		objManager.setTargetBound();
	}


	/**
	 * we use  targetBound data structures for the optimization cuts
	 */
	@Override
	public void postDynamicCut() throws ContradictionException {
		objManager.postTargetBound();
	}



	@Override
	protected void advancedInitialPropagation() throws ContradictionException {
		if(solver.getConfiguration().readBoolean(Configuration.INIT_DESTRUCTIVE_LOWER_BOUND) 
				|| solver.getConfiguration().readBoolean(Configuration.BOTTOM_UP) ) {
			shavingTools.destructiveLowerBound(objManager);
		}
		super.advancedInitialPropagation();
	}

	@Override
	public Boolean nextSolution() {
		if( objManager.isTargetInfeasible()) {
			//the search is finished as the optimum has been proven by the bounding mechanism.
			return Boolean.FALSE;
		}else {
			//otherwise, continue the search.
			return super.nextSolution();
		}
	}


	protected final void bottomUpSearch() {
		while( shavingTools.nextBottomUp(objManager) == Boolean.FALSE) {
			//The current upper bound is infeasible, try next
			objManager.incrementFloorBound();
			if(objManager.isTargetInfeasible() ) return; //problem is infeasible
			else {
				//partially initialize a new search tree
				clearTrace();
				solver.worldPopUntil(baseWorld+1);
				nextMove = INIT_SEARCH;
			} 
		}
	}

	@Override
	public void incrementalRun() {
		initialPropagation();
		if(isFeasibleRootState()) {
			assert(solver.getWorldIndex() > baseWorld);
			if( solver.getConfiguration().readBoolean(Configuration.BOTTOM_UP) ) bottomUpSearch();
			else topDownSearch();
		}
		endTreeSearch();
	}


	@Override
	public String partialRuntimeStatistics(boolean logOnSolution) {
		if( logOnSolution) {
			return "Objective: "+objManager.getObjectiveValue()+", "+super.partialRuntimeStatistics(logOnSolution);
		}else {
			return "Upper-bound: "+objManager.getBestObjectiveValue()+", "+super.partialRuntimeStatistics(logOnSolution);
		}

	}


	@Override
	public String runtimeStatistics() {
		return "  "+ (doMaximize ? "Maximize: " : "Minimize: ") + objective + '\n' +super.runtimeStatistics();
	}


	@Override
	public void restoreBestSolution() {
		super.restoreBestSolution();
		if( ! objManager.getBestObjectiveValue().equals(objManager.getObjectiveValue())) {
			throw new SolverException("Illegal state: the best objective "+objManager.getBestObjectiveValue()+" is not equal to the best solution objective "+objManager.getObjectiveValue());
		}
	}




}
