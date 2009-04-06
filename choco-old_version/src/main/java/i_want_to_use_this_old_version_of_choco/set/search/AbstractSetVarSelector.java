package i_want_to_use_this_old_version_of_choco.set.search;

import i_want_to_use_this_old_version_of_choco.AbstractVar;
import i_want_to_use_this_old_version_of_choco.search.AbstractSearchHeuristic;
import i_want_to_use_this_old_version_of_choco.set.SetVar;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public abstract class AbstractSetVarSelector extends AbstractSearchHeuristic implements SetVarSelector {

  /**
   * a specific array of SetVars from which the object seeks the one with smallest domain
   */
  protected SetVar[] vars;

  public AbstractVar selectVar() {
    return (AbstractVar) selectSetVar();
  }

}
