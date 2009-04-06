// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.search.AbstractSearchHeuristic;

public class MidVal extends AbstractSearchHeuristic implements ValSelector {
  /**
   * selecting a value in the middle of the domain
   *
   * @param x the variable under consideration
   * @return what seems the most interesting value for branching
   */
  public int getBestVal(IntDomainVar x) {
    return x.getNextDomainValue(x.getInf() + (x.getSup() - x.getInf()) / 2);
  }
}
