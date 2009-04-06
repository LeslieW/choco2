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
import i_want_to_use_this_old_version_of_choco.Propagator;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * A class for equivalences (c1 if and only if c2)
 * @deprecated see Reifed package
 */
public class Equiv extends AbstractBinBoolConstraintWithCounterOpposite {

  public Equiv(AbstractConstraint c1, AbstractConstraint c2) {
    super(c1, c2);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Pretty print of the constraint.
   */

  public String pretty() {
    return "( " + const0.pretty() + " ) <=> ( " + const1.pretty() + " )";
  }

  // if status(i) can be inferred, sets targetStatus(j) and propagate accordingly
  protected void checkStatus(int i) throws ContradictionException {
    Propagator ci = ((i == 0) ? const0 : const1);
    int j = 1 - i;
    Propagator cj = ((j == 0) ? const0 : const1);
    Propagator oppcj = ((j == 0) ? oppositeConst0 : oppositeConst1);

    assert(getTargetStatus(j) == null);
    Boolean Bi = ci.isEntailed();
    if (Bi != null) {
      boolean bi = Bi.booleanValue();
      setStatus(i, bi);
      setTargetStatus(j, bi);
      Boolean Bj = getStatus(j);
      if (Bj != null) {
        if (Bj.booleanValue() != bi) {
          fail();
        }
      } else if (bi) {
        cj.awake();
      } else {
        oppcj.awake();
      }
    }
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx < offset) {
      if (getStatus(0) == null) {
        Boolean TB0 = getTargetStatus(0);
        if (TB0 == Boolean.TRUE) {
          ((IntConstraint) const0).awakeOnInf(idx);
        } else if (TB0 == Boolean.FALSE) {
          ((IntConstraint) oppositeConst0).awakeOnInf(idx);
        } else {
          checkStatus(0);
        }
      }
    } else {
      if (getStatus(1) == null) {
        Boolean TB1 = getTargetStatus(1);
        if (TB1 == Boolean.TRUE) {
          ((IntConstraint) const1).awakeOnInf(idx - this.offset);
        } else if (TB1 == Boolean.FALSE) {
          ((IntConstraint) oppositeConst1).awakeOnInf(idx - this.offset);
        } else {
          checkStatus(1);
        }
      }
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx < offset) {
      if (getStatus(0) == null) {
        Boolean TB0 = getTargetStatus(0);
        if (TB0 == Boolean.TRUE) {
          ((IntConstraint) const0).awakeOnSup(idx);
        } else if (TB0 == Boolean.FALSE) {
          ((IntConstraint) oppositeConst0).awakeOnSup(idx);
        } else {
          checkStatus(0);
        }
      }
    } else {
      if (getStatus(1) == null) {
        Boolean TB1 = getTargetStatus(1);
        if (TB1 == Boolean.TRUE) {
          ((IntConstraint) const1).awakeOnSup(idx - this.offset);
        } else if (TB1 == Boolean.FALSE) {
          ((IntConstraint) oppositeConst1).awakeOnSup(idx - this.offset);
        } else {
          checkStatus(1);
        }
      }
    }
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    if (idx < offset) {
      if (getStatus(0) == null) {
        Boolean TB0 = getTargetStatus(0);
        if (TB0 == Boolean.TRUE) {
          ((IntConstraint) const0).awakeOnInst(idx);
        } else if (TB0 == Boolean.FALSE) {
          ((IntConstraint) oppositeConst0).awakeOnInst(idx);
        } else {
          checkStatus(0);
        }
      }
    } else {
      if (getStatus(1) == null) {
        Boolean TB1 = getTargetStatus(1);
        if (TB1 == Boolean.TRUE) {
          ((IntConstraint) const1).awakeOnInst(idx - this.offset);
        } else if (TB1 == Boolean.FALSE) {
          ((IntConstraint) oppositeConst1).awakeOnInst(idx - this.offset);
        } else {
          checkStatus(1);
        }
      }
    }
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    if (idx < offset) {
      if (getStatus(0) == null) {
        Boolean TB0 = getTargetStatus(0);
        if (TB0 == Boolean.TRUE) {
          ((IntConstraint) const0).awakeOnRem(idx, x);
        } else if (TB0 == Boolean.FALSE) {
          ((IntConstraint) oppositeConst0).awakeOnRem(idx, x);
        } else {
          checkStatus(0);
        }
      }
    } else {
      if (getStatus(1) == null) {
        Boolean TB1 = getTargetStatus(1);
        if (TB1 == Boolean.TRUE) {
          ((IntConstraint) const1).awakeOnRem(idx - this.offset, x);
        } else if (TB1 == Boolean.FALSE) {
          ((IntConstraint) oppositeConst1).awakeOnRem(idx - this.offset, x);
        } else {
          checkStatus(1);
        }
      }
    }
  }

