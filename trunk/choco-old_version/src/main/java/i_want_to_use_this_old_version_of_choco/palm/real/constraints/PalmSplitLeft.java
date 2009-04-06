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
  public Constraint negate() {
    return new PalmSplitRight(v0, previous);
  }

  public int getBranch() {
    return 1;
  }
}
