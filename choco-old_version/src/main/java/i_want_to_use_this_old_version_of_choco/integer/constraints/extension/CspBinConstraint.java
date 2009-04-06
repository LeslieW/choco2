// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractBinIntConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

public abstract class CspBinConstraint extends AbstractBinIntConstraint {


  protected BinRelation relation;


  protected CspBinConstraint(IntDomainVar x, IntDomainVar y, BinRelation relation) {
    super(x, y);
    this.relation = relation;
  }

  /**
   * Checks if the constraint is satisfied when the variables are instantiated.
   *
   * @return true if the constraint is satisfied
   */

  public boolean isSatisfied(int[] tuple) {
    return relation.isConsistent(tuple[0], tuple[1]); //table.get((v1.getVal() - offset) * n + (v0.getVal() - offset));
  }

  public BinRelation getRelation() {
    return relation;
  }

  public void awakeOnVar(int idx) throws ContradictionException {
    logger.severe("AwakeOnVar should not be called in CspBinConstraint");
    System.exit(0);
  }

  public Boolean isEntailed() {
    boolean always = true;
    IntIterator itv1 = v0.getDomain().getIterator();
    while (itv1.hasNext()) {
      int nbs = 0;
      int val = itv1.next();
      IntIterator itv2 = v1.getDomain().getIterator();
      while (itv2.hasNext()) {
        if (relation.isConsistent(val, itv2.next())) nbs += 1;
      }
      if (nbs == 0) {
        always = false;
      } else if (nbs != v1.getDomainSize()) {
        return null;
      }
    }
    if (always)
      return Boolean.TRUE;
    else
      return Boolean.FALSE;
  }
}
