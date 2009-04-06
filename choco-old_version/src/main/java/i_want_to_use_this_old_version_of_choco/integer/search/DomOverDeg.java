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

/**
 * A heuristic selecting the {@link i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl} with smallest ration (domainSize / degree)
 * (the degree of a variable is the number of constraints linked to it)
 */
public final class DomOverDeg extends DoubleHeuristicIntVarSelector {
  public DomOverDeg(AbstractProblem pb) {
    super(pb);

  }

  public DomOverDeg(AbstractProblem pb, IntDomainVar[] vs) {
    super(pb);
    vars = vs;
  }

  public double getHeuristic(IntDomainVar v) {
    int dsize = v.getDomainSize();
    int deg = v.getNbConstraints();
    if (deg == 0)
      return Double.POSITIVE_INFINITY;
    else
      return (double) dsize / (double) deg;
  }
}
