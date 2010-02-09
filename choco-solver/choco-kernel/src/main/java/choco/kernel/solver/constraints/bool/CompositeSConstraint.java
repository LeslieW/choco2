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

package choco.kernel.solver.constraints.bool;

import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.listener.IntVarEventListener;

/**
 * An interface for all implementations of listeners using search variables.
 * @deprecated see Reifed package
 */
public interface CompositeSConstraint extends SConstraint, IntVarEventListener {

  /**
   * return the index of the subconstraint where the i-th variable is referenced
   *
   * @param varIdx the overall index of the variable (among all variables of the combination
   * @return the index of the subconstraint involving the variable
   *         or -1 if none can be found (which would be a definite bug !)
   */
  public int getSubConstraintIdx(int varIdx);

  /**
   * accessor to the sub-constraints from which the composite constraint is made of
   *
   * @param constIdx the index of the constraint
   * @return the appropriate subConstraint
   */
  public SConstraint getSubConstraint(int constIdx);

  /**
   * returns the number of sub-constraints that the composite constraint is made of
   */
  public int getNbSubConstraints();

  /**
   * returns the global index of a variable within a constraint
   *
   * @param subConstraint the subconstraint (a node in the composition tree)
   * @param localVarIdx   the index of the variable, local to the subconstraint
   * @return the index of the variable in the global numbering associated to the composition (this)
   */
  public int getGlobalVarIndex(SConstraint subConstraint, int localVarIdx);

}
