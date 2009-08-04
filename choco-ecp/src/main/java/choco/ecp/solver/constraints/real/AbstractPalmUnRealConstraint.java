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

import choco.ecp.solver.propagation.PalmRealVarListener;
import choco.kernel.solver.constraints.real.AbstractUnRealSConstraint;
import choco.kernel.solver.constraints.real.RealSConstraint;
import choco.kernel.solver.variables.real.RealVar;

/**
 * Abstract implementation of an unary constraint on one real variable.
 */

public abstract class AbstractPalmUnRealConstraint extends AbstractUnRealSConstraint
    implements RealSConstraint, PalmRealVarListener {
    /**
     * Constraucts a constraint with the priority 0.
     */
    protected AbstractPalmUnRealConstraint(RealVar v0) {
        super(v0);
    }

    /**
   * Synchronous update events are not handled by default.
   *
   * @param idx      the index of the modified variable
   * @param select   the modificatino on this variable
   * @param newValue the new value for the involved property
   * @param oldValue the old value for the involved property
   */
  public void updateDataStructuresOnConstraint(int idx, int select, int newValue, int oldValue) {
  }

  /**
   * Synchronous restoration events are not handled by default.
   *
   * @param idx      the index of the modified variable
   * @param select   the modificatino on this variable
   * @param newValue the new value for the involved property
   * @param oldValue the old value for the involved property
   */
  public void updateDataStructuresOnRestoreConstraint(int idx, int select, int newValue, int oldValue) {
  }
}
