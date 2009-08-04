package choco.ecp.solver.constraints;

import choco.ecp.solver.propagation.PalmIntVarListener;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: Administrateur
 * Date: 27 janv. 2004
 * Time: 15:26:56
 * To change this template use Options | File Templates.
 */
public abstract class AbstractPalmLargeIntSConstraint
    extends AbstractLargeIntSConstraint
    implements PalmIntVarListener, PalmSConstraint {

  public AbstractPalmLargeIntSConstraint(IntDomainVar[] vars) {
    super(vars);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public IntDomainVar getIntVar(int i) {
    if (i >= 0 && i < getNbVars())
      return this.vars[i];
    else
      return null;
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
