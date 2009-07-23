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

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.branch.AbstractBranching;
import choco.kernel.solver.branch.AbstractIntBranching;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.search.limit.AbstractLimitManager;
import choco.kernel.solver.search.measures.ISearchMeasures;

import java.util.logging.Level;

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
	public static final int RESTART = 1 << 3;
	public static final int STOP = 1 << 4;

	/**
	 * index of the current trace in the stack
	 */
	protected int currentTraceIndex = -1;
	
	/**
	 * a data structure storing the stack of choice contexts (for incremental search explorations)
	 */
	protected IntBranchingTrace[] traceStack;
	
	/**
	 * a reusable trace object to start the branching from the root node.
	 */
	private final IntBranchingTrace initialTrace = new IntBranchingTrace();

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

	public AbstractLimitManager limitManager;
	
	public ISearchLoop searchLoop;

	protected AbstractGlobalSearchStrategy(Solver solver) {
		this.solver = solver;
		traceStack = new IntBranchingTrace[solver.getNbIntVars() + solver.getNbSetVars()];
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
		

	public final AbstractLimitManager getLimitManager() {
		return limitManager;
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
		baseWorld = solver.getWorldIndex();
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
			if (  ! solutionPool.isEmpty() && (!stopAtFirstSol)) {
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
        resetSolutions();
		baseWorld = solver.getWorldIndex();
		initialTrace.setBranching(this.mainGoal);
		limitManager.initialize();
	}

	/**
	 * called before a new search tree is explored
	 */
	public void endTreeSearch() {
		limitManager.reset();
		if (LOGGER.isLoggable(Level.CONFIG)) {
			LOGGER.log(Level.CONFIG, "=== solve => {0} solutions\n\twith {1}", new Object[]{getSolutionCount(), runtimeStatistics()});
		}
	}

	/**
	 * called before a node is expanded in the search tree (choice point creation)
	 * @throws choco.kernel.solver.ContradictionException
	 */
	public void newTreeNode() throws ContradictionException {
		limitManager.newNode();
	}

	/**
	 * called after a node is expanded in the search tree (choice point creation)
	 * @throws choco.kernel.solver.ContradictionException
	 */
	public void endTreeNode() throws ContradictionException {
		limitManager.endNode();
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
		limitManager.writeLimits(sol);
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
				if  (limitManager.getNbLimits() > 0) {
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
	
	public final int getCurrentTraceIndex() {
		return currentTraceIndex;
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
	
	
	public final IntBranchingTrace initialTrace() {
		return isTraceEmpty() ? initialTrace : traceStack[currentTraceIndex];
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
		return limitManager.pretty();
	}

	

	public final ISearchMeasures getSearchMeasures() {
		return limitManager;
	}
	
	/**
	 * @return the time elapsed during the last search in milliseconds
	 * (-1 if time was neither measured nor bounded)
	 */
	@Deprecated
	public int getTimeCount() {
		return limitManager.getTimeCount();
	}

	/**
	 * @return the CPU time elapsed during the last search in milliseconds
	 * (-1 if time was neither measured nor bounded)
	 */
	@Deprecated
	public int getCpuTimeCount() {
		return limitManager.getTimeCount();
	}

	/**
	 * @return the number of nodes of the tree search (including the root node where
	 * initial propagation has been performed and saved)
	 */
	@Deprecated
	public int getNodeCount() {
		return limitManager.getNodeCount();
	}

	/**
	 * @return the number of backtracks of the tree search
	 */
	@Deprecated
	public int getBackTrackCount() {
		return limitManager.getBackTrackCount();
	}

	/**
	 * @return the number of fails of the tree search
	 */
	@Deprecated
	public int getFailCount() {
		return limitManager.getFailCount();
	}

	/**
	 * Checks if a limit has been encountered
	 *
	 * @return true if a limit has been reached
	 */
	public final boolean isEncounteredLimit() {
		return encounteredLimit != null;
	}

	/**
	 * If a limit has been encounteres, return the involved limit
	 * @return the encoutered limit
	 */
	public final GlobalSearchLimit getEncounteredLimit() {
		return encounteredLimit;
	}

	
	public final void setLimitManager(AbstractLimitManager limitManager) {
		this.limitManager = limitManager;
		limitManager.setSearchStrategy(this);
	}

	public final void setSearchLoop(ISearchLoop searchLoop) {
		this.searchLoop = searchLoop;
	}

	public final void setEncounteredLimit(GlobalSearchLimit encounteredLimit) {
		this.encounteredLimit = encounteredLimit;
	}
	
	
	
}
