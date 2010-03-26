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
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 27 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public final class LargeXnor extends AbstractLargeIntSConstraint {

    /**
     * A constraint to ensure :
     * b = XNOR_{i} vars[i]
     *
     * @param vars boolean variables
     */
    public LargeXnor(IntDomainVar[] vars) {
        super(vars);
    }

    @Override
    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector;
        // return 0x0B;
    }

    /**
     * Default initial propagation: full constraint re-propagation.
     */
    @Override
    public void awake() throws ContradictionException {
        for(int i = 0; i < vars.length; i++){
            if(vars[i].isInstantiated()){
                filter(i);
                break;
            }
        }
    }

    private void filter(int idx) throws ContradictionException {
        int val = vars[idx].getVal();
        for(int i = 0; i < vars.length; i++){
            if(idx!=i){
                vars[i].instantiate(val, this, false);
            }
        }
    }

    @Override
    public void propagate() throws ContradictionException {
    }

    @Override
    public void awakeOnInst(int idx) throws ContradictionException {
        filter(idx);
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
            if (aTuple != tuple[0]) return false;
        }
        return true;
    }

    @Override
    public Boolean isEntailed() {
        for (IntDomainVar var : vars) {
            if (var.isInstantiated()){
                if(var.getVal()!=vars[0].getVal()){
                    return Boolean.FALSE;
                }
            }else{
                return null;
            }
        }
        return Boolean.TRUE;
    }
}
