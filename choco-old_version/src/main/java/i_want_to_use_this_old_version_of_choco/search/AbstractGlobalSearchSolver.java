// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.AbstractSolver;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.AbstractIntBranching;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * An abstract class for controlling tree search in various ways
 */
public abstract class AbstractGlobalSearchSolver extends AbstractSolver {

  /**
   * constants for dirving the incremental search algorithm
   */
  public static final int INIT_SEARCH = -1;
  public static final int OPEN_NODE = 0;
  public static final int UP_BRANCH = 1;
  public static final int DOWN_BRANCH = 2;

  /**
   * a data structure storing the stack of choice contexts (for incremental search explorations)
   */
  public ArrayList traceStack = null;

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
   */
//  public int nbBacktracks = 0;


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
//  public AbstractGlobalSearchLimit[] limits;
  public ArrayList limits;


  public ISearchLoop searchLoop;

  protected AbstractGlobalSearchSolver(AbstractProblem pb) {
    problem = pb;
    searchLoop = new SearchLoop(this);
//    limits = new AbstractGlobalSearchLimit[0];
    limits = new ArrayList();
    traceStack = new ArrayList();
    currentTraceIndex = -1;
    nextMove = INIT_SEARCH;
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
      problem.propagate();
    } catch (ContradictionException e) {
      feasibleRootState = false;
    }
    if (feasibleRootState) {
      problem.worldPush();
      mainGoal.explore(1);
      problem.worldPop();
      // explore is responsible for recordinf problem.feasible = Boolean.TRUE in case a solution is found
      if (problem.feasible == null) {
        problem.feasible = Boolean.TRUE;
      } else if ((maxNbSolutionStored > 0) && (!stopAtFirstSol) && existsSolution()) {
        restoreBestSolution();
      }
    } else {
      problem.feasible = Boolean.FALSE;
    }
    endTreeSearch();
  }*/

  /**
   * main entry point: searching for one solution
   * Note: the initial propagation must be done before pushing any world level.
   * It is therefore kept before restoring a solution
   */
  public void incrementalRun() {
    baseWorld = problem.getEnvironment().getWorldIndex();
    boolean feasibleRootState = true;
    try {
      newTreeSearch();
      problem.propagate();
    } catch (ContradictionException e) {
      feasibleRootState = false;
    }
    if (feasibleRootState) {
      problem.worldPush();
      if (stopAtFirstSol) {
        nextSolution();
      } else {
        while (nextSolution() == Boolean.TRUE) {
          ;
        }
      }
      //problem.worldPop();
      if ((maxNbSolutionStored > 0) && (!stopAtFirstSol) && existsSolution()) {
        problem.worldPopUntil(baseWorld);
        restoreBestSolution();
      }
      if (!isEncounteredLimit() && !existsSolution()) {
         problem.feasible = Boolean.FALSE;
      }
    } else {
      problem.feasible = Boolean.FALSE;
    }
    endTreeSearch();
  }


  /**
   * called before a new search tree is explored
   */
  public void newTreeSearch() throws ContradictionException {
    assert(problem.getSolver().getSearchSolver() == this);
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(true);
    }
    nbSolutions = 0;
    baseWorld = problem.getEnvironment().getWorldIndex();
  }

  /**
   * called before a new search tree is explored
   */
  public void endTreeSearch() {
    if (logger.isLoggable(Level.SEVERE)) {
      if (problem.feasible == Boolean.TRUE) {
        logger.log(Level.INFO, "solve => " + new Integer(nbSolutions) + " solutions");
      } else {
        logger.info("solve => no solution");
      }
      for (int i = 0; i < limits.size(); i++) {
        AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
        logger.info(lim.pretty());
      }
    }
  }

  /**
   * called before a node is expanded in the search tree (choice point creation)
   */
  public void newTreeNode() throws ContradictionException {
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      if (lim.newNode(this) == false) {
        encounteredLimit = lim;
        problem.getPropagationEngine().raiseContradiction(lim);
      }
    }
  }

  /**
   * called after a node is expanded in the search tree (choice point creation)
   */
  public void endTreeNode() throws ContradictionException {
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      if (lim.endNode(this) == false) {//<hca> currentElement also the limit while going up to avoid useless propagation steps lim.endNode(this);
         encounteredLimit = lim;
         problem.getPropagationEngine().raiseContradiction(lim);
      }
    }
  }


  public Boolean nextSolution() {
     return  searchLoop.run();
  }

  /**
   * called when a solution is encountered: printing and, if needed, storing the solution
   */
  public void recordSolution() {
    problem.feasible = Boolean.TRUE;
    nbSolutions = nbSolutions + 1;
    if (logger.isLoggable(Level.FINE)) {
        logger.fine("Solution #" + nbSolutions + " is found");
        if  ((logger.isLoggable(Level.FINER)) && (this.limits.size() > 0)) {
          StringBuffer sb = new StringBuffer("  with ");
          for (int i=0; i<limits.size(); i++) {
            AbstractGlobalSearchLimit l = (AbstractGlobalSearchLimit) limits.get(i);
            sb.append(l.pretty());
            sb.append("; ");
          }
          logger.finer(sb.toString());
        }
        if  (logger.isLoggable(Level.FINEST)) {
          logger.finest("  " + problem.solutionToString());
        }
    }
    if (maxNbSolutionStored > 0) {
      super.recordSolution();
    }
  }

  /**
   * called before going down into each branch of the choice point
   */
  public void postDynamicCut() throws ContradictionException {
  }


  public IntBranchingTrace pushTrace() {
    currentTraceIndex++;
    IntBranchingTrace nextTrace = null;
    if (currentTraceIndex > traceStack.size() - 1) {
      nextTrace = new IntBranchingTrace();
      traceStack.add(nextTrace);
    } else {
      nextTrace = (IntBranchingTrace) traceStack.get(currentTraceIndex);
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
      return (IntBranchingTrace) traceStack.get(currentTraceIndex);
    }
  }

  public IntBranchingTrace topTrace() {
    if (currentTraceIndex < 0) {
      return null;
    } else {
      return (IntBranchingTrace) traceStack.get(currentTraceIndex);
    }
  }

  public void popTraceUntil(int targetWorld) {
    int deltaWorld = (problem.getEnvironment().getWorldIndex() - targetWorld);
    if (deltaWorld > 0) {
      if (currentTraceIndex - deltaWorld < -1)
        System.err.println("bizarre");
      currentTraceIndex = currentTraceIndex - deltaWorld;
    }
  }



  /**
   * Print all statistics
   */
  public void printRuntimeStatistics() {
    for (int i = 0; i < limits.size(); i++) {
        System.out.println((((AbstractGlobalSearchLimit) limits.get(i)).pretty()));
    }
  }

  /**
   * @return the time elapsed during the last search in milliseconds
   * (-1 if time was neither measured nor bounded)
   */
 public int getTimeCount() {
   for (int i = 0; i < limits.size(); i++) {
     AbstractGlobalSearchLimit l = (AbstractGlobalSearchLimit) limits.get(i);
     if (l instanceof TimeLimit) {
       return ((TimeLimit) l).getNbTot() + ((TimeLimit) l).getNb();
     }
   }
   return -1;
 }

  /**
   * @return the number of nodes of the tree search (including the root node where
   * initial propagation has been performed and saved)
   */
 public int getNodeCount() {
   for (int i = 0; i < limits.size(); i++) {
     AbstractGlobalSearchLimit l = (AbstractGlobalSearchLimit) limits.get(i);
     if (l instanceof NodeLimit) {
       return ((NodeLimit) l).getNbTot() + ((NodeLimit) l).getNb();
     }
   }
   return -1;
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
   */
  public GlobalSearchLimit getEncounteredLimit() {
    return encounteredLimit;
  }

  public void setSearchLoop(ISearchLoop searchLoop) {
      this.searchLoop = searchLoop;
  }
}
