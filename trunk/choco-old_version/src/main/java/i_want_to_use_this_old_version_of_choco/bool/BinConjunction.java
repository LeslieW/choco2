// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.bool;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * A class for Conjunctions (only used in subterms of Boolean formulae (c1 and c2)
 * @deprecated see Reifed package
 */
// note v1.02: for conjunctions, targetStatus slots are useless -> we only use status fields
public class BinConjunction extends AbstractBinBoolConstraintWithCounterOpposite {

  public BinConjunction(AbstractConstraint c0, AbstractConstraint c1) {
    super(c0, c1);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Pretty print of the constraint.
   */

  public String pretty() {
    return " (" + const0.pretty() + ") and (" + const1.pretty() + ") ";
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx < offset) {
      ((IntConstraint) const0).awakeOnInf(idx);
    } else {
      ((IntConstraint) const1).awakeOnInf(idx - offset);
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx < offset) {
      ((IntConstraint) const0).awakeOnSup(idx);
    } else {
      ((IntConstraint) const1).awakeOnSup(idx - offset);
    }
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    if (idx < offset) {
      ((IntConstraint) const0).awakeOnInst(idx);
    } else {
      ((IntConstraint) const1).awakeOnInst(idx - offset);
    }
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    if (idx < offset) {
      ((IntConstraint) const0).awakeOnRem(idx, x);
    } else {
      ((IntConstraint) const1).awakeOnRem(idx - offset, x);
    }
  }

  public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
    if (idx < offset) {
      ((IntConstraint) const0).awakeOnRemovals(idx, deltaDomain);
    } else {
      ((IntConstraint) const1).awakeOnRemovals(idx - offset, deltaDomain);
    }
  }

  public void awakeOnBounds(int varIndex) throws ContradictionException {
    if (varIndex < offset) {
      ((IntConstraint) const0).awakeOnBounds(varIndex);
    } else {
      ((IntConstraint) const1).awakeOnBounds(varIndex - offset);
    }
  }

  public void propagate() throws ContradictionException {
    const0.propagate();
    const1.propagate();
  }

  public void awake() throws ContradictionException {
    const0.awake();
    const1.awake();
  }

  public boolean isSatisfied() {
    return (const0.isSatisfied() && const1.isSatisfied());
  }

  public boolean isConsistent() {
    throw new UnsupportedOperationException();
  }

  public Boolean isEntailed() {
    Boolean B0 = getStatus(0);
    if (B0 == null) {
      B0 = const0.isEntailed();
      if (B0 != null) {
        setStatus(0, B0.booleanValue());
      }
    }
    Boolean B1 = getStatus(1);
    if (B1 == null) {
      B1 = const1.isEntailed();
      if (B1 != null) {
        setStatus(1, B1.booleanValue());
      }
    }
    if ((B0 == Boolean.TRUE) && (B1 == Boolean.TRUE)) {
      return Boolean.TRUE;
    } else if ((B0 == Boolean.FALSE) || (B1 == Boolean.FALSE)) {
      return Boolean.FALSE;
    } else {
      return null;
    }
  }

  public AbstractConstraint opposite() {
    return new BinDisjunction(oppositeConst0, oppositeConst1);
  }

  public boolean isEquivalentTo(Constraint compareTo) {
    if (compareTo instanceof BinConjunction) {
      BinConjunction c = (BinConjunction) compareTo;
      return ((this.const0.isEquivalentTo(c.const0) && this.const1.isEquivalentTo(c.const1)) ||
          (this.const0.isEquivalentTo(c.const1) && this.const1.isEquivalentTo(c.const0)));
    } else {
      return false;
    }
  }

}
