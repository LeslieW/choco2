package choco.ecp.solver.constraints;

import choco.ecp.solver.propagation.PalmIntVarListener;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractTernIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: Administrateur
 * Date: 30 janv. 2004
 * Time: 09:00:21
 * To change this template use Options | File Templates.
 */
public abstract class AbstractPalmTernIntSConstraint
    extends AbstractTernIntSConstraint
    implements PalmIntVarListener, PalmSConstraint {

  public AbstractPalmTernIntSConstraint(IntDomainVar x0, IntDomainVar x1, IntDomainVar x2) {
    super(x0,x1,x2);
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
  }

  public void updateDataStructuresOnConstraint(int idx, int select, int newValue, int oldValue) {
  }

  public void updateDataStructuresOnRestoreConstraint(int idx, int select, int newValue, int oldValue) {
  }
}
