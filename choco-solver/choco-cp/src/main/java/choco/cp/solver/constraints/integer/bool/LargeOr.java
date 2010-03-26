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
package choco.cp.solver.constraints.integer.bool;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Aug 6, 2008
 * Since : Choco 2.0.0
 *
 */
public final class LargeOr extends AbstractLargeIntSConstraint {


    /**
     * Nb literals set to 0 (false).
     */
    private final IStateInt toZERO;


    /**
     * A constraint to ensure :
     * b = OR_{i} vars[i]
     *
     * @param vars
     * @param environment
     */
    public LargeOr(IntDomainVar[] vars, IEnvironment environment) {
        super(vars);
        toZERO = environment.makeInt(0);
    }

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector;
    }

    public void propagate() throws ContradictionException {
        int toZERO = 0;
        int lastIdx = 0;
        for(int i = 0; i < vars.length; i++){
            if(vars[i].isInstantiatedTo(1)){
                setEntailed();
                return;
            }else if(vars[i].isInstantiatedTo(0)){
                toZERO++;
            }else{
                lastIdx = i;
            }
        }
        if(toZERO == vars.length){
            this.fail();
        }else if((toZERO == vars.length - 1)){
            vars[lastIdx].instantiate(1, this, false);
            setEntailed();
            return;
        }
        this.toZERO.set(toZERO);
    }

    @Override
    public void awakeOnInst(int idx) throws ContradictionException {
        int val = vars[idx].getVal();
        switch (val){
            case 1:
                setEntailed();
                break;
            case 0:
                toZERO.add(1);
                // 1 var inconnue
                if(toZERO.get()>= vars.length-1){
                    filter();
                }
                break;
        }
    }

    private void filter() throws ContradictionException {
        int toZero = toZERO.get();
        int n = vars.length;
        if(toZero == n){
            this.fail();
        }else{
            for(int i = 0; i < n; i++){
                if(!vars[i].isInstantiated()){
                    vars[i].instantiate(1, this, false);
                    setEntailed();
                    break;
                }
                // speed up
                else if(!vars[(n-1)-i].isInstantiated()){
                    vars[(n-1)-i].instantiate(1, this, false);
                    setEntailed();
                    break;
                }
            }
        }
    }

    public void awakeOnInf(int varIdx) throws ContradictionException {
    }

    public void awakeOnSup(int varIdx) throws ContradictionException {
    }

    public void awakeOnBounds(int varIndex) throws ContradictionException {
    }

    public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {

    }

    public boolean isSatisfied(int[] tuple) {
        for (int i = 0; i < tuple.length; i++) {
            if (tuple[i] == 1) return true;
        }
        return false;
    }

    public Boolean isEntailed() {
        for (int i = 0; i < vars.length; i++) {
            if (vars[i].isInstantiatedTo(1))
                return Boolean.TRUE;
        }
        for (int i = 0; i < vars.length; i++) {
            if (vars[i].fastCanBeInstantiatedTo(1))
                return null;
        }
        return Boolean.FALSE;
    }

}
