// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.Var;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

public final class DecreasingDomain implements ValIterator {
  /**
   * testing whether more branches can be considered after branch i, on the alternative associated to variable x
   *
   * @param x the variable under scrutiny
   * @param i the index of the last branch explored
   * @return true if more branches can be expanded after branch i
   */
  public boolean hasNextVal(Var x, int i) {
    return (i > ((IntDomainVar) x).getInf());
  }

  /**
   * Accessing the index of the first branch for variable x
   *
   * @param x the variable under scrutiny
   * @return the index of the first branch (such as the first value to be assigned to the variable)
   */
  public int getFirstVal(Var x) {
    return ((IntDomainVar) x).getSup();
  }

  /**
   * generates the index of the next branch after branch i, on the alternative associated to variable x
   *
   * @param x the variable under scrutiny
   * @param i the index of the last branch explored
   * @return the index of the next branch to be expanded after branch i
   */
  public int getNextVal(Var x, int i) {
    return ((IntDomainVar) x).getPrevDomainValue(i);
  }
}
