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
package choco.kernel.solver.propagation;

import choco.kernel.solver.ContradictionException;

/**
 * An interface for real event listener (like real constraints for instance).
 */
public interface RealVarEventListener extends VarEventListener, Propagator {
  /**
   * Default propagation on improved lower bound: propagation on domain revision.
   */

  public void awakeOnInf(int idx) throws ContradictionException;


  /**
   * Default propagation on improved upper bound: propagation on domain revision.
   */

  public void awakeOnSup(int idx) throws ContradictionException;
}
