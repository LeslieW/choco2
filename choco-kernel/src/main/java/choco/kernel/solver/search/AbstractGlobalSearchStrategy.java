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
//*  CHOCO: an open-source Constraint Programming  *
//*     System for Research and Education          *
//*                                                *
//*    contributors listed in choco.Entity.java    *
//*           Copyright (C) F. Laburthe, 1999-2006 *
//**************************************************
package choco.kernel.solver.search;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.branch.AbstractBranching;
import choco.kernel.solver.branch.AbstractIntBranching;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.search.measures.AbstractMeasures;
import choco.kernel.solver.search.measures.ISearchMeasures;

/**
 * An abstract class for controlling tree search in various ways
 * version 2.0.3 : change the value of search constant to use bit masks.
 */
public abstract class AbstractGlobalSearchStrategy extends AbstractSearchStrategy {

	/**
	 * constants for driving the incremental search algorithm
	 */
	public static final int INIT_SEARCH = 0;
	public static final int OPEN_NODE = 1;
	public static final int UP_BRANCH = 1 << 1;
	public static final int DOWN_BRANCH = 1 << 2;

	/**
	 * index of the current trace in the stack
	 */
	protected int currentTraceIndex = -1;
	
	/**
	 * a data structure storing the stack of choice contexts (for incremental search explorations)
	 */
	protected IntBranchingTrace[] traceStack;

	/**
	 * search controller: a flag storing the next move in the search tree
	 */
	public int nextMove = INIT_SEARCH;

	/**
	 * indicates whether the control should stop after the first solution is found
	 */
	public boolean stopAtFirstSol = true;

	/**
	 * indicates whether a limit was encountered in the alst incremental search
	 */
	protected GlobalSearchLimit encounteredLimit = null;


	/**
	 * count of the backtracks made during search
	 * @param loggingMaxDepth max depth of the logging
	 */
	public void setLoggingMaxDepth(int loggingMaxDepth) {
		this.loggingMaxDepth = loggingMaxDepth;
	}

	public int getLoggingMaxDepth() {
		return loggingMaxDepth;
	}

	/**
	 * maximal search depth for logging statements
	 */
	protected int loggingMaxDepth = 5;

	/**
	 * /**
	 * the goal that needs be solved to drive the exploration
	 */
	public AbstractIntBranching mainGoal;

	/**
	 * the index of the world where the search started
	 */
	public int baseWorld = 0;

	/**
	 * A set of limits controlling the exploration
	 */
	protected List<AbstractGlobalSearchLimit> limits;
	
	public final ISearchMeasures measures;

	public ISearchLoop searchLoop;

	protected AbstractGlobalSearchStrategy(Solver solver) {
		this.solver = solver;
		limits = new LinkedList<AbstractGlobalSearchLimit>(); //always iterates over the list
		measures = new StrategyMeasures();
		traceStack = new IntBranchingTrace[solver.getNbBooleanVars() + solver.getNbIntVars() + solver.getNbSetVars()];
		nextMove = INIT_SEARCH;
	}

	public void initMainGoal(SConstraint c) {
		if (mainGoal != null) {
			mainGoal.initConstraintForBranching(c);
			AbstractBranching branch = mainGoal.getNextBranching();
			while(branch != null) {
				branch.initConstraintForBranching(c);
				branch = branch.getNextBranching();
			}
		}
	}

	public final void addLimit(AbstractGlobalSearchLimit limit) {
		limits.add(limit);
	}
	
	public final void resetLimits(boolean first) {
		for (AbstractGlobalSearchLimit limit : limits) {
			limit.reset(first);
		}
	}
	
	public final List<AbstractGlobalSearchLimit> getLimitsView() {
		return Collections.unmodifiableList(limits);
	}

	/*
	 * main entry point: searching for one solution
	 * Note: the initial propagation must be done before pushing any world level.
	 * It is therefore kept before restoring a solution
	 *
	 * @deprecated replaced by incrementalRun
	 */
	/*public void run() {
    boolean feasibleRootState = true;
    try {
      newTreeSearch();
      model.propagate();
    } catch (ContradictionException e) {
      feasibleRootState = false;
    }
    if (feasibleRootState) {
      model.worldPush();
      mainGoal.explore(1);
      model.worldPop();
      // explore is responsible for recordinf model.feasible = Boolean.TRUE in case a solution is found
      if (model.feasible == null) {
        model.feasible = Boolean.TRUE;
      } else if ((maxNbSolutionStored > 0) && (!stopAtFirstSol) && existsSolution()) {
        restoreBestSolution();
      }
    } else {
      model.feasible = Boolean.FALSE;
    }
    endTreeSearch();
  }*/

