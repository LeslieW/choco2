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

    public void propagate() throws ContradictionException {
        if (v0.isInstantiated()) {
            v2.instantiate(v0.getVal(), cIdx2);
            setEntailed();
        } else if (v2.isInstantiated()) {
            v0.instantiate(v2.getVal(), cIdx0);
            setEntailed();
        }
    }


    @Override
    public void awakeOnInst(int idx) throws ContradictionException {
        switch (idx) {
            case 0:
                v2.instantiate(v0.getVal(), cIdx2);
                setEntailed();
                break;
            case 2:
                v0.instantiate(v2.getVal(), cIdx0);
                setEntailed();
                break;
        }
    }

    @Override
    public void awakeOnInf(int varIdx) throws ContradictionException {}

    @Override
    public void awakeOnSup(int varIdx) throws ContradictionException {}

    @Override
    public void awakeOnBounds(int varIndex) throws ContradictionException {}

    @Override
    public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
    }


    public boolean isSatisfied(int[] tuple) {
        if (tuple[0] == 1) {
            return tuple[2] == 1;
        } else {
            return tuple[2] == 0;
        }
    }

    public Boolean isEntailed() {
        if (v0.isInstantiatedTo(1)
                && v1.isInstantiated() && v2.isInstantiated()) {
            return v2.getVal() == 1;
        } else if (v0.isInstantiatedTo(0)) {
            return v2.getVal() == 0;
        }
        return null;
    }
}
