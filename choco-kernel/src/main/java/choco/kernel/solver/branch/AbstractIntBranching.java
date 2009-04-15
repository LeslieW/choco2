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
    if (LOGGER.isLoggable(Level.FINE)) {
      int n = manager.solver.getEnvironment().getWorldIndex();
      if (n <= manager.getLoggingMaxDepth()) {
        LOGGER.log(Level.FINE, LOG_DOWN_MSG, new Object[]{n,x,getDecisionLogMsg(i), i});
      }
    }
  }
  
  protected void logUpBranch(final Object x, final int i) {
    if (LOGGER.isLoggable(Level.FINE)) {
      int n = manager.solver.getEnvironment().getWorldIndex();
      if (n <= manager.getLoggingMaxDepth()) {
        LOGGER.log(Level.FINE, LOG_UP_MSG, new Object[]{n + 1,x,getDecisionLogMsg(i), i});
      }
    }
  }
}
