//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard,
//                                   Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.real;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.palm.PalmVarListener;
import i_want_to_use_this_old_version_of_choco.real.constraint.RealListener;

/**
 * Interface for listeners that need to be warned when modification on
 * real variables occurs.
 */
public interface PalmRealVarListener extends PalmVarListener, RealListener {
  /**
   * Handles an inf bound restoration on the constraint <code>idx</code>.
   * @param idx Variable involved.
   * @throws ContradictionException if a contradiction occurs
   */
  void awakeOnRestoreInf(int idx) throws ContradictionException;


  /**
   * Handles a sup bound restoration on the constraint <code>idx</code>.
   * @param idx Variable involved.
   * @throws ContradictionException if a contradiction occurs
   */
  void awakeOnRestoreSup(int idx) throws ContradictionException;
}
