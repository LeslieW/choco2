// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.branch;

import i_want_to_use_this_old_version_of_choco.ContradictionException;

import java.util.logging.Level;

/**
 * An abstract class for all implementations of branching objets (objects controlling the tree search)
 */
public abstract class AbstractIntBranching extends AbstractBranching implements IntBranching {

  public void goDownBranch(Object x, int i) throws ContradictionException {
    logDownBranch(x, i);
  }

  public void goUpBranch(Object x, int i) throws ContradictionException {
    logUpBranch(x, i);
  }

  protected void logDownBranch(final Object x, final int i) {
    if (logger.isLoggable(Level.FINE)) {
      int n = manager.problem.getEnvironment().getWorldIndex();
      if (n <= manager.getLoggingMaxDepth()) {
        logger.log(Level.FINE, LOG_DOWN_MSG, new Object[]{new Integer(n),x,getDecisionLogMsg(i), new Integer(i)});
      }
    }
  }
  
  protected void logUpBranch(final Object x, final int i) {
    if (logger.isLoggable(Level.FINE)) {
      int n = manager.problem.getEnvironment().getWorldIndex();
      if (n <= manager.getLoggingMaxDepth()) {
        logger.log(Level.FINE, LOG_UP_MSG, new Object[]{new Integer(n + 1),x,getDecisionLogMsg(i), new Integer(i)});
      }
    }
  }
}
