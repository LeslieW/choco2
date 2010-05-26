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
package choco.cp.solver.constraints.integer.bool;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.integer.AbstractBinIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 26 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*
* maintain v1 AND v2 where v1 and v2 are boolean variables
 * i.e variables of domain {0,1}
*/
public final class BinAnd extends AbstractBinIntSConstraint {

    BinAnd(IntDomainVar v1, IntDomainVar v2) {
        super(v1, v2);
    }

    @Override
    public int getFilteredEventMask(int idx) {
        return IntVarEvent.NO_MASK;
    }

    public void propagate() throws ContradictionException {
        v0.instantiate(1, this, false);
        v1.instantiate(1, this, false);
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
    public void awakeOnRem(int varIdx, int val) throws ContradictionException {
    }

    @Override
    public boolean isSatisfied(int[] tuple) {
        return tuple[0] == 1 && tuple[1] == 1;
    }

    @Override
    public Boolean isEntailed() {
        if (v0.isInstantiatedTo(1) &&
                v1.isInstantiatedTo(1))
            return Boolean.TRUE;
        else if (v0.isInstantiatedTo(0) ||
                v1.isInstantiatedTo(0))
            return Boolean.FALSE;
        else return null;
    }

    /**
     * Get the opposite constraint
     *
     * @return the opposite constraint  @param solver
     */
    @Override
    public AbstractSConstraint<IntDomainVar> opposite(final Solver solver) {
        return BooleanFactory.nand(solver.getEnvironment(), vars);
    }
}
