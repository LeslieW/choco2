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

import choco.kernel.solver.ContradictionException;

import java.security.UnresolvedPermission;
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

  protected Object getVariableLogParameter(final Object x) {
	  return x;
  }
  
  protected Object getValueLogParameter(final Object x, final int branch) {
	  return Integer.valueOf(branch);
  }
  
  protected final String getDefaultLogMessage() {
	  return "{1} {2} {3} {4}";
  }
  
  protected final String getLogMessageWithBranch() {
	  return "{1} {2} {3} {4} branch {5}";
  }
  
  /**
   * a log message using java.util logging {} arguments </br>
   * {0}: world index (formatter arguments)</br>
   * {1}: Up or Down message </br>
   * {2}: Branching var </br>
   * {3}: decision msg </br>
   * {4}: Branching val </br>
   * {5}: Branch index </br>
   * 
   * @return
   */
  protected String getLogMessage() {
	  return getDefaultLogMessage();
  }
  
  protected final void logDownBranch(final Object x, final int i) {
    if (LOGGER.isLoggable(Level.INFO)) {
      final int n = manager.solver.getEnvironment().getWorldIndex();
      if (n <= manager.getLoggingMaxDepth()) {
        LOGGER.log(Level.INFO, getLogMessage(), new Object[]{n, LOG_DOWN_MSG, getVariableLogParameter(x), getDecisionLogMsg(i), getValueLogParameter(x,i), Integer.valueOf(i)});
      }
    }
  }
  
  protected final void logUpBranch(final Object x, final int i) {
    if (LOGGER.isLoggable(Level.INFO)) {
      int n = manager.solver.getEnvironment().getWorldIndex();
      if (n <= manager.getLoggingMaxDepth()) {
    	  LOGGER.log(Level.INFO, getLogMessage(), new Object[]{n + 1, LOG_UP_MSG, getVariableLogParameter(x), getDecisionLogMsg(i), getValueLogParameter(x,i), Integer.valueOf(i)});
      }
    }
  }
}
