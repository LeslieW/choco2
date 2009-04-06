// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.branch;

import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchSolver;

import java.util.logging.Logger;

public abstract class AbstractBranching {
  /**
   * the main control object (responsible for the whole exploration, while the branching object
   * is responsible only at the choice point level
   */
  protected AbstractGlobalSearchSolver manager;
  /**
   * a link towards the next branching object (once this one is exhausted)
   */
  protected AbstractBranching nextBranching;
  /**
   * an object for logging trace statements
   */
  protected static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search.branching");

  public static String LOG_DOWN_MSG = "down branch ";
  public static String LOG_UP_MSG = "up branch ";
  public String[] LOG_DECISION_MSG = {""};

  public void setSolver(AbstractGlobalSearchSolver s) {
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
}
