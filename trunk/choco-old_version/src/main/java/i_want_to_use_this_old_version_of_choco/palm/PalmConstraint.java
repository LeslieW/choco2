//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm;

import i_want_to_use_this_old_version_of_choco.Propagator;

public interface PalmConstraint extends Propagator, PalmVarListener {

  /**
   * Informs constraints that one of their children has a modified status (due to value restoration).
   */

  public void takeIntoAccountStatusChange(int index);

}
