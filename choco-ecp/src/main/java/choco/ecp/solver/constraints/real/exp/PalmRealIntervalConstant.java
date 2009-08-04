//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.constraints.real.exp;

import choco.ecp.solver.PalmSolver;
import choco.ecp.solver.explanations.Explanation;
import choco.ecp.solver.explanations.dbt.PalmExplanation;
import choco.ecp.solver.variables.real.PalmRealInterval;
import choco.kernel.common.util.objects.ConstraintCollection;
import choco.kernel.solver.variables.real.RealIntervalConstant;

/**
 * Implementation of a constant interval.
 */
public class PalmRealIntervalConstant extends RealIntervalConstant implements PalmRealInterval {
  /**
   * The lower bound explanation if needed.
   */
  PalmExplanation explanationOnInf;

  /**
   * The upper bound explanation if needed.
   */
  PalmExplanation explanationOnSup;

  /**
   * Creates a constant from the current state of an interval.
   *
   * @param pb The current problem (needed for creating explanations).
   */
  public PalmRealIntervalConstant(PalmRealInterval interval, PalmSolver pb) {
    super(interval);
    explanationOnInf = (PalmExplanation) pb.makeExplanation();
    explanationOnSup = (PalmExplanation) pb.makeExplanation();
    interval.self_explain(PalmRealInterval.INF, explanationOnInf);
    interval.self_explain(PalmRealInterval.SUP, explanationOnSup);
  }

  /**
   * Creates a constant with the specified values.
   */
  public PalmRealIntervalConstant(double inf, double sup, Explanation expOnInf, Explanation expOnSup) {
    super(inf, sup);
    explanationOnInf = (PalmExplanation) expOnInf;
    explanationOnSup = (PalmExplanation) expOnSup;
  }

  /**
   * Creates a constant without explanations from the current state of the interval.
   */
  public PalmRealIntervalConstant(PalmRealInterval interval) {
    super(interval);
    explanationOnInf = null;
    explanationOnSup = null;
  }

  /**
   * Creates a constant without explanations.
   */
  public PalmRealIntervalConstant(double inf, double sup) {
    super(inf, sup);
    explanationOnInf = null;
    explanationOnSup = null;
  }

  /**
   * Explains the values of the constant.
   *
   * @param select The part if the domain that should be explained.
   * @param e      The constraint collection in which this explanation should be added.
   */
  public void self_explain(int select, Explanation e) {
    if (explanationOnInf != null)
      switch (select) { // Sinon explication vide...
        case PalmRealInterval.INF:
          e.merge((ConstraintCollection) this.explanationOnInf);
          break;
        case PalmRealInterval.SUP:
          e.merge((ConstraintCollection) this.explanationOnSup);
          break;
        case PalmRealInterval.DOM:
          e.merge((ConstraintCollection) this.explanationOnInf);
          e.merge((ConstraintCollection) this.explanationOnSup);
          break;
      }
  }
}
