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

package choco.kernel.solver.constraints.integer;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.IntVarEventListener;
import choco.kernel.solver.propagation.Propagator;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * An interface for all implementations of listeners using search variables.
 */
public interface IntSConstraint extends SConstraint, Propagator, IntVarEventListener {

  /**
   * <i>Network management:</i>
   * Accessing the i-th search variable of a constraint.
   *
   * @param i index of the variable among all search variables in the constraint. Numbering start from 0 on.
   * @return the variable, or null when no such variable is found
   */

  public IntDomainVar getIntVar(int i);

  public void awakeOnRemovals(int varIdx, DisposableIntIterator deltaDomain) throws ContradictionException;

  public void awakeOnBounds(int varIdx) throws ContradictionException;

  public boolean isSatisfied(int[] tuple);

}
