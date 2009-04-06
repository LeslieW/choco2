//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.integer.constraints.PalmGreaterOrEqualXC;
import i_want_to_use_this_old_version_of_choco.palm.integer.constraints.PalmLessOrEqualXC;

/**
 * An optimizing solver.
 */

public class PalmBranchAndBound extends PalmAbstractBranchAndBound {


  /**
   * Some bounds for search use.
   */

  protected int lowerBound = Integer.MIN_VALUE, upperBound = Integer.MAX_VALUE;


  /**
   * Optimum value found during the search.
   */

  protected int optimum;


  /**
   * Creates the solver for the specified problem.
   */

  public PalmBranchAndBound(AbstractProblem pb, IntDomainVar obj, boolean max) {
    super(pb, obj, max);
    lowerBound = obj.getInf();
    upperBound = obj.getSup();
  }

  public Constraint getDynamicCut() {
    int bv = getObjectiveValue();
    if (maximizing)
      lowerBound = Math.max(lowerBound, bv) + 1;
    else
      upperBound = Math.min(upperBound, bv) - 1;
    if (maximizing)
      return new PalmGreaterOrEqualXC((IntDomainVar) objective, lowerBound);
    else
      return new PalmLessOrEqualXC((IntDomainVar) objective, upperBound);
  }

  private int getObjectiveValue() {
    if (maximizing) {
      optimum = ((IntDomainVar) objective).getSup();
      return optimum;
    } else {
      optimum = ((IntDomainVar) objective).getInf();
      return optimum;
    }
  }

  public Number getOptimumValue() {
    return new Integer(optimum);
  }
}
