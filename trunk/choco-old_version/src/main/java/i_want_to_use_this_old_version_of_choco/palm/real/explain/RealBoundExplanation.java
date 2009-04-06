//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.real.explain;

import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.real.PalmRealVar;

public interface RealBoundExplanation extends PalmExplanation {
  /**
   * Returns the previous value of the bound (that is value the variable bound should equal if
   * this explanaation is not valid anymore).
   */
  public double getPreviousValue();

  /**
   * Returns the variable involved by this explanation.
   *
   * @return
   */
  public PalmRealVar getVariable();
}
