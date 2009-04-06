//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.real;

import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;

/**
 * Interface of a real interval in PaLM.
 */
public interface PalmRealInterval extends RealInterval {
  /**
   * Constant for selecting all domain when asking explanations.
   */
  public static final int DOM = 0;

  /**
   * Constant for selecting lower bound when asking explanations.
   */
  public static final int INF = 1;

  /**
   * Constant for selecting upper bound when asking explanations.
   */
  public static final int SUP = 2;

  /**
   * Merge explanation of the specified part of the domain to a constraint collection.
   */
  public void self_explain(int select, Explanation e);
}
