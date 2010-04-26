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

import static choco.kernel.common.util.tools.StringUtils.pretty;
import static choco.kernel.common.util.tools.StringUtils.prettyOnePerLine;

import java.util.logging.Level;

import choco.Options;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.AbstractBranchingStrategy;
import choco.kernel.solver.branch.AbstractIntBranchingStrategy;
import choco.kernel.solver.configure.StrategyConfiguration;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.ShavingTools;
import choco.kernel.solver.search.limit.AbstractGlobalSearchLimit;
import choco.kernel.solver.search.measure.ISearchMeasures;

/**
 * An abstract class for controlling tree search in various ways
 * version 2.0.3 : change the value of search constant to use bit masks.
 */
public abstract class AbstractGlobalSearchStrategy extends AbstractSearchStrategy implements ISearchMeasures {

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
	protected AbstractGlobalSearchLimit encounteredLimit = null;

	/**
	 * 
	 * the goal that needs be solved to drive the exploration
	 */
	public AbstractIntBranchingStrategy mainGoal;

	/**
	 * the index of the world where the search started
	 */
	public int baseWorld = 0;

	public StrategyConfiguration configuration;

	public ShavingTools shavingTools;
	
	public GlobalSearchLimitManager limitManager;

	public AbstractSearchLoop searchLoop;

	protected AbstractGlobalSearchStrategy(Solver solver) {
		this.solver = solver;
		traceStack = new IntBranchingTrace[solver.getNbIntVars() + solver.getNbSetVars()];
		nextMove = INIT_SEARCH;
	}


	public void initMainGoal(SConstraint c) {
		if (mainGoal != null) {
			mainGoal.initConstraintForBranching(c);
			AbstractBranchingStrategy branch = mainGoal.getNextBranching();
			while(branch != null) {
				branch.initConstraintForBranching(c);
				branch = branch.getNextBranching();
			}
		}
	}

	public IObjectiveManager getObjectiveManager() {
		return null;
	}
	
	public final StrategyConfiguration getConfiguration() {
		return configuration;
	}

	public final void setConfiguration(StrategyConfiguration configuration) {
		this.configuration = configuration;
	}


	public final GlobalSearchLimitManager getLimitManager() {
		return limitManager;
	}


	public final AbstractSearchLoop getSearchLoop() {
		return searchLoop;
	}


	public final ShavingTools getShavingTools() {
		return shavingTools;
	}


	public final void setShavingTools(ShavingTools shavingTools) {
		this.shavingTools = shavingTools;
	}


	public final void setSearchLoop(AbstractSearchLoop searchLoop) {
		this.searchLoop = searchLoop;
	}

