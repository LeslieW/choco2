package i_want_to_use_this_old_version_of_choco.palm.real.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.PalmAbstractBranchAndBound;
import i_want_to_use_this_old_version_of_choco.real.RealMath;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

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

  public PalmRealBranchAndBound(AbstractProblem pb, RealVar obj, boolean max) {
    super(pb, obj, max);
    lowerBound = obj.getInf();
    upperBound = obj.getSup();
  }

  public Constraint getDynamicCut() {
    double bv = getObjectiveValue();
    if (maximizing)
      lowerBound = RealMath.nextFloat(Math.max(lowerBound, bv));
    else
      upperBound = RealMath.prevFloat(Math.min(upperBound, bv));
    if (maximizing)
      return problem.geq((RealVar) objective, lowerBound);
    //new PalmGreaterOrEqualXC((IntDomainVar) objective, bv + 1);
    else
      return problem.leq((RealVar) objective, upperBound);
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
