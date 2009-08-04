//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.search.dbt;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.PropagationEngine;

public class PalmContradiction extends ContradictionException {

  public PalmContradiction() {
    super();
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
