// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

public abstract class TuplesTest extends ConsistencyRelation implements LargeRelation {

  /**
   * the default constructor build a relation in feasability
   */
  public TuplesTest() {
    this.feasible = true;
  }

  public TuplesTest(boolean feasible) {
    this.feasible = feasible;
  }

  public boolean isConsistent(int[] tuple) {
    return checkTuple(tuple) == feasible;
  }

  public ConsistencyRelation getOpposite() {
    TuplesTest ct = null;
    try {
      ct = (TuplesTest) this.clone();
      ct.feasible = !feasible;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return (ConsistencyRelation) ct;
  }

}
