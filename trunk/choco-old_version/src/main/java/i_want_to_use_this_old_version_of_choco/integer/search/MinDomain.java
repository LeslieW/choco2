// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

public final class MinDomain extends IntHeuristicIntVarSelector {
  public MinDomain(AbstractProblem pb) {
    super(pb);
  }

  public MinDomain(AbstractProblem pb, IntDomainVar[] vs) {
    super(pb);
    vars = vs;
  }

  public int getHeuristic(IntDomainVar v) {
    return v.getDomainSize();
  }

}
