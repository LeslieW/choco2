package choco.ecp.solver.search;

import choco.ecp.solver.search.dbt.PalmAbstractBranchAndBound;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.real.RealMath;
import choco.kernel.solver.variables.real.RealVar;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 20 juil. 2004
 */
public class PalmRealBranchAndBound extends PalmAbstractBranchAndBound {
  /**
   * Some bounds for search use.
   */

  protected double lowerBound, upperBound;


  /**
   * Optimum value found during the search.
   */

  protected double optimum;

  /**
   * Creates the solver for the specified problepb.
   */

  public PalmRealBranchAndBound(Solver pb, RealVar obj, boolean max) {
    super(pb, obj, max);
    lowerBound = obj.getInf();
    upperBound = obj.getSup();
  }

  public SConstraint getDynamicCut() {
    double bv = getObjectiveValue();
    if (maximizing)
      lowerBound = RealMath.nextFloat(Math.max(lowerBound, bv));
    else
      upperBound = RealMath.prevFloat(Math.min(upperBound, bv));
    if (maximizing)
      return solver.geq((RealVar) objective, lowerBound);
    //new PalmGreaterOrEqualXC((IntDomainVar) objective, bv + 1);
    else
      return solver.leq((RealVar) objective, upperBound);
    //new PalmLessOrEqualXC((IntDomainVar) objective, bv - 1);
  }

  private double getObjectiveValue() {
    if (maximizing) {
      optimum = ((RealVar) objective).getSup();
      return optimum;
    } else {
      optimum = ((RealVar) objective).getInf();
      return optimum;
    }
  }

  public Number getOptimumValue() {
    return new Double(optimum);
  }
}
