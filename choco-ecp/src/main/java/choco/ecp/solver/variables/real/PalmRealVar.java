//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.variables.real;

import choco.ecp.solver.variables.PalmVar;
import choco.kernel.solver.variables.real.RealVar;

/**
 * Interface of a real variable in PaLM.
 */
public interface PalmRealVar extends RealVar, PalmRealInterval, PalmVar {
  /**
   * Restores a previous value of the lower bound.
   */
  public void restoreInf(double newValue);

  /**
   * Restores a previous value of the upper bound.
   */
  public void restoreSup(double newValue);

  /**
   * Updates lower bound explanations: it removes not up-to-date explanations.
   */
  public void resetExplanationOnInf();

  /**
   * Updates upper bound explanations: it removes not up-to-date explanations.
   */
  public void resetExplanationOnSup();

  /**
   * Updates decision constraints on this variable: it removes all erased constraints.
   */
  public void updateDecisionConstraints();
}
