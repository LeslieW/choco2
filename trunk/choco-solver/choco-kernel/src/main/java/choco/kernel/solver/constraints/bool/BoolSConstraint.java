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
import choco.kernel.solver.constraints.integer.IntSConstraint;

/**
 * Boolean constraints are composite constraints who maintain for each sub-constraint:
 * a status {unknown, true, false} indicating whether the subconstraint has been proven true or false
 * a targetStatus {unknown, true, false} indicating whether the subconstraint should be
 * true (in which case it is propagated) or false (in which case its opposite is propagated)
 * @deprecated see Reifed package
 */
public interface BoolSConstraint extends CompositeSConstraint, IntSConstraint {

  /**
   * returns the current status of one of its subconstraints
   *
   * @param constIdx the index of the subconstraint
   * @return Boolean.TRUE if the subconstraint is entailed, Boolean.FALSE if it is violated, NULL otherwise
   */
  public Boolean getStatus(int constIdx);

  /**
   * returns the current target status of one of its subconstraints
   *
   * @param constIdx the index of the subconstraint
   * @return Boolean.TRUE if the subconstraint must be satisfied (thus propagated),
   *         Boolean.FALSE if it must be violated (thus counter-propagated), NULL otherwise
   */
  public Boolean getTargetStatus(int constIdx);

  /**
   * updates the status of one of its subconstraints
   *
   * @param constIdx the index of the subconstraint
   * @param st       true if the subconstraint is entailed, false if it is violated
   */
  public void setStatus(int constIdx, boolean st);

  /**
   * updates the target status of one of its subconstraints
   *
   * @param constIdx the index of the subconstraint
   * @param st       true if the subconstraint must be satisfied (thus propagated),
   *                 false if it must be violated (thus counter-propagated)
   */
  public void setTargetStatus(int constIdx, boolean st);

  /**
   * records that the status of a subConstraint is now true
   *
   * @param subConstraint the subconstraint
   * @param status        the new value of the status to be recorded
   * @param varOffset     the offset for the local variable indexing in the subConstraint wrt the global numbering in this
   */
  public void setSubConstraintStatus(SConstraint subConstraint, boolean status, int varOffset);

}
