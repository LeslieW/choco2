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
 * A class for implications (if (c1,c2)) propagated in a lazy way (nothing done until c1 proven true)
 * @deprecated see Reifed package
 */
public class Guard extends AbstractBinBoolConstraint {

  public Guard(AbstractConstraint c1, AbstractConstraint c2) {
    super(c1, c2);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Pretty print of the constraint.
   */

  public String pretty() {
    return "( " + const0.pretty() + " ) => ( " + const1.pretty() + " )";
  }

// Note: checkStatus is always called on a sub-constraint that we want to propagate
//    checkStatus asks for entailment, updates the status field, and,
//    when necessary, propagates the subconstraint through doAwake
//
  protected void checkStatus(int i) throws ContradictionException {
    assert(i == 0);
    assert(getTargetStatus(0) == null);
    if (getStatus(0) == null) {
      Boolean b = const0.isEntailed();
      if (b != null) {
        setStatus(0, b.booleanValue());
        if (b.booleanValue() && (getTargetStatus(1) == null)) {
          setTargetStatus(1, true);
          const1.awake();
        }
      }
    }
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx < offset) {
      checkStatus(0);
    } else if (getTargetStatus(1) != null) {
      assert(getTargetStatus(1) == Boolean.TRUE);
      ((IntConstraint) const1).awakeOnInf(idx - offset);
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx < offset) {
      checkStatus(0);
    } else if (getTargetStatus(1) != null) {
      assert(getTargetStatus(1) == Boolean.TRUE);
      ((IntConstraint) const1).awakeOnSup(idx - offset);
    }
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    if (idx < offset) {
      checkStatus(0);
    } else if (getTargetStatus(1) != null) {
      assert(getTargetStatus(1) == Boolean.TRUE);
      ((IntConstraint) const1).awakeOnInst(idx - offset);
    }
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    if (idx < offset) {
      checkStatus(0);
    } else if (getTargetStatus(1) != null) {
      assert(getTargetStatus(1) == Boolean.TRUE);
      ((IntConstraint) const1).awakeOnRem(idx - offset, x);
    }
  }

  public void propagate() throws ContradictionException {
    if (getTargetStatus(1) != null) {
      assert(getTargetStatus(1) == Boolean.TRUE);
      const1.propagate();
    } else {
      checkStatus(0);
    }
  }

  public Boolean isEntailed() {
    Boolean B0 = getStatus(0);
    Boolean B1 = getStatus(1);
    if (B0 != null) {
      if (B0.booleanValue()) {
        // case of an implication with a true condition
        assert(getTargetStatus(1) == Boolean.TRUE);
        if (B1 == null) {
          B1 = const1.isEntailed();
          if (B1 != null) {
            boolean b1 = B1.booleanValue();
            setStatus(1, b1);
          }
        }
        return B1;
      } else {
        // case of an implication with false condition : it is alays true
        return Boolean.TRUE;
      }
      // case of an implication with an unsettled condition (may be true, may be false)
    } else {
      assert(getTargetStatus(1) == null);
      // if the conclusion is true, the the constraint is defintely satisfied, otherwise, we do not know
      if (B1 == null) {
        B1 = const1.isEntailed();
        if (B1 != null) {
          boolean b1 = B1.booleanValue();
          setStatus(1, b1);
        }
      }
      return ((B1 == Boolean.TRUE) ? Boolean.TRUE : null);
    }
  }

  public boolean isSatisfied() {
    return (!const0.isSatisfied() || const1.isSatisfied());
  }

  public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
    if (idx < offset) {
      checkStatus(0);
    } else if (getTargetStatus(1) != null) {
      assert(getTargetStatus(1) == Boolean.TRUE);
      ((IntConstraint) const1).awakeOnRemovals(idx - offset, deltaDomain);
    }
  }

  public void awakeOnBounds(int varIndex) throws ContradictionException {
    if (varIndex < offset) {
      checkStatus(0);
    } else if (getTargetStatus(1) != null) {
      assert(getTargetStatus(1) == Boolean.TRUE);
      ((IntConstraint) const1).awakeOnBounds(varIndex - offset);
    }
  }

  public boolean isConsistent() {
    throw new UnsupportedOperationException();
  }

  public boolean isEquivalentTo(Constraint compareTo) {
    if (compareTo instanceof Guard) {
      Guard c = (Guard) compareTo;
      return (this.const0.isEquivalentTo(c.const0) && this.const1.isEquivalentTo(c.const1));
    } else {
      return false;
    }
  }

}
