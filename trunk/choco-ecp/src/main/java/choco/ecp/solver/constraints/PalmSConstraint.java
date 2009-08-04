//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.constraints;

import choco.ecp.solver.propagation.PalmVarListener;
import choco.kernel.solver.propagation.Propagator;

public interface PalmSConstraint extends Propagator, PalmVarListener {

  /**
   * Informs constraints that one of their children has a modified status (due to value restoration).
   */

  public void takeIntoAccountStatusChange(int index);

  public Object getPlugIn();

}
