package i_want_to_use_this_old_version_of_choco.palm.integer;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractTernIntConstraint;
import i_want_to_use_this_old_version_of_choco.palm.PalmConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * Created by IntelliJ IDEA.
 * User: Administrateur
 * Date: 30 janv. 2004
 * Time: 09:00:21
 * To change this template use Options | File Templates.
 */
public abstract class AbstractPalmTernIntConstraint
    extends AbstractTernIntConstraint
    implements PalmIntVarListener, PalmConstraint {

  public AbstractPalmTernIntConstraint(IntDomainVar x0, IntDomainVar x1, IntDomainVar x2) {
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

  public void awakeOnRestoreVal(int idx, IntIterator repairDomain) throws ContradictionException {
    for (; repairDomain.hasNext();) {
      awakeOnRestoreVal(idx, repairDomain.next());
    }
  }

  public void updateDataStructuresOnConstraint(int idx, int select, int newValue, int oldValue) {
  }

  public void updateDataStructuresOnRestoreConstraint(int idx, int select, int newValue, int oldValue) {
  }
}
