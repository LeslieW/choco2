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
package choco.cp.solver.constraints.integer.channeling;

import choco.cp.solver.variables.integer.IntVarEvent;
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
public class ReifiedLargeAnd  extends AbstractLargeIntSConstraint {


    /**
     * A pointer to one of the boolean variable that is not yet instantiated
     * or assigned to true if b is true.
     */
    protected int lit1 = Integer.MAX_VALUE;

    protected int lit2 = Integer.MAX_VALUE;


    public static IntDomainVar[] makeTable(IntDomainVar b, IntDomainVar[] vars) {
        IntDomainVar[] vs = new IntDomainVar[vars.length + 1];
        vs[0] = b;
        System.arraycopy(vars, 0, vs, 1, vars.length);
        return vs;
    }

    /**
     * A constraint to ensure :
     * b = AND_{i} vars[i]
     *
     * @param vars boolean variables, first one is the reified variable
     */
    public ReifiedLargeAnd(IntDomainVar[] vars) {
        super(vars);
    }

    @Override
    public int getFilteredEventMask(int idx) {
        return IntVarEvent.INSTINTbitvector;
    }

    @Override
    public void awake() throws ContradictionException {
        lit1 = Integer.MAX_VALUE;
        lit2 = Integer.MAX_VALUE;
        propagate();
    }

    public void initFilterFrombOne() throws ContradictionException {
        boolean updateLit1 = true;
        boolean updateLit2 = true;

        if (lit1 != Integer.MAX_VALUE && vars[lit1].fastCanBeInstantiatedTo(0))
            updateLit1 = false;
        if (lit2 != Integer.MAX_VALUE && vars[lit2].fastCanBeInstantiatedTo(0))
            updateLit2 = false;

        if (updateLit1 || updateLit2) {
            for (int i = 1; i < vars.length; i++) {
                if (vars[i].isInstantiatedTo(0)) {
                    vars[0].instantiate(0, cIndices[0]);
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
            if (vars[0].isInstantiatedTo(0)) {
                if (lit1 == Integer.MAX_VALUE) {
                    this.fail();
                } else if (lit2 == Integer.MAX_VALUE) {
                    vars[lit1].instantiate(0, cIndices[lit1]);
                }
            } else {
                if (lit1 == Integer.MAX_VALUE &&
                        lit2 == Integer.MAX_VALUE) {
                    vars[0].instantiate(1, cIndices[0]);
                }
            }
        }
    }

    public void propagate() throws ContradictionException {
        if (vars[0].isInstantiatedTo(1)) {
            for (int i = 1; i < vars.length; i++) {
                vars[i].instantiate(1, cIndices[i]);
            }
        } else {
            initFilterFrombOne();
        }
    }

    @Override
    public void awakeOnInst(int idx) throws ContradictionException {
        int val = vars[idx].getVal();
        if (idx == 0) {
            if (val == 1) {
                for (int i = 1; i < vars.length; i++) {
                    vars[i].instantiate(1, cIndices[i]);
                }
            } else {
                if (vars[lit1].isInstantiatedTo(1)) {
                    vars[lit2].instantiate(0, cIndices[lit2]);
                }
                if (vars[lit2].isInstantiatedTo(1)) {
                    vars[lit1].instantiate(0, cIndices[lit1]);
                }
            }
        } else {
            if (val == 0) {
                vars[0].instantiate(0, cIndices[0]);
                setEntailed();
            } else {
                if (idx == lit1) {

                    for (int i = 1; i < vars.length; i++) {
                        if (i != lit2 && vars[i].fastCanBeInstantiatedTo(0)) {
                            lit1 = i;
                            break;
                        }
                    }
                    if (lit1 == idx) {
                        if (vars[0].isInstantiatedTo(0)) {
                            vars[lit2].instantiate(0, cIndices[lit2]);
                        } else if (vars[lit2].isInstantiatedTo(1)) {
                            vars[0].instantiate(1, cIndices[0]);
                        }
                    }

                } else if (idx == lit2) {

                    for (int i = 1; i < vars.length; i++) {
                        if (i != lit1 && vars[i].fastCanBeInstantiatedTo(0)) {
                            lit2 = i;
                            break;
                        }
                    }
                    if (lit2 == idx) {
                        if (vars[0].isInstantiatedTo(0)) {
                            vars[lit1].instantiate(0, cIndices[lit1]);
                        } else if (vars[lit1].isInstantiatedTo(1)) {
                            vars[0].instantiate(1, cIndices[0]);
                        }
                    }
                }
            }
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

    @Override
    public boolean isSatisfied(int[] tuple) {
        if (tuple[0] == 1) {
            for (int i = 1; i < tuple.length; i++) {
                if (tuple[i] != 1) return false;
            }
            return true;
        } else {
            for (int i = 1; i < tuple.length; i++) {
                if (tuple[i] == 0) return true;
            }
            return false;
        }
    }

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