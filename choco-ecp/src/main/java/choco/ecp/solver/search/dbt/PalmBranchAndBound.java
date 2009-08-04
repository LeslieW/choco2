//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.search.dbt;

import choco.ecp.solver.constraints.integer.PalmGreaterOrEqualXC;
import choco.ecp.solver.constraints.integer.PalmLessOrEqualXC;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

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

  public PalmBranchAndBound(Solver pb, IntDomainVar obj, boolean max) {
    super(pb, obj, max);
    lowerBound = obj.getInf();
    upperBound = obj.getSup();
  }

  public SConstraint getDynamicCut() {
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
