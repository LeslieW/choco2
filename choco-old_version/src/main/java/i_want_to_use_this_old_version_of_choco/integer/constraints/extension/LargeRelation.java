// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

public interface LargeRelation {

  /**
   * return true if tuple is feasible according
   * to the definition of the relation. e.g if the relation is defined
   * with infeasible tuples, it returns true if tuple is one of them.
   *
   * @return
   */
  public boolean checkTuple(int[] tuple);

  /**
   * Test whether a tuple is consistent
   *
   * @return true if tuple is consistent.
   */
  public boolean isConsistent(int[] tuple);


}
