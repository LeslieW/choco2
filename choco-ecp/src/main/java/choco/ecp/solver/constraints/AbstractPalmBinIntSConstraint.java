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

import choco.ecp.solver.propagation.PalmIntVarListener;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

public abstract class AbstractPalmBinIntSConstraint
    extends AbstractBinIntSConstraint
    implements PalmIntVarListener, PalmSConstraint {

  public AbstractPalmBinIntSConstraint(IntDomainVar x0, IntDomainVar x1) {
    super(x0, x1);
  }

  public IntDomainVar getIntVar(int i) {
    if (i == 0) return v0;
    if (i == 1) return v1;
    return null;
    //throw new NetworkOutOfBoundException();
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

  public void awakeOnRestoreVal(int idx, DisposableIntIterator repairDomain) throws ContradictionException {
    for (; repairDomain.hasNext();) {
      awakeOnRestoreVal(idx, repairDomain.next());
    }
      repairDomain.dispose();
    /*for (int val = repairDomain.next(); repairDomain.hasNext(); val=repairDomain.next()) {
      awakeOnRestoreVal(idx, val);
    }*/
  }

  public void updateDataStructuresOnConstraint(int idx, int select, int newValue, int oldValue) {
  }

  public void updateDataStructuresOnRestoreConstraint(int idx, int select, int newValue, int oldValue) {
  }
}
