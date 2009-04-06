//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.integer;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.var.IntVarEventListener;
import i_want_to_use_this_old_version_of_choco.palm.PalmVarListener;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Set;

public interface PalmIntVarListener extends PalmVarListener, IntVarEventListener {

  /**
   * Handles an inf bound restoration on the constraint <code>idx</code>
   *
   * @param idx Variable involved.
   * @throws ContradictionException
   */

  public void awakeOnRestoreInf(int idx) throws ContradictionException;


  /**
   * Handles a sup bound restoration on the constraint <code>idx</code>
   *
   * @param idx Variable involved.
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException
   */

  public void awakeOnRestoreSup(int idx) throws ContradictionException;


  /**
   * Handles a val restoration on the constraint <code>idx</code>
   *
   * @param idx Variable involved.
   * @throws ContradictionException
   */

  public void awakeOnRestoreVal(int idx, int val) throws ContradictionException;

  /**
   * Handles a val restoration on the constraint <code>idx</code>
   *
   * @param idx Variable involved.
   * @throws ContradictionException
   */

  public void awakeOnRestoreVal(int idx, IntIterator it) throws ContradictionException;

  /**
   * When all variables are instantiated, explains why the wonstraint is true.
   *
   * @return A set of constraint justifying that the constraint is satisfied.
   */

  public Set whyIsTrue();


  /**
   * When all variables are instantiated, explains why the wonstraint is false.
   *
   * @return A set of constraint justifying that the constraint is not satisfied.
   */

  public Set whyIsFalse();
}
