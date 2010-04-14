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

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
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
	 * @param solver the solver
	 * @param maximize maximization or minimization ?
	 */
	protected AbstractOptimize(Solver solver, IObjectiveManager bounds, boolean maximize) {
		super(solver);
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
	protected void advancedInitialPropagation() throws ContradictionException {
//		if(configuration.isDestructiveLowerBound() || configuration.isBottomUpSearch() ) shavingTools.destructiveLowerBound(bounds);
//		else super.advancedInitialPropagation();
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


	protected final void bottomUpSearch() {
		while( shavingTools.nextBottomUp(bounds) == Boolean.FALSE) {
			//The current upper bound is infeasible, try next
			bounds.incrementFloorBound();
			if(bounds.isTargetInfeasible() ) return; //problem is infeasible
			else {
				//partially initialize a new search tree
				clearTrace();
				solver.worldPopUntil(baseWorld+1);
				nextMove = INIT_SEARCH;
			} 
		}
	}

//	@Override
//	public void incrementalRun() {
//		initialPropagation();
//		if(isFeasibleRootState()) {
//			assert(solver.getWorldIndex() > baseWorld);
//			if( configuration.isTopDownSearch() ) topDownSearch();
//			else bottomUpSearch();
//		}
//		endTreeSearch();
//	}


	@Override
	public String partialRuntimeStatistics(boolean logOnSolution) {
		if( logOnSolution) {
			return "Objective: "+bounds.getObjectiveValue()+", "+super.partialRuntimeStatistics(logOnSolution);
		}else {
			return "Upper-bound: "+bounds.getBestObjectiveValue()+", "+super.partialRuntimeStatistics(logOnSolution);
		}

	}


	@Override
	public String runtimeStatistics() {
		return "  "+ (doMaximize ? "Maximize: " : "Minimize: ") + getObjective() + "\n" +super.runtimeStatistics();
	}




}