	/**
	 * main entry point: searching for one solution
	 * Note: the initial propagation must be done before pushing any world level.
	 * It is therefore kept before restoring a solution
	 */
	public void incrementalRun() {
		baseWorld = solver.getEnvironment().getWorldIndex();
		boolean feasibleRootState = true;
		try {
			newTreeSearch();
			//initializeDegreeOfVariables();
			solver.propagate();
		} catch (ContradictionException e) {
			feasibleRootState = false;
		}
		if (feasibleRootState) {
			solver.worldPush();
			if (stopAtFirstSol) {
				nextSolution();
			} else {
                //noinspection StatementWithEmptyBody
                while (nextSolution() == Boolean.TRUE){}
			}
			//model.worldPop();
			if ( existsStoredSolution() && (!stopAtFirstSol)) {
				solver.worldPopUntil(baseWorld);
				restoreBestSolution();
			}
			if (!isEncounteredLimit() && !existsSolution()) {
				solver.setFeasible(Boolean.FALSE);
			}
		} else {
			solver.setFeasible(Boolean.FALSE);
		}
		endTreeSearch();
	}


	/**
	 * called before a new search tree is explored
	 * @throws choco.kernel.solver.ContradictionException
	 */
	public void newTreeSearch() throws ContradictionException {
		assert(solver.getSearchStrategy() == this);
		resetSolutionCounter();
		baseWorld = solver.getEnvironment().getWorldIndex();
		resetLimits(true);
	}

	/**
	 * called before a new search tree is explored
	 */
	public void endTreeSearch() {
		resetLimits(false);
		if (LOGGER.isLoggable(Level.CONFIG)) {
			LOGGER.log(Level.CONFIG, "=== solve => {0} solutions\n\twith {1}", new Object[]{getSolutionCount(), runtimeStatistics()});
		}
	}

	/**
	 * called before a node is expanded in the search tree (choice point creation)
	 * @throws choco.kernel.solver.ContradictionException
	 */
	public void newTreeNode() throws ContradictionException {
		for (AbstractGlobalSearchLimit lim : limits) {
			if (!lim.newNode(this)) {
				encounteredLimit = lim;
				solver.getPropagationEngine().raiseContradiction(lim, ContradictionException.SEARCH_LIMIT);
			}
		}
	}

	/**
	 * called after a node is expanded in the search tree (choice point creation)
	 * @throws choco.kernel.solver.ContradictionException
	 */
	public void endTreeNode() throws ContradictionException {
		for (AbstractGlobalSearchLimit lim : limits) {
			if (!lim.endNode(this)) {//<hca> currentElement also the limit while going up to avoid useless propagation steps lim.endNode(this);
				encounteredLimit = lim;
				solver.getPropagationEngine().raiseContradiction(lim, ContradictionException.SEARCH_LIMIT);
			}
		}
	}


	public Boolean nextSolution() {
		//precondition for calling the search loop is that a limit has not been attempted
		//useful when solution are recorded from outside the OPEN_NODE case (hand-made)
		//in this case a limit and a solution could have been found at the same time
		if(this.isEncounteredLimit()) {
			return null;
		} else {return  searchLoop.run();}
	}

	
	
	@Override
	public void writeSolution(Solution sol) {
		super.writeSolution(sol);
		//record limits
		for (AbstractGlobalSearchLimit l : limits) {
			sol.recordLimit(l);
		}
	}

	/**
	 * called when a solution is encountered: printing and, if needed, storing the solution
	 */
	@Override
	public void recordSolution() {
		//Check wether every decisions variables have been instantiated
		if(solver.checkDecisionVariables()){
			super.recordSolution();
			if (LOGGER.isLoggable(Level.FINE)) {
				StringBuilder sb = new StringBuilder();
				sb.append("Solution #").append(getSolutionCount()).append(" is found");
				if  (this.limits.size() > 0) {
					sb.append("\n\twith ").append(runtimeStatistics());
				}
				LOGGER.log(Level.FINE, "=== {0}",sb);	
				if (LOGGER.isLoggable(Level.FINER)) {LOGGER.log(Level.FINER,"\t{0}", solver.solutionToString());}
			}
		}else{
			throw new SolverException("Bug in solution :one or more decisions variables is not instantiated");
		}
	}


