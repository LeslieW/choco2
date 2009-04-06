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
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractUnIntConstraint;
import i_want_to_use_this_old_version_of_choco.palm.PalmConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

public abstract class AbstractPalmUnIntConstraint
    extends AbstractUnIntConstraint
    implements PalmIntVarListener, PalmConstraint {

  public IntDomainVar getIntVar(int i) {
    if (i == 0) return v0;
    return null; //throw new NetworkOutOfBoundException();
  }

  public void takeIntoAccountStatusChange(int index) {
  }


  public void awakeOnRestoreInf(int index) throws ContradictionException {
    this.propagate();
  }

  public void awakeOnRestoreSup(int index) throws ContradictionException {
    this.propagate();
  }

  public void awakeOnInst(int idx) {
  }

  public void awakeOnRestoreVal(int idx, IntIterator repairDomain) throws ContradictionException {
    for (; repairDomain.hasNext();) {
      awakeOnRestoreVal(idx, repairDomain.next());
    }
    //for (int val = repairDomain.next(); repairDomain.hasNext(); val=repairDomain.next()) {
    //  awakeOnRestoreVal(idx, val);
    //}
  }

  public void updateDataStructuresOnConstraint(int idx, int select, int newValue, int oldValue) {
  }

  public void updateDataStructuresOnRestoreConstraint(int idx, int select, int newValue, int oldValue) {
  }
}
