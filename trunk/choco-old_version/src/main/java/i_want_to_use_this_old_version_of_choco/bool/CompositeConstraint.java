// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.bool;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.integer.var.IntVarEventListener;

/**
 * An interface for all implementations of listeners using search variables.
 * @deprecated see Reifed package
 */
public interface CompositeConstraint extends Constraint, IntVarEventListener {

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
  public Constraint getSubConstraint(int constIdx);

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
  public int getGlobalVarIndex(Constraint subConstraint, int localVarIdx);

}
