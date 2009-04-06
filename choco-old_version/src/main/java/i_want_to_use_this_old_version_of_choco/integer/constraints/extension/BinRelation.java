// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

public interface BinRelation {

  /**
   * return true if couple (x,y) is feasible according
   * to the definition of the relation. e.g if the relation is defined
   * with infeasible tuples, it returns true if (x,y) is one of them.
   *
   * @param x
   * @param y
   * @return
   */
  public boolean checkCouple(int x, int y);

  /**
   * Test whether the couple (x,y) is consistent
   *
   * @param x
   * @param y
   * @return true if (x,y) is a consistent couple
   */
  public boolean isConsistent(int x, int y);

}
