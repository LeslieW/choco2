package i_want_to_use_this_old_version_of_choco.integer.constraints;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomain;

/**
 * The absolute constraint X = |Y| is a binary constraint
 * with X = v0 and Y = v1
 */
public class Absolute extends AbstractBinIntConstraint {

    /**
     * Constructs the constraint with the specified variables.
     *
     * @param x0 first IntDomainVar
     * @param x1 second IntDomainVar
     */

    public Absolute(IntDomainVar x0, IntDomainVar x1) {
        super(x0, x1);
    }


    /**
     * The initial propagation consist in
     * enforcing X to be positive
     * executing all standard propagation methods
     * executing specific propagation methods
     */
    public void propagate() throws ContradictionException {
        v0.updateInf(0, cIdx0);
        if (v0.getDomain().isEnumerated()) {
            IntDomain dom0 = v0.getDomain();
            for (int valeur = v0.getInf();
                 valeur <= v0.getSup(); valeur = dom0.getNextValue(valeur)) {
                if (!v1.canBeInstantiatedTo(valeur) &&
                        !v1.canBeInstantiatedTo(-valeur)) {
                    v0.removeVal(valeur, cIdx0);
                }
            }
        } else {
            awakeOnInf(1);
            awakeOnSup(1);
        }
        if (v1.getDomain().isEnumerated()) {
            IntDomain dom1 = v1.getDomain();
            for (int valeur = v1.getInf();
                 valeur <= v1.getSup(); valeur = dom1.getNextValue(valeur)) {
                if (!v0.canBeInstantiatedTo(valeur) &&
                        !v0.canBeInstantiatedTo(-valeur)) {
                    v1.removeVal(valeur, cIdx1);
                    v1.removeVal(-valeur, cIdx1);
                }
            }
        } else {
            awakeOnInf(0);
            awakeOnSup(0);
        }

    }

    /**
     * If X.inf increases, values from -X.inf to X.inf are forbidden for Y
     * If Y.inf increases, it depends on the situation
     *
     * @param idx
     * @throws ContradictionException
     */
    public void awakeOnInf(int idx) throws ContradictionException {
//    // v0 = |v1| + cste
        if (idx == 0) { // absolute variable lower bound is increased
//      values from -v0.inf - cste to v0.inf - cste are forbidden for v1
            if (v1.getInf() >= 0) // v1 >= 0 => v1.inf = v0.inf
                v1.updateInf(v0.getInf(), cIdx1);
            else if (v1.getSup() <= 0) //v1 <= 0 => v1.sup = - v0.inf
                v1.updateSup(-v0.getInf(), cIdx1);
            else if (v1.getInf() > -v0.getInf())
                v1.updateInf(v0.getInf(), cIdx1);
            else if (v1.getSup() < v0.getInf())
                v1.updateSup(-v0.getInf(), cIdx1);
            else if (v1.getDomain().isEnumerated()) {
                v1.removeInterval(-v0.getInf() + 1, v0.getInf() - 1, cIdx1);
            }
        } else { // free variable lower bound is increased
            if (!v1.getDomain().isEnumerated()
                    && v1.getInf() > -v0.getInf() /* v0.getInf() > 0 by definition */
                    && v1.getInf() < v0.getInf() /* v0.getInf() > cste by fefinition */) {
                v1.updateInf(v0.getInf(), -1);
            } else if (v1.getInf() >= 0) {
                v0.updateInf(v1.getInf(), cIdx0);
                v0.updateSup(v1.getSup(), cIdx0);
                detectSymetricalHoles(v1.getInf(), v1.getSup());
            } else if (v1.getSup() <= 0) {
                v0.updateSup(-v1.getInf(), cIdx0);
                v0.updateInf(-v1.getSup(), cIdx0);
            } else {
                v0.updateSup(Math.max(-v1.getInf(), v1.getSup()), cIdx0);
                detectSymetricalHoles(-v1.getInf(), v1.getSup());
            }
        }
        //propagate();
    }

    /**
     * If X.sup decreases, Y is limited to (-(X.sup) ..  X.sup)
     * If Y.sup decreases, it depends on the situation
     */
    public void awakeOnSup(int idx) throws ContradictionException {
        if (idx == 0) {
            v1.updateSup(v0.getSup(), cIdx1);
            v1.updateInf(-v0.getSup(), cIdx1);
        } else {
            if (!v1.getDomain().isEnumerated() &&
                    v1.getSup() > -v0.getInf() &&
                    v1.getSup() < v0.getInf()) {
                // Y.sup cannot remain in the gap (-(X.inf) .. X.inf)
                // -> finish to cross this gap before calling back awakeOnSup
                //    (because the cause is set to -1)
                v1.updateSup(-v0.getInf(), -1);
            } else if (v1.getInf() >= 0) {
                v0.updateSup(v1.getSup(), cIdx0);
                v0.updateInf(v1.getInf(), cIdx0);
            } else if (v1.getSup() <= 0) {
                v0.updateInf(-v1.getSup(), cIdx0); // Y < 0 => x.inf = -(y.sup)
                v0.updateSup(-v1.getInf(), cIdx0); // Y < 0 => x.sup = -(y.inf)
                detectSymetricalHoles(-v1.getSup(), -v1.getInf());
            } else {      // general case: x.sup = max(abs(y.inf),abs(y.sup))
                v0.updateSup(Math.max(-v1.getInf(), v1.getSup()), cIdx0);
                detectSymetricalHoles(v1.getSup(), -v1.getInf());
            }

        }

    }


