// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.constraints.AbstractLargeIntConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

public class CspLargeConstraint extends AbstractLargeIntConstraint {

  protected LargeRelation relation;

  protected int[] currentTuple;

  public CspLargeConstraint(IntDomainVar[] vs, LargeRelation relation) {
    super(vs);
    this.relation = relation;
    this.currentTuple = new int[vs.length];
  }

  public Object clone() throws CloneNotSupportedException {
    CspLargeConstraint newc = (CspLargeConstraint) super.clone();
    newc.currentTuple = new int[this.currentTuple.length];
    System.arraycopy(this.currentTuple, 0, newc.currentTuple, 0, this.currentTuple.length);
    return newc;
  }

  public LargeRelation getRelation() {
    return relation;
  }

  public void propagate() throws ContradictionException {
    boolean stop = false;
    int nbUnassigned = 0;
    int index = -1, i = 0;
    while (!stop && i < vars.length) {
      if (!vars[i].isInstantiated()) {
        nbUnassigned++;
        index = i;
      } else
        currentTuple[i] = vars[i].getVal();
      if (nbUnassigned > 1) stop = true;
      i++;
    }
    if (!stop) {
      if (nbUnassigned == 1) {
        IntIterator it = vars[index].getDomain().getIterator();
        while (it.hasNext()) {
          currentTuple[index] = it.next();
          if (!relation.isConsistent(currentTuple))
            vars[index].removeVal(currentTuple[index], cIndices[index]);
        }
      } else {
        if (!relation.isConsistent(currentTuple)) this.fail();
      }
    }
  }

  public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
    this.constAwake(false);
  }

  public void awakeOnBounds(int varIndex) throws ContradictionException {
    this.constAwake(false);
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    this.constAwake(false);
  }

  public boolean isSatisfied(int[] tuple) {
    return relation.isConsistent(tuple);
  }

  public String pretty() {
    StringBuilder sb = new StringBuilder();
    sb.append("CSPLarge({");
    for (int i = 0; i < vars.length; i++) {
      if (i > 0) sb.append(", ");
      IntDomainVar var = vars[i];
      sb.append(", ");
    }
    sb.append("})");
    return sb.toString();
  }

  public AbstractConstraint opposite() {
    LargeRelation rela2 = (LargeRelation) ((ConsistencyRelation) relation).getOpposite();
    AbstractConstraint ct = new CspLargeConstraint(vars, rela2);
    return ct;
  }

  public Boolean isEntailed() {
    throw new Error("isEntailed not yet implemented in CspLargeConstraint");
  }

}
