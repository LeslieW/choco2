// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

public abstract class ConsistencyRelation implements Cloneable {

  protected boolean feasible;

  /**
   * currentElement if the relation is defined with feasible tuples or
   * infeasible one.
   */
  public boolean isDefinedByFeasability() {
    return feasible;
  }

  /**
   * inverse the feasability of the relation
   */
  public void switchToOppositeRelation() {
    feasible = !feasible;
  }

  /**
   * return the opposite relation of itself
   */
  public abstract ConsistencyRelation getOpposite();

}
