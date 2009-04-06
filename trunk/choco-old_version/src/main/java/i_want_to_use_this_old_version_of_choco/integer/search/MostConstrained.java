// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

/**
 * An heuristic to first instantiating most constrained variables.
 */
public class MostConstrained extends IntHeuristicIntVarSelector {

  /**
   * Builds the heuristic for the given problem.
   * @param pb the solved problem
   */
  public MostConstrained(final Problem pb) {
    super(pb);
  }

  /**
   * Builds the heuristic for the given problem.
   * @param pb the solved problem
   * @param vs a list of variables instead of all prolem integer variables
   */
  public MostConstrained(final Problem pb, final IntDomainVar[] vs) {
    super(pb);
    vars = vs;
  }

   public int getHeuristic(IntDomainVar v) {
    return -v.getNbConstraints();
  }
}
