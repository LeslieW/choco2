/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */

package choco.cp.solver.constraints.integer;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.propagation.VarEvent;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class Element extends AbstractBinIntSConstraint {
    int[] lval;
    int cste;

    public Element(IntDomainVar index, int[] values, IntDomainVar var, int offset) {
        super(index, var);
        this.lval = values;
        this.cste = offset;
    }

    public Element(IntDomainVar index, int[] values, IntDomainVar var) {
        this(index, values, var, 0);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return "Element";
    }

    public int getFilteredEventMask(int idx) {
        if (idx == 0)
            return IntVarEvent.INSTINTbitvector + IntVarEvent.REMVALbitvector;
        else return IntVarEvent.REMVALbitvector;
    }

    /**
     * <i>Propagation:</i>
     * Propagating the constraint until local consistency is reached.
     *
     * @throws choco.kernel.solver.ContradictionException
     *          contradiction exception
     */

    public void propagate() throws ContradictionException {
        this.updateIndexFromValue();
        this.updateValueFromIndex();
    }

    public String pretty() {
        return (this.v1.pretty() + " = nth(" + this.v0.pretty() + ", " + StringUtils.pretty(this.lval) + ")");
    }

    protected void updateValueFromIndex() throws ContradictionException {
        int minVal = Integer.MAX_VALUE;
        int maxVal = Integer.MIN_VALUE;
        DisposableIntIterator iter = this.v0.getDomain().getIterator();
        for (; iter.hasNext();) {
            int index = iter.next();
            if (minVal > this.lval[index + cste]) minVal = this.lval[index + cste];
            if (maxVal < this.lval[index + cste]) maxVal = this.lval[index + cste];
        }
        iter.dispose();
        this.v1.updateInf(minVal, this.cIdx1);
        this.v1.updateSup(maxVal, this.cIdx1);

        // todo : <hcambaza> : why it does not perform AC on the value variable ?
    }

    protected void updateIndexFromValue() throws ContradictionException {
        int minFeasibleIndex = Math.max(0 - cste, this.v0.getInf());
        int maxFeasibleIndex = Math.min(this.v0.getSup(), lval.length - 1 - cste);
        int cause = this.v1.hasEnumeratedDomain() ? this.cIdx0 : VarEvent.NOCAUSE;

        while ((this.v0.canBeInstantiatedTo(minFeasibleIndex))
                && !(this.v1.canBeInstantiatedTo(lval[minFeasibleIndex + this.cste])))
            minFeasibleIndex++;
        this.v0.updateInf(minFeasibleIndex, cause);

        while ((this.v0.canBeInstantiatedTo(maxFeasibleIndex))
                && !(this.v1.canBeInstantiatedTo(lval[maxFeasibleIndex + this.cste])))
            maxFeasibleIndex--;
        this.v0.updateSup(maxFeasibleIndex, cause);

        if (this.v0.hasEnumeratedDomain()) {
            for (int i = minFeasibleIndex + 1; i <= maxFeasibleIndex - 1; i++) {
                if (this.v0.canBeInstantiatedTo(i) && !(this.v1.canBeInstantiatedTo(this.lval[i + this.cste])))
                    this.v0.removeVal(i, cause);
            }
        }
    }

    public void awake() throws ContradictionException {
        this.updateIndexFromValue();
        this.updateValueFromIndex();
    }

    public void awakeOnInst(int i) throws ContradictionException {
        if (i == 0)
            this.v1.instantiate(this.lval[this.v0.getVal() + this.cste], this.cIdx1);
//    else
//      this.updateIndexFromValue();
    }

    public void awakeOnRem(int i, int x) throws ContradictionException {
        if (i == 0)
            this.updateValueFromIndex();
        else
            this.updateIndexFromValue();
    }

    public Boolean isEntailed() {
        if (this.v1.isInstantiated()) {
            boolean allVal = true;
            boolean oneVal = false;
            DisposableIntIterator iter = this.v0.getDomain().getIterator();
            for (; iter.hasNext();) {
                int val = iter.next();
                boolean b = (val + this.cste) >= 0
                        && (val + this.cste) < this.lval.length
                        && this.lval[val + this.cste] == this.v1.getVal();
                allVal &= b;
                oneVal |= b;
            }
            iter.dispose();
            if (allVal) return Boolean.TRUE;
            if (oneVal) return null;
        } else {
            boolean b = false;
            DisposableIntIterator iter = this.v0.getDomain().getIterator();
            while (iter.hasNext() && !b) {
                int val = iter.next();
                if ((val + this.cste) >= 0 &&
                        (val + this.cste) < this.lval.length) {
                    b |= this.v1.canBeInstantiatedTo(this.lval[val + this.cste]);
                }
            }
            iter.dispose();
            if (b) return null;
        }
        return Boolean.FALSE;
    }

    public boolean isSatisfied(int[] tuple) {
        if (tuple[0] + this.cste >= lval.length ||
            tuple[0] + this.cste < 0) return false;
        return this.lval[tuple[0] + this.cste] == tuple[1];
    }
}
