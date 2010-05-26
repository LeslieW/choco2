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
package choco.cp.solver.constraints.integer.channeling;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractTernIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A constraint that ensures :
 * b = x1 xnor x2 ... Or xn
 * where b, and x1,... xn are boolean variables (of domain {0,1})
 */
public final class ReifiedBinXnor extends AbstractTernIntSConstraint {

     /**
     * A constraint to ensure :
     * b = v1 xnor v2
     */
    public ReifiedBinXnor(IntDomainVar b, IntDomainVar v1, IntDomainVar v2) {
        super(b, v1, v2);
    }

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINT_MASK;
    }

    public void propagate() throws ContradictionException {
        if (v0.isInstantiated()) {
            filter0();
        } else {
            if(v1.isInstantiated())filter(v1.getVal(), v2);
            if(v2.isInstantiated())filter(v2.getVal(), v1);
        }
    }

    private void filter0() throws ContradictionException {
        switch (v0.getVal()) {
            case 0:
                if (v1.isInstantiated()) {
                    v2.instantiate(Math.abs(v1.getVal() - 1), this, false);
                } else if (v2.isInstantiated()) {
                    v1.instantiate(Math.abs(v2.getVal() - 1), this, false);
                }
                break;
            case 1:
            if (v1.isInstantiated()) {
                    v2.instantiate(v1.getVal(), this, false);
                } else if (v2.isInstantiated()) {
                    v1.instantiate(v2.getVal(), this, false);
                }
            break;
        }
    }

    private void filter(int val, IntDomainVar v) throws ContradictionException {
        switch (val){
            case 0:
                if (v.isInstantiated()) {
                    v0.instantiate(Math.abs(v.getVal() - 1), this, false);
                }
                break;
            case 1:
                if (v.isInstantiated()) {
                    v0.instantiate(v.getVal(), this, false);
                }
                break;
        }
    }


    @Override
    public void awakeOnInst(int idx) throws ContradictionException {
        switch (idx) {
            case 0:
                filter0();
                break;
            case 1:
                filter(v1.getVal(), v2);
                break;
            case 2:
                filter(v2.getVal(), v1);
                break;
        }
    }

    @Override
    public void awakeOnInf(int varIdx) throws ContradictionException {

    }
    @Override
    public void awakeOnSup(int varIdx) throws ContradictionException {
    }
    @Override
    public void awakeOnBounds(int varIndex) throws ContradictionException {
    }
    @Override
    public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
    }


    public boolean isSatisfied(int[] tuple) {
        if (tuple[0] == 1) {
            return tuple[1] == tuple[2];
        } else {
            return tuple[1] != tuple[2];
        }
    }

    public Boolean isEntailed() {
        if(v0.isInstantiatedTo(1)
                && v1.isInstantiated() && v2.isInstantiated()){
            return v1.getVal() == v2.getVal();
        }else if(v0.isInstantiatedTo(0)){
            return v1.getVal() != v2.getVal();
        }
        return null;
    }

}