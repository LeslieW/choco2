/* ************************************************
*           _       _                            *
*          |  �(..)  |                           *
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
package choco.cp.solver.constraints.integer.bool;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 26 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class LargeAnd extends AbstractLargeIntSConstraint {

    /**
     * A constraint to ensure :
     * b = AND_{i} vars[i]
     *
     * @param vars boolean variables
     */
    public LargeAnd(IntDomainVar[] vars) {
        super(vars);
    }

    @Override
    public int getFilteredEventMask(int idx) {
        return 0;
    }

    public void propagate() throws ContradictionException {
        for(int i = 0; i < vars.length; i++){
            vars[i].instantiate(1, cIndices[i]);
        }
    }

    @Override
    public void awakeOnInst(int idx) throws ContradictionException {
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

    @Override
    public boolean isSatisfied(int[] tuple) {
        for (int aTuple : tuple) {
            if (aTuple == 0) return false;
        }
        return true;
    }

    @Override
    public Boolean isEntailed() {
        for (IntDomainVar var : vars) {
            if (var.isInstantiatedTo(0))
                return Boolean.FALSE;
        }
        for (IntDomainVar var : vars) {
            if (var.fastCanBeInstantiatedTo(1))
                return null;
        }
        return Boolean.TRUE;
    }
}