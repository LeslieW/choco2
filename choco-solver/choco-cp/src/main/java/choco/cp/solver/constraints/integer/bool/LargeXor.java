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
public class LargeXor extends AbstractLargeIntSConstraint {

    /**
     * A constraint to ensure :
     * b = XOR_{i} vars[i]
     *
     * @param vars boolean variables
     */
    public LargeXor(IntDomainVar[] vars) {
        super(vars);
    }

    @Override
    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector;
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
        int val = Math.abs(vars[idx].getVal()-1);
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
        int cnt = 0;
        for (int i = 0; i < tuple.length && cnt <=1; i++ ) {
            if (tuple[i]== 1) cnt++;
        }
        return cnt==1;
    }

    @Override
    public Boolean isEntailed() {
        int cnt = 0;
        for (int i = 0; i < vars.length && cnt <= 1; i++) {
            if (vars[i].isInstantiated()){
                if(vars[i].getVal()==1){
                    cnt++;
                }
            }else{
                return null;
            }
        }
        return cnt==1;
    }
}