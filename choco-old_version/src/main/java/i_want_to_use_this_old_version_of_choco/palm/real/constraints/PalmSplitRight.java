//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.real.constraints;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;
import i_want_to_use_this_old_version_of_choco.real.RealMath;
import i_want_to_use_this_old_version_of_choco.real.RealVar;

/**
 * Splits the variable domain in two sub intervals and make the variable equal the
 * second half (the upper values).
 */
public class PalmSplitRight extends AbstractPalmSplit {
  /**
   * Creates a constraint splitting the interval to assign the variable to the
   * second half.
   */
  public PalmSplitRight(RealVar var, RealInterval interval) {
    super(var, interval);
    current = RealMath.secondHalf(interval);
  }

  /**
   * Returns the opposite of this constraint (that is a SplitLeft constraint).
   */
  public Constraint negate() {
    return new PalmSplitLeft(v0, previous);
  }

  public int getBranch() {
    return 2;
  }
}
