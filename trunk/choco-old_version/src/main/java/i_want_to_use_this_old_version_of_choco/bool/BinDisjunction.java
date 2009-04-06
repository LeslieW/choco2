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
 * @deprecated see Reifed package
 */
public class BinDisjunction extends AbstractBinBoolConstraint {

    public BinDisjunction(AbstractConstraint c1, AbstractConstraint c2) {
        super(c1, c2);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Pretty print of the constraint.
     */

    public String pretty() {
        return " (" + const0.pretty() + ") or (" + const1.pretty() + ") ";
    }

// this function is always called on a constraint that we want to propagate (either a root constraint
    // or a subconstraint that now needs to be propagated, eg such that targetStatus=true)
    protected void checkStatus(int i) throws ContradictionException {
        assert (getStatus(i) == null);
        AbstractConstraint c0 = ((i == 0) ? const0 : const1);
        Boolean b = c0.isEntailed();
        Boolean tgtb = getTargetStatus(i);
        if (b != null) {
            if (tgtb != null) {
                if (b != tgtb) {
                    this.fail();
                }
            }
            setStatus(i, b.booleanValue());
            // note (v1.02): below, we may already have targetStatus2=true without having status1=false
            //   (for instance if the disjunction has been settled within a choice point)
            //    therefore we explicitly check that the targetStatus is not already known before setting it to true
            if ((b == Boolean.FALSE) && (getTargetStatus(1 - i) == null)) {
                setTargetStatus(1 - i, true);
                AbstractConstraint c1 = ((i == 0) ? const1 : const0);
                c1.awake();
            }
        }
    }

    /**
     * Default propagation on improved lower bound: propagation on domain revision.
     */

    public void awakeOnInf(int i) throws ContradictionException {
        if (i < offset) {
            if (getStatus(0) == null) {
                if (getTargetStatus(0) == Boolean.TRUE) {
                    ((IntConstraint) const0).awakeOnInf(i);
                } else {
                    // if the target status is not TRUE, it must be null (never FALSE for disjunctions).
                    checkStatus(0);
                }
            }
        } else if (getStatus(1) == null) {
            if (getTargetStatus(1) == Boolean.TRUE) {
                ((IntConstraint) const1).awakeOnInf(i - offset);
            } else {
                // if the target status is not TRUE, it must be null (never FALSE for disjunctions).
                checkStatus(1);
            }
        }
    }

    public void awakeOnSup(int i) throws ContradictionException {
        if (i < offset) {
            if (getStatus(0) == null) {
                if (getTargetStatus(0) == Boolean.TRUE) {
                    ((IntConstraint) const0).awakeOnSup(i);
                } else {
                    // if the target status is not TRUE, it must be null (never FALSE for disjunctions).
                    checkStatus(0);
                }
            }
        } else if (getStatus(1) == null) {
            if (getTargetStatus(1) == Boolean.TRUE) {
                ((IntConstraint) const1).awakeOnSup(i - offset);
            } else {
                // if the target status is not TRUE, it must be null (never FALSE for disjunctions).
                checkStatus(1);
            }
        }
    }

    public void awakeOnBounds(int i) throws ContradictionException {
        if (i < offset) {
            if (getStatus(0) == null) {
                if (getTargetStatus(0) == Boolean.TRUE) {
                    ((IntConstraint) const0).awakeOnBounds(i);
                } else {
                    // if the target status is not TRUE, it must be null (never FALSE for disjunctions).
                    checkStatus(0);
                }
            }
        } else if (getStatus(1) == null) {
            if (getTargetStatus(1) == Boolean.TRUE) {
                ((IntConstraint) const1).awakeOnBounds(i - offset);
            } else {
                // if the target status is not TRUE, it must be null (never FALSE for disjunctions).
                checkStatus(1);
            }
        }
    }

    public void awakeOnInst(int i) throws ContradictionException {
        if (i < offset) {
            if (getStatus(0) == null) {
                if (getTargetStatus(0) == Boolean.TRUE) {
                    ((IntConstraint) const0).awakeOnInst(i);
                } else {
                    // if the target status is not TRUE, it must be null (never FALSE for disjunctions).
                    checkStatus(0);
                }
            }
        } else if (getStatus(1) == null) {
            if (getTargetStatus(1) == Boolean.TRUE) {
                ((IntConstraint) const1).awakeOnInst(i - offset);
            } else {
                // if the target status is not TRUE, it must be null (never FALSE for disjunctions).
                checkStatus(1);
            }
        }
    }

    public void awakeOnRemovals(int i, IntIterator deltaDomain) throws ContradictionException {
        if (i < offset) {
            if (getStatus(0) == null) {
                if (getTargetStatus(0) == Boolean.TRUE) {
                    ((IntConstraint) const0).awakeOnRemovals(i, deltaDomain);
                } else {
                    // if the target status is not TRUE, it must be null (never FALSE for disjunctions).
                    checkStatus(0);
                }
            }
        } else if (getStatus(1) == null) {
            if (getTargetStatus(1) == Boolean.TRUE) {
                ((IntConstraint) const1).awakeOnRemovals(i - offset, deltaDomain);
            } else {
                // if the target status is not TRUE, it must be null (never FALSE for disjunctions).
                checkStatus(1);
            }
        }
    }

    public void awakeOnRem(int i, int x) throws ContradictionException {
        if (i < offset) {
            if (getStatus(0) == null) {
                if (getTargetStatus(0) == Boolean.TRUE) {
                    ((IntConstraint) const0).awakeOnRem(i, x);
                } else {
                    // if the target status is not TRUE, it must be null (never FALSE for disjunctions).
                    checkStatus(0);
                }
            }
        } else if (getStatus(1) == null) {
            if (getTargetStatus(1) == Boolean.TRUE) {
                ((IntConstraint) const1).awakeOnRem(i - offset, x);
            } else {
                // if the target status is not TRUE, it must be null (never FALSE for disjunctions).
                checkStatus(1);
            }
        }
    }

    public boolean isSatisfied() {
        assert ((getStatus(0) != null) && (getStatus(1) != null));
        return (getStatus(0).booleanValue() || getStatus(1).booleanValue());
//    return ((const0.isSatisfied()) ||(const1.isSatisfied()));
    }

    public boolean isConsistent() {
        throw new UnsupportedOperationException();
    }

    public Boolean isEntailed() {
        Boolean st0 = getStatus(0);
        if (st0 == null) {
            st0 = const0.isEntailed();
            if (st0 != null) {
                setStatus(0, st0.booleanValue());
            }
        }
        Boolean st1 = getStatus(1);
        if (st1 == null) {
            st1 = const1.isEntailed();
            if (st1 != null) {
                setStatus(1, st1.booleanValue());
            }
        }
        if (st0 == Boolean.TRUE) {
            return Boolean.TRUE;
        } else if (st1 == Boolean.TRUE) {
            return Boolean.TRUE;
        } else if ((st0 == Boolean.FALSE) && (st1 == Boolean.FALSE)) {
            return Boolean.FALSE;
        } else {
            return null;
        }
    }

    public void propagate() throws ContradictionException {
        if (getStatus(0) == null) {
            checkStatus(0);
        }
        if (getStatus(1) == null) {
            checkStatus(1);
        }
    }

    public AbstractConstraint opposite() {
        return new BinConjunction(const0.opposite(), const1.opposite());
    }

    public boolean isEquivalentTo(Constraint compareTo) {
        if (compareTo instanceof BinDisjunction) {
            BinDisjunction c = (BinDisjunction) compareTo;
            return ((this.const0.isEquivalentTo(c.const0) && this.const1.isEquivalentTo(c.const1)) ||
                    (this.const0.isEquivalentTo(c.const1) && this.const1.isEquivalentTo(c.const0)));
        } else {
            return false;
        }
    }

}
