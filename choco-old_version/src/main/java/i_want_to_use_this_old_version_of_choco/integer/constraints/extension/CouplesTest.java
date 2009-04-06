// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

public abstract class CouplesTest extends ConsistencyRelation implements BinRelation {

  /**
   * the default constructor build a relation in feasability
   */
  protected CouplesTest() {
    feasible = true;
  }

  protected CouplesTest(boolean feasible) {
    this.feasible = feasible;
  }

  /**
   * check if the couple (x,y) is consistent according
   * to the feasability of the relation and the checkCouple method.
   * checkCouple have to be overriden by any concrete CouplesTest
   * relation.
   */
  public boolean isConsistent(int x, int y) {
    return checkCouple(x, y) == feasible;
  }

  /**
   * @return the opposite relation
   */
  public ConsistencyRelation getOpposite() {
    CouplesTest ct = null;
    try {
      ct = (CouplesTest) this.clone();
      ct.feasible = !feasible;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return (ConsistencyRelation) ct;
  }
}
