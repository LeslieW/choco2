//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.search;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Entity;
import i_want_to_use_this_old_version_of_choco.prop.PropagationEngine;

public class PalmContradiction extends ContradictionException {

  public PalmContradiction(Entity cause) {
    super(cause);
  }

  /**
   * when a contradiction is thrown,
   * the propagation queue is flushed.
   *
   * @param pe
   */
  public void resetQueue(PropagationEngine pe) {
  }

}