  public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
    if (idx < offset) {
      if (getStatus(0) == null) {
        Boolean TB0 = getTargetStatus(0);
        if (TB0 == Boolean.TRUE) {
          ((IntConstraint) const0).awakeOnRemovals(idx - this.offset, deltaDomain);
        } else if (TB0 == Boolean.FALSE) {
          ((IntConstraint) oppositeConst0).awakeOnRemovals(idx, deltaDomain);
        } else {
          checkStatus(0);
        }
      }
    } else {
      if (getStatus(1) == null) {
        Boolean TB1 = getTargetStatus(1);
        if (TB1 == Boolean.TRUE) {
          ((IntConstraint) const1).awakeOnRemovals(idx - this.offset, deltaDomain);
        } else if (TB1 == Boolean.FALSE) {
          ((IntConstraint) oppositeConst1).awakeOnRemovals(idx, deltaDomain);
        } else {
          checkStatus(1);
        }
      }
    }
  }

  public void awakeOnBounds(int idx) throws ContradictionException {
    if (idx < offset) {
      if (getStatus(0) == null) {
        Boolean TB0 = getTargetStatus(0);
        if (TB0 == Boolean.TRUE) {
          ((IntConstraint) const0).awakeOnBounds(idx);
        } else if (TB0 == Boolean.FALSE) {
          ((IntConstraint) oppositeConst0).awakeOnBounds(idx);
        } else {
          checkStatus(0);
        }
      }
    } else {
      if (getStatus(1) == null) {
        Boolean TB1 = getTargetStatus(1);
        if (TB1 == Boolean.TRUE) {
          ((IntConstraint) const1).awakeOnBounds(idx - this.offset);
        } else if (TB1 == Boolean.FALSE) {
          ((IntConstraint) oppositeConst1).awakeOnBounds(idx - this.offset);
        } else {
          checkStatus(1);
        }
      }
    }
  }

// v1.05 when the target status is known, no need to call awake, propagate is enough
// (initial propagation was already done when the target status was settled)
  public void propagate() throws ContradictionException {
    Boolean B0 = getTargetStatus(0);
    if (B0 == Boolean.TRUE) {
      const0.propagate();
    } else if (B0 == Boolean.FALSE) {
      oppositeConst0.propagate();
    } else {// B0 == null
      if (getStatus(1) == null) {
        checkStatus(1);
      }
    }
    Boolean B1 = getTargetStatus(1);
    if (B1 == Boolean.TRUE) {
      const1.propagate();
    } else if (B1 == Boolean.FALSE) {
      oppositeConst1.propagate();
    } else {// B1 == null
      if (getStatus(0) == null) {
        checkStatus(0);
      }
    }
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
    if (B0 == Boolean.TRUE) {
      return B1;
    } else if (B1 == Boolean.TRUE) {
      return B0;
    } else if ((B0 == Boolean.FALSE) && (B1 == Boolean.FALSE)) {
      return Boolean.TRUE;
    } else {
      return null;
    }
  }

  public boolean isSatisfied() {
    return (const0.isSatisfied() == const1.isSatisfied());
  }

  public boolean isConsistent() {
    throw new UnsupportedOperationException();
  }

  public boolean isEquivalentTo(Constraint compareTo) {
    if (compareTo instanceof Equiv) {
      Equiv c = (Equiv) compareTo;
      return ((this.const0.isEquivalentTo(c.const0) && this.const1.isEquivalentTo(c.const1)) ||
          (this.const0.isEquivalentTo(c.const1) && this.const1.isEquivalentTo(c.const0)));
    } else {
      return false;
    }
  }

}