	public final void setLimitManager(GlobalSearchLimitManager limitManager) {
		this.limitManager = limitManager;
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

	protected final boolean isFeasibleRootState() {
		return solver.isFeasible() != Boolean.FALSE;
	}

	public final void initialPropagation() {
		try {
			newTreeSearch();
			//initializeDegreeOfVariables();
			solver.propagate();
			advancedInitialPropagation();
			newFeasibleRootState();
		} catch (ContradictionException e) {
			solver.setFeasible(Boolean.FALSE);
		}
	}


	protected void advancedInitialPropagation() throws ContradictionException {
		if( solver.containsOption(Options.S_ROOT_SHAVING) ) shavingTools.shaving();
	}


	protected final void topDownSearch() {
		if (stopAtFirstSol) {
			nextSolution();
		} else {
			//noinspection StatementWithEmptyBody
			while (nextSolution() == Boolean.TRUE){}
		}
	}
	/**
	 * main entry point: searching for one solution
	 * Note: the initial propagation must be done before pushing any world level.
	 * It is therefore kept before restoring a solution
	 */
	public void incrementalRun() {
		//		baseWorld = solver.getWorldIndex();
		//		boolean feasibleRootState = true;
		//		try {
		//			newTreeSearch();
		//			//initializeDegreeOfVariables();
		//			solver.propagate();
		//		} catch (ContradictionException e) {
		//			feasibleRootState = false;
		//		}
		//		if (feasibleRootState) {
		//			newFeasibleRootState();
		//			if (stopAtFirstSol) {
		//				nextSolution();
		//			} else {
		//				//noinspection StatementWithEmptyBody
		//				while (nextSolution() == Boolean.TRUE){}
		//			}
		//			if (  ! solutionPool.isEmpty() && (!stopAtFirstSol)) {
		//				solver.worldPopUntil(baseWorld);
		//				restoreBestSolution();
		//			}
		//			if (!isEncounteredLimit() && !existsSolution()) {
		//				solver.setFeasible(Boolean.FALSE);
		//			}
		//		} else {
		//			solver.setFeasible(Boolean.FALSE);
		//		}
		initialPropagation();
		if(isFeasibleRootState()) {
			//System.out.println(solver.getWorldIndex() +">"+ baseWorld);
			assert(solver.getWorldIndex() > baseWorld);
			topDownSearch();
		}
		endTreeSearch();
	}



	public final static boolean isUsingShavingTools(Solver solver) {
		return solver.containsOption(Options.S_ROOT_SHAVING) ||
		solver.containsOption(Options.S_DESTRUCTIVE_LOWER_BOUND) ||
		solver.containsOption(Options.S_BOTTOM_UP);
	}
	
	/**
	 * called before a new search tree is explored
	 * @throws choco.kernel.solver.ContradictionException
	 */
	public void newTreeSearch() throws ContradictionException {
		assert(solver.getSearchStrategy() == this);
		LOGGER.info(ChocoLogging.START_MESSAGE);
		baseWorld = solver.getWorldIndex();
		resetSolutions();
		if( isUsingShavingTools(solver) && shavingTools == null) shavingTools = new ShavingTools(solver); 
		initialTrace.setBranching(this.mainGoal);
		limitManager.initialize();
		searchLoop.initialize();
		solver.getFailMeasure().safeReset();
	}

	/**
	 * called when the root state of the search tree is feasible. 
	 */
	public void newFeasibleRootState() {
		solver.worldPush();
	}

	
	public void endTreeSearch() {
		if ( ! solutionPool.isEmpty() && (!stopAtFirstSol)) {
			solver.worldPopUntil(baseWorld);
			restoreBestSolution();
		}
		if (!isEncounteredLimit() && !existsSolution()) {
			solver.setFeasible(Boolean.FALSE);
		}
		limitManager.endTreeSearch();
		if (LOGGER.isLoggable(Level.INFO)) {
			if( isEncounteredLimit() ) {
				LOGGER.log(Level.INFO, "- Search incompleted: Exiting on limit reached\n  Limit: {0}\n{1}", 
						new Object[]{limitManager.toString(), runtimeStatistics()});
			}else if( solver.isFeasible() == Boolean.TRUE) {
				LOGGER.log(Level.INFO, "- Search completed\n{0}",runtimeStatistics());
			}else if( solver.isFeasible() == Boolean.FALSE) {
				LOGGER.log(Level.INFO, "- Search completed: No solutions\n{0}",runtimeStatistics());
			}else {
				LOGGER.log(Level.INFO, "- Search stopped ?\n{1}", runtimeStatistics()); 
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
		sol.recordSearchMeasures(this);
	}


	/**
	 * called when a solution is encountered: printing and, if needed, storing the solution
	 */
	@Override
	public void recordSolution() {
		//Check wether every decisions variables have been instantiated
		assert(solver.checkSolution());
		super.recordSolution();
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.log(Level.FINE, "- Solution #{0} found. {1}.",
					new Object[]{getSolutionCount(),partialRuntimeStatistics(true)}
			);	
			if (LOGGER.isLoggable(Level.FINER)) {
				LOGGER.log(Level.FINER,"  {0}", solver.solutionToString());
			}
		}
	}


	/**
	 * called before going down into each branch of the choice point
	 * @throws choco.kernel.solver.ContradictionException
	 */
	public void postDynamicCut() throws ContradictionException {}


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
			//is it really useful as we overwrite the trace ?
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

	/**
	 * @returns reusable trace object to start or restart the search.
	 */
	public final IntBranchingTrace getReusableInitialTrace() {
		return initialTrace;
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




	public String runtimeStatistics() {
		return "  Solutions: "+getSolutionCount() + "\n"+prettyOnePerLine(this);
	}

	public String partialRuntimeStatistics(boolean logOnSolution) {
		return logOnSolution ? 
				getSolutionCount()+" Solutions, "+pretty(this) :
					pretty(this);
	}



	/**
	 * @return the time elapsed during the last search in milliseconds
	 */
	@Override
	public int getTimeCount() {
		return limitManager.getTimeCount();
	}



	/**
	 * @return the number of nodes of the tree search (including the root node where
	 * initial propagation has been performed and saved)
	 */
	@Override
	public int getNodeCount() {
		return searchLoop.getNodeCount();
	}

	/**
	 * @return the number of backtracks of the tree search
	 */
	@Override
	public int getBackTrackCount() {
		return searchLoop.getBacktrackCount();
	}


	@Override
	public int getRestartCount() {
		return searchLoop.getRestartCount();
	}

	/**
	 * @return the number of fails of the tree search
	 */
	@Override
	public int getFailCount() {
		return solver.getFailMeasure().getFailCount();
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
	public final AbstractGlobalSearchLimit getEncounteredLimit() {
		return encounteredLimit;
	}



	public final void setEncounteredLimit(AbstractGlobalSearchLimit encounteredLimit) {
		this.encounteredLimit = encounteredLimit;
	}



}
