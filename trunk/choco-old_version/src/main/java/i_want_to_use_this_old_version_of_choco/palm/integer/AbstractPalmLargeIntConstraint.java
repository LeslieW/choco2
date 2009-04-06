package i_want_to_use_this_old_version_of_choco.palm.integer;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractLargeIntConstraint;
import i_want_to_use_this_old_version_of_choco.palm.PalmConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * Created by IntelliJ IDEA.
 * User: Administrateur
 * Date: 27 janv. 2004
 * Time: 15:26:56
 * To change this template use Options | File Templates.
 */
public abstract class AbstractPalmLargeIntConstraint
    extends AbstractLargeIntConstraint
    implements PalmIntVarListener, PalmConstraint {

  public AbstractPalmLargeIntConstraint(IntDomainVar[] vars) {
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

  public void awakeOnRestoreVal(int idx, IntIterator repairDomain) throws ContradictionException {
    for (; repairDomain.hasNext();) {
      awakeOnRestoreVal(idx, repairDomain.next());
    }
    /*for (int val = repairDomain.next(); repairDomain.hasNext(); val=repairDomain.next()) {
      awakeOnRestoreVal(idx, val);
    }*/
  }

  public void updateDataStructuresOnConstraint(int idx, int select, int newValue, int oldValue) {
  }

  public void updateDataStructuresOnRestoreConstraint(int idx, int select, int newValue, int oldValue) {
  }

}