    /**
     * When X is instantiated, Y is restricted to 2 values : X.value and -(X.value)
     * When Y is instantiated, X is instantiated to abs(Y)
     *
     * @param idx
     * @throws ContradictionException
     */
    public void awakeOnInst(int idx) throws ContradictionException {
        if (idx == 0) {
            int val = v0.getVal();
            if (!v1.canBeInstantiatedTo(val))
                v1.instantiate(-val, cIdx1);
            else if (!v1.canBeInstantiatedTo(-val))
                v1.instantiate(val, cIdx1);
            else {
                if (val >= 0) {
                    v1.updateSup(val, cIdx1);
                    v1.updateInf(-val, cIdx1);
                    v1.removeInterval(-val + 1, val - 1, cIdx1);
                } else {
                    v1.updateInf(val, cIdx1);
                    v1.updateSup(-val, cIdx1);
                    v1.removeInterval(val + 1, -val - 1, cIdx1);
                }
            }
        } else {
            v0.instantiate(Math.abs(v1.getVal()), cIdx0);
        }

    }


    /**
     * When a value is removed from the domain of X: then this value and its opposite are removed from the domain of Y.
     * When a value is removed from the domain of Y: IF its opposite is not in the domain of Y either,
     * then its absolute value is removed from the domain of X.
     *
     * @param idx
     * @param x
     * @throws ContradictionException
     */
    public void awakeOnRem(int idx, int x) throws ContradictionException {
        if (idx == 0) {
            if (x >= 0) {
                v1.removeVal(x, cIdx1);
                v1.removeVal(-x, cIdx1);
                updateMinFromHoles();
            }
        } else if (!v1.canBeInstantiatedTo(-x)) {
            v0.removeVal(Math.abs(x), cIdx0);
        }
    }

    /**
     * Try to detect symetrical holes
     * (ie value such that both value and -(value) are not in the domain of Y,
     * in which case abs(value) can be removed from the domain of X)
     */
    protected void detectSymetricalHoles(int inf, int sup) throws ContradictionException {
        if (v1.getDomain().isEnumerated()) {
            IntDomain dom0 = v0.getDomain();
            for (int valeur = Math.max(inf, v0.getInf());
                 valeur <= Math.min(sup, v0.getSup()); valeur = dom0.getNextValue(valeur)) {
                if (!v1.canBeInstantiatedTo(valeur) &&
                        !v1.canBeInstantiatedTo(-valeur)) {
                    v0.removeVal(valeur, cIdx0);
                }
            }
        }
    }

    /**
     * Deduce the minimum value of X from the eventual central gap in the domain of Y.
     *
     * @throws ContradictionException
     */
    protected void updateMinFromHoles() throws ContradictionException {
        if (v1.getDomain().isEnumerated() &&
                v1.getInf() < 0 &&
                v1.getSup() > 0) {
            int minPositiveValue = v1.getDomain().getNextValue(-1);
            int maxNegativeValue = v1.getDomain().getPrevValue(1);
            v0.updateInf(Math.min(-maxNegativeValue, minPositiveValue), cIdx0);
        }
    }

    /**
     * Checks if the listeners must be checked or must fail.
     */
    public Boolean isEntailed() {
        if (v0.getSup() < 0) return Boolean.FALSE;
        else if (v0.isInstantiated()) {
            if (v1.isInstantiated())
                return (v0.getVal() == Math.abs(v1.getVal()));
            else if (v1.getDomainSize() == 2 &&
                    v1.canBeInstantiatedTo(v0.getVal()) &&
                    v1.canBeInstantiatedTo(-v0.getVal()))
                return Boolean.TRUE;
            else if (!v1.canBeInstantiatedTo(v0.getVal()) &&
                    !v1.canBeInstantiatedTo(-v0.getVal()))
                return Boolean.FALSE;
            else return null;
        } else return null;
    }

    /**
     * Checks if the constraint is satisfied when the variables are instantiated.
     */

    public boolean isSatisfied(int[] tuple) {
        return Math.abs(tuple[0]) == tuple[1];
    }

    /**
     * tests if the constraint is consistent with respect to the current state of domains
     *
     * @return true iff the constraint is bound consistent (weaker than arc consistent)
     */
    public boolean isConsistent() {
        throw new UnsupportedOperationException("Absolute.isConsistent is not implemented!");
    }

    public AbstractConstraint opposite() {
        throw new UnsupportedOperationException("Absolute.opposite is not implemented!");
    }

    public final boolean isEquivalentTo(Constraint compareTo) {
        throw new UnsupportedOperationException("Absolute.isEquivalentTo is not implemented!");
    }

    public String pretty() {
        StringBuffer sb = new StringBuffer();
        sb.append(v0.toString());
        sb.append(" = |");
        sb.append(v1.toString());
        sb.append("| ");
        return sb.toString();
    }

}