	/**
	 * called before going down into each branch of the choice point
	 * @throws choco.kernel.solver.ContradictionException
	 */
	public void postDynamicCut() throws ContradictionException {
	}

	
	public final IntBranchingTrace pushTrace() {
		currentTraceIndex++;
		if (currentTraceIndex >= traceStack.length) {
			//ensure capacity
			 int newCapacity = (traceStack.length * 3)/2 + 1;
			 IntBranchingTrace[] tmp = new IntBranchingTrace[newCapacity];
			 System.arraycopy(traceStack, 0, tmp, 0, traceStack.length);
			 traceStack = tmp;
			 traceStack[currentTraceIndex] = new IntBranchingTrace(); //create trace 
		}else if (traceStack[currentTraceIndex] == null) {
			traceStack[currentTraceIndex] = new IntBranchingTrace();  //create trace
		}else {
			traceStack[currentTraceIndex].clear(); //reset old trace
		}
		 return traceStack[currentTraceIndex];
	}

	public final boolean isTraceEmpty() {
		return currentTraceIndex < 0;
	}
	
	public final IntBranchingTrace getTrace(int index) {
		return traceStack[index];
	}
	
	public final int getTraceSize() {
		return currentTraceIndex+1;
	}
	
	public final IntBranchingTrace popTrace() {
		if (currentTraceIndex <= 0) {
			currentTraceIndex = -1;
			return null;
		} else {
			currentTraceIndex--;
			return traceStack[currentTraceIndex];
		}
	}

	public final IntBranchingTrace topTrace() {
		return isTraceEmpty() ? null : traceStack[currentTraceIndex];
	}
	
	public final void clearTrace() {
		currentTraceIndex = -1;
	}
	
//	public final void popTraceUntil(int targetWorld) {
//		clearTrace();
//
//		/*int deltaWorld = (solver.getEnvironment().getWorldIndex() - targetWorld);
//    if (deltaWorld > 0) {
//      if (currentTraceIndex - deltaWorld < -1)
//        LOGGER.severe("bizarre");
//      currentTraceIndex = currentTraceIndex - deltaWorld;
//    }*/
//	}



	/**
	 * Print all statistics
	 */
	public void printRuntimeStatistics() {
		if(LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info(runtimeStatistics());
		}
	}

	public String runtimeStatistics() {
		final StringBuilder stb = new StringBuilder();
		for (AbstractGlobalSearchLimit l : limits) {
			stb.append(l.pretty()).append(" ; ");
		}
		return stb.toString();
	}

	public AbstractGlobalSearchLimit getLimit(Limit limit) {
		return AbstractGlobalSearchLimit.getLimit(limits, limit);
	}



	public final ISearchMeasures getSearchMeasures() {
		return measures;
	}
	
	/**
	 * @return the time elapsed during the last search in milliseconds
	 * (-1 if time was neither measured nor bounded)
	 */
	@Deprecated
	public int getTimeCount() {
		return measures.getTimeCount();
	}

	/**
	 * @return the CPU time elapsed during the last search in milliseconds
	 * (-1 if time was neither measured nor bounded)
	 */
	@Deprecated
	public int getCpuTimeCount() {
		return measures.getTimeCount();
	}

	/**
	 * @return the number of nodes of the tree search (including the root node where
	 * initial propagation has been performed and saved)
	 */
	@Deprecated
	public int getNodeCount() {
		return measures.getNodeCount();
	}

	/**
	 * @return the number of backtracks of the tree search
	 */
	@Deprecated
	public int getBackTrackCount() {
		return measures.getBackTrackCount();
	}

	/**
	 * @return the number of fails of the tree search
	 */
	@Deprecated
	public int getFailCount() {
		return measures.getFailCount();
	}

	/**
	 * Checks if a limit has been encountered
	 *
	 * @return true if a limit has been reached
	 */
	public boolean isEncounteredLimit() {
		return encounteredLimit != null;
	}

	/**
	 * If a limit has been encounteres, return the involved limit
	 * @return the encoutered limit
	 */
	public GlobalSearchLimit getEncounteredLimit() {
		return encounteredLimit;
	}

	public void setSearchLoop(ISearchLoop searchLoop) {
		this.searchLoop = searchLoop;
	}

	public void setEncounteredLimit(GlobalSearchLimit encounteredLimit) {
		this.encounteredLimit = encounteredLimit;
	}
	
	
	private final class StrategyMeasures extends AbstractMeasures {

		@Override
		protected Collection<AbstractGlobalSearchLimit> getLimits() {
			return getLimitsView();
		}	
	}
	
}
