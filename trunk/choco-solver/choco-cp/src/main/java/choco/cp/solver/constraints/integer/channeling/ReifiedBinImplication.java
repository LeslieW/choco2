/* ************************************************
*           _       _                            *
*          |  °(..)  |                           *
*          |_  J||L _|        CHOCO solver       *
*                                                *
*     Choco is a java library for constraint     *
*     satisfaction problems (CSP), constraint    *
*     programming (CP) and explanation-based     *
*     constraint solving (e-CP). It is built     *
*     on a event-based propagation mechanism     *
*     with backtrackable structures.             *
*                                                *
*     Choco is an open-source software,          *
*     distributed under a BSD licence            *
*     and hosted by sourceforge.net              *
*                                                *
*     + website : http://choco.emn.fr            *
*     + support : choco@emn.fr                   *
*                                                *
*     Copyright (C) F. Laburthe,                 *
*                   N. Jussien    1999-2009      *
**************************************************/
package choco.cp.solver.constraints.integer.channeling;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractTernIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 2 nov. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class ReifiedBinImplication extends AbstractTernIntSConstraint {

    /**
     * A constraint to ensure :
     * b = v1 xnor v2
     */
    public ReifiedBinImplication(IntDomainVar b, IntDomainVar v1, IntDomainVar v2) {
        super(b, v1, v2);
    }

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector;
    }

    private boolean updateOnV0(int val) throws ContradictionException {
        boolean mod = false;
        if (val == 0) {
            mod = mod | v1.instantiate(1, cIdx1);
            mod |=v2.instantiate(0, cIdx2);
        } else {
            if (v1.isInstantiatedTo(0)) {
                setEntailed();
            } else if (v1.isInstantiatedTo(1)) {
                mod = mod | v2.instantiate(1, cIdx2);
            }
        }
        return mod;
    }

    private void updateOnV1(int val) throws ContradictionException {
        if (val == 0) {
            v0.instantiate(1, cIdx0);
        } else {
            if (v0.isInstantiated()) {
                v2.instantiate(v0.getVal(), cIdx2);
            } else if (v2.isInstantiated()) {
                v0.instantiate(v2.getVal(), cIdx0);
            }
        }
    }

    private void updateOnV2(int val) throws ContradictionException {
        if (val == 0) {
            if (v0.isInstantiated()) {
                v1.instantiate(Math.abs(v0.getVal() - 1), cIdx1);
            } else if (v1.isInstantiated()) {
                v0.instantiate(Math.abs(v1.getVal() - 1), cIdx0);
            }
        } else {
            v0.instantiate(1, cIdx0);
            setEntailed();
        }
    }

    public void propagate() throws ContradictionException {
        if (v0.isInstantiated()) {
            updateOnV0(v0.getVal());
        }
        if (v1.isInstantiated()) {
            updateOnV1(v1.getVal());
        }
        if (v2.isInstantiated()) {
            updateOnV2(v2.getVal());
        }
    }


    @Override
    public void awakeOnInst(int idx) throws ContradictionException {
        switch (idx) {
            case 0:
                updateOnV0(v0.getVal());
                break;
            case 1:
                updateOnV1(v1.getVal());
                break;
            case 2:
                updateOnV2(v2.getVal());
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
        if(tuple[0] == 0){
            return tuple[1] == 1 && tuple[2] == 0;
        }else{
            return tuple[1] <= tuple[2];
        }
    }

    public Boolean isEntailed() {

        return null;
    }
}
