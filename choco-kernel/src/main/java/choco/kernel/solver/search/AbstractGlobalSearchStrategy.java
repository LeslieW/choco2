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
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.branch.AbstractBranching;
import choco.kernel.solver.branch.AbstractIntBranching;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.Collection;
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

	/**
	 * a data structure storing the stack of choice contexts (for incremental search explorations)
	 */
	public ArrayList<IntBranchingTrace> traceStack = null;

	/**
	 * index of the current trace in the stack
	 */
	public int currentTraceIndex = -1;

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
	 * count of the solutions found during search
	 */
	public int nbSolutions = 0;

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
	//	public AbstractGlobalSearchLimit[] limits;
	public ArrayList<AbstractGlobalSearchLimit> limits;

	public Collection<AbstractGlobalSearchLimit> solutionLimits;

	public ISearchLoop searchLoop;

	protected AbstractGlobalSearchStrategy(Solver solver) {
		this.solver = solver;
		limits = new ArrayList<AbstractGlobalSearchLimit>();
		traceStack = new ArrayList<IntBranchingTrace>();
		currentTraceIndex = -1;
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

	public void initializeDegreeOfVariables() {
		for (int i = 0; i < solver.getNbIntVars(); i++) {
			IntDomainVar v = (IntDomainVar) solver.getIntVar(i);
			if (v.isInstantiated()) {
				v.updateNbVarInstanciated();
			}
		}
	}

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
				while (nextSolution() == Boolean.TRUE) {
					;
				}
			}
			//model.worldPop();
			if ((maxNbSolutionStored > 0) && (!stopAtFirstSol) && existsSolution()) {
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
		for (AbstractGlobalSearchLimit lim : limits) {
			lim.reset(true);
		}
		nbSolutions = 0;
		solutionLimits = null;
		baseWorld = solver.getEnvironment().getWorldIndex();
	}

	/**
	 * called before a new search tree is explored
	 */
	public void endTreeSearch() {
		if (LOGGER.isLoggable(Level.INFO)) {
			//TODO display objective if any and makespan
			LOGGER.log(Level.INFO, "solve => {1} solutions\n\twith {2}", new Object[]{-1, Integer.valueOf(nbSolutions), runtimeStatistics()});
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

	/**
	 * called when a solution is encountered: printing and, if needed, storing the solution
	 */
	@Override
	public void recordSolution() {
		//Check wether every decisions variables have been instantiated
		if(solver.checkDecisionVariables()){
			solver.setFeasible(Boolean.TRUE);
			nbSolutions = nbSolutions + 1;
			if (LOGGER.isLoggable(Level.FINE)) {
				StringBuilder sb = new StringBuilder();
				sb.append("Solution #").append(nbSolutions).append(" is found");
				if  (this.limits.size() > 0) {
					sb.append("\n\twith ").append(runtimeStatistics());
				}
				if  (LOGGER.isLoggable(Level.FINEST)) {sb.append("\n\t").append(solver.solutionToString());}
				LOGGER.log(Level.FINE, new String(sb), -1 );
			}
			if (maxNbSolutionStored > 0) {
				super.recordSolution();
			}
		}else{
			throw new SolverException("Bug in solution :one or more decisions variables is not instantiated");
		}
	}



	@Override
	public void restoreBestSolution() {
		super.restoreBestSolution();
		solutionLimits = getBestSolution().getLimits();
	}

	/**
	 * called before going down into each branch of the choice point
	 * @throws choco.kernel.solver.ContradictionException
	 */
	public void postDynamicCut() throws ContradictionException {
	}


	public IntBranchingTrace pushTrace() {
		currentTraceIndex++;
		IntBranchingTrace nextTrace;
		if (currentTraceIndex > traceStack.size() - 1) {
			nextTrace = new IntBranchingTrace();
			traceStack.add(nextTrace);
		} else {
			nextTrace = traceStack.get(currentTraceIndex);
			nextTrace.clear();
		}
		return nextTrace;
	}

	public IntBranchingTrace popTrace() {
		if (currentTraceIndex <= 0) {
			currentTraceIndex = -1;
			return null;
		} else {
			currentTraceIndex--;
			return traceStack.get(currentTraceIndex);
		}
	}

	public IntBranchingTrace topTrace() {
		if (currentTraceIndex < 0) {
			return null;
		} else {
			return traceStack.get(currentTraceIndex);
		}
	}

	public void popTraceUntil(int targetWorld) {
		while (currentTraceIndex > targetWorld) {
			popTrace();
		}

		/*int deltaWorld = (solver.getEnvironment().getWorldIndex() - targetWorld);
    if (deltaWorld > 0) {
      if (currentTraceIndex - deltaWorld < -1)
        System.err.println("bizarre");
      currentTraceIndex = currentTraceIndex - deltaWorld;
    }*/
	}



	/**
	 * Print all statistics
	 */
	public void printRuntimeStatistics() {
		System.out.print(runtimeStatistics());
	}

	public String runtimeStatistics() {
		final StringBuilder stb = new StringBuilder();
		for (AbstractGlobalSearchLimit l : limits) {
			stb.append(l.pretty()).append(" ; ");
		}
		return stb.toString();
	}


	public final AbstractGlobalSearchLimit getLimit(Limit limit) {
		for (AbstractGlobalSearchLimit l : limits) {
			if (l.getType().getProperty().equals(limit.getProperty())) {
				return l;
			}
		}
		return null;
	}

	private int getNb(AbstractGlobalSearchLimit limit) {
		return limit==null ? -1 : limit.getNbAll();
	}


	/**
	 * @return the time elapsed during the last search in milliseconds
	 * (-1 if time was neither measured nor bounded)
	 */
	public int getTimeCount() {
		return getNb(getLimit(Limit.TIME));
	}

	/**
	 * @return the CPU time elapsed during the last search in milliseconds
	 * (-1 if time was neither measured nor bounded)
	 */
	public int getCpuTimeCount() {
		return getNb(getLimit(Limit.CPU_TIME));
	}

	/**
	 * @return the number of nodes of the tree search (including the root node where
	 * initial propagation has been performed and saved)
	 */
	public int getNodeCount() {
		return getNb(getLimit(Limit.NODE));
	}

	/**
	 * @return the number of backtracks of the tree search
	 */
	public int getBackTrackCount() {
		return getNb(getLimit(Limit.BACKTRACK));
	}

	/**
	 * @return the number of fails of the tree search
	 */
	public int getFailCount() {
		return getNb(getLimit(Limit.FAIL));
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
}
