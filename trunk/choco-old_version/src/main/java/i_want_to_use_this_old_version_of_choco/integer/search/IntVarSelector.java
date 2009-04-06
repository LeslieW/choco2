// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.VarSelector;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

/**
 * an interface for objects controlling the selection of an search variable (for heuristic purposes)
 */
public interface IntVarSelector extends VarSelector {
  /**
   * the IIntVarSelector can be asked to return an {@link i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl}
   *
   * @return a non instantiated search variable
   */
  public IntDomainVar selectIntVar() throws ContradictionException;
}
