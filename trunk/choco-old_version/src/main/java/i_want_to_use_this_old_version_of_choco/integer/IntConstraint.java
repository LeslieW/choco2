// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Propagator;
import i_want_to_use_this_old_version_of_choco.integer.var.IntVarEventListener;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * An interface for all implementations of listeners using search variables.
 */
public interface IntConstraint extends Constraint, Propagator, IntVarEventListener {

  /**
   * <i>Network management:</i>
   * Accessing the i-th search variable of a constraint.
   *
   * @param i index of the variable among all search variables in the constraint. Numbering start from 0 on.
   * @return the variable, or null when no such variable is found
   */

  public IntDomainVar getIntVar(int i);

  public void awakeOnRemovals(int varIdx, IntIterator deltaDomain) throws ContradictionException;

  public void awakeOnBounds(int varIdx) throws ContradictionException;

  public boolean isSatisfied(int[] tuple);
}
