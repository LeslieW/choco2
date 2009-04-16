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
package choco.kernel.solver.branch;

import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.constraints.SConstraint;

import java.util.logging.Logger;

public abstract class AbstractBranching {
  /**
   * the main control object (responsible for the whole exploration, while the branching object
   * is responsible only at the choice point level
   */
  protected AbstractGlobalSearchStrategy manager;
  /**
   * a link towards the next branching object (once this one is exhausted)
   */
  protected AbstractBranching nextBranching;
  
  

  public final static String LOG_DOWN_MSG = "down branch ";
  public final static String LOG_UP_MSG = "up branch ";
  public String[] LOG_DECISION_MSG = {""};

  public void setSolver(AbstractGlobalSearchStrategy s) {
    manager = s;
  }

  /**
   * Gets the next branching.
   * @return the next branching
   */
  public AbstractBranching getNextBranching() {
    return nextBranching;
  }

  /**
   * Sets the next branching.
   * @param nextBranching the next branching
   */
  public void setNextBranching(AbstractBranching nextBranching) {
    this.nextBranching = nextBranching;
  }

  /**
   * used for logging messages related to the search tree
   * @param branchIndex is the index of the branching
   * @return an string that will be printed between the branching object and the branch index
   * Suggested implementations return LOG_DECISION_MSG[0] or LOG_DECISION_MSG[branchIndex]
   */
  public abstract String getDecisionLogMsg(int branchIndex);

  /**
   * This method is called before launching the search. it may be used to intialiaze data structures or counters for
   * instance.
   */
  public void initBranching() {
    // Nothing to do by default
  }

    /**
     * this method is used to build the data structure in the branching for
     * the given constraint. This is used when the constraint was not present
     * at the initialization of the branching, for example a cut 
     * @param s
     */
  public void initConstraintForBranching(SConstraint c) {
    //nothing to do by default
  }
}
