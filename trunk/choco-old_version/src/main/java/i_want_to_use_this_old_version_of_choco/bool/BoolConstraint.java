// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.bool;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;

/**
 * Boolean constraints are composite constraints who maintain for each sub-constraint:
 * a status {unknown, true, false} indicating whether the subconstraint has been proven true or false
 * a targetStatus {unknown, true, false} indicating whether the subconstraint should be
 * true (in which case it is propagated) or false (in which case its opposite is propagated)
 * @deprecated see Reifed package
 */
public interface BoolConstraint extends CompositeConstraint, IntConstraint {

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
  public void setSubConstraintStatus(Constraint subConstraint, boolean status, int varOffset);

}
