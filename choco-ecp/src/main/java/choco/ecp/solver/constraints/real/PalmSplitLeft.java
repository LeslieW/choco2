//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.constraints.real;

import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.real.RealInterval;
import choco.kernel.solver.variables.real.RealMath;
import choco.kernel.solver.variables.real.RealVar;

/**
 * Splits the variable domain in two sub intervals and make the variable equal the
 * first half (the lower values).
 */
public class PalmSplitLeft extends AbstractPalmSplit {
  /**
   * Creates a constraint splitting the interval to assign the variable to the
   * first half.
   */
  public PalmSplitLeft(RealVar var, RealInterval interval) {
    super(var, interval);
    current = RealMath.firstHalf(interval);
  }

  /**
   * Returns the opposite of this constraint (that is a SplitRight constraint).
   */
  public SConstraint negate() {
    return new PalmSplitRightS(v0, previous);
  }

  public int getBranch() {
    return 1;
  }
}
