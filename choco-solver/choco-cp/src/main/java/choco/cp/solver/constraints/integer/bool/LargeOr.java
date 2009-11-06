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
public class LargeOr extends AbstractLargeIntSConstraint {


    /**
     * A pointer to one of the boolean variable that is not yet instantiated
     * or assigned to true if b is true.
     */
    protected int lit1 = Integer.MAX_VALUE;

    protected int lit2 = Integer.MAX_VALUE;


    /**
     * A constraint to ensure :
     * b = OR_{i} vars[i]
     *
     * @param vars
     */
    public LargeOr(IntDomainVar[] vars) {
        super(vars);
    }

    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector;
        // return 0x0B;
    }

    public void awake() throws ContradictionException {
        lit1 = Integer.MAX_VALUE;
        lit2 = Integer.MAX_VALUE;
        propagate();
    }


    public void initFilterFrombOne() throws ContradictionException {
        boolean updateLit1 = true;
        boolean updateLit2 = true;

        if (lit1 != Integer.MAX_VALUE && vars[lit1].fastCanBeInstantiatedTo(1))
            updateLit1 = false;
        if (lit2 != Integer.MAX_VALUE && vars[lit2].fastCanBeInstantiatedTo(1))
            updateLit2 = false;
        
        if (updateLit1 || updateLit2) {
            for (int i = 0; i < vars.length; i++) {
                if (vars[i].isInstantiatedTo(1)) {
                    setEntailed();
                } else if (!vars[i].isInstantiated()) {
                    if (updateLit1 && lit1 > i) {
                        lit1 = i;
                    } else if (updateLit2 && lit2 > i) {
                        lit2 = i;
                        break;
                    }
                }
            }
            if (lit1 == Integer.MAX_VALUE) {
                this.fail();
            } else if (lit2 == Integer.MAX_VALUE) {
                vars[lit1].instantiate(1, cIndices[lit1]);
            }
        }
    }


    public void propagate() throws ContradictionException {
        initFilterFrombOne();
    }

    @Override
    public void awakeOnInst(int idx) throws ContradictionException {
        int val = vars[idx].getVal();
        if (val == 1) {
            setEntailed();
        } else {
            if (idx == lit1) {
                for (int i = 0; i < vars.length; i++) {
                    if (i != lit2 && vars[i].fastCanBeInstantiatedTo(1)) {
                        lit1 = i;
                        break;
                    }
                }
                if (lit1 == idx) {
                    vars[lit2].instantiate(1, cIndices[lit2]);
                }

            } else if (idx == lit2) {
                for (int i = 0; i < vars.length; i++) {
                    if (i != lit1 && vars[i].fastCanBeInstantiatedTo(1)) {
                        lit2 = i;
                        break;
                    }
                }
                if (lit2 == idx) {
                    vars[lit1].instantiate(1, cIndices[lit1]);
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
