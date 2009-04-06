// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.search.AbstractSearchHeuristic;

public abstract class AbstractIntVarSelector extends AbstractSearchHeuristic implements IntVarSelector {

  /**
   * a specific array of IntVars from which the object seeks the one with smallest domain
   */
  protected IntDomainVar[] vars;

  /**
   * the IVarSelector can be asked to return a variable
   *
   * @return a variable on whose domain an alternative can be set (such as a non instantiated search variable)
   */
  public AbstractVar selectVar() throws ContradictionException {
    return (AbstractVar) selectIntVar();
  }
}
