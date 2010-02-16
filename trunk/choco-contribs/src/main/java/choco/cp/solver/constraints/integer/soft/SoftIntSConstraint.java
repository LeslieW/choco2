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
package choco.cp.solver.constraints.integer.soft;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.propagation.event.VarEvent;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 9 nov. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class SoftIntSConstraint extends AbstractLargeIntSConstraint {

    protected AbstractIntSConstraint cons;

    //scopeCons[i] = j means that the i-th variable of cons is the j-th in reifiedIntConstraint
    protected int[] scopeCons;

    //temporary data to store tuples
    int[] tupleCons;

    public static IntDomainVar[] makeTableVar(IntDomainVar bool, AbstractIntSConstraint cons) {
        HashSet<IntDomainVar> consV = new HashSet<IntDomainVar>();
        for (int i = 0; i < cons.getNbVars(); i++)
            consV.add(cons.getVar(i));
        consV.add(bool);
        IntDomainVar[] vars = new IntDomainVar[consV.size()];
        consV.remove(bool);
        vars[0] = bool;
        int i = 1;
        for (Object aConsV : consV) {
            IntDomainVar intDomainVar = (IntDomainVar) aConsV;
            vars[i] = intDomainVar;
            i++;
        }
        return vars;
    }

    /**
     * A constraint that allows to 'softy' another constraint into a distance value (boolean).
     * b = 0 <=> cons is satisfied
     * b = 1+ <=> cons is not satisfied
     * @param dist distance to satisfaction
     * @param cons the constraint to softy
     */
    public SoftIntSConstraint(IntDomainVar dist, AbstractIntSConstraint cons) {
        super(makeTableVar(dist, cons));
        this.cons = cons;
        init();
    }

    public void init() {
        tupleCons = new int[cons.getNbVars()];
        scopeCons = new int[cons.getNbVars()];
        for (int i = 0; i < cons.getNbVars(); i++) {
            IntDomainVar v = cons.getVar(i);
            for (int j = 0; j < vars.length; j++) {
                if (v.equals(vars[j])) {
                    scopeCons[i] = j;
                    break;
                }
            }
        }
    }

    public void filter() throws ContradictionException {
        if (vars[0].isInstantiated()) {
            filterReifiedConstraintFromBool();
        } else {
            filterReifiedConstraintFromCons();
        }
    }

    //assume that the distance is known
    public void filterReifiedConstraintFromBool() throws ContradictionException {
        if (vars[0].isInstantiatedTo(0)) {
            cons.awake();//propagate();
        }
    }

    public void filterReifiedConstraintFromCons() throws ContradictionException {
        Boolean isEntailed = cons.isEntailed();
        if (isEntailed != null) {
            if (isEntailed) {
                vars[0].instantiate(0, cIndices[0]);
            } else {
                vars[0].instantiate(1, VarEvent.domOverWDegIdx(cIndices[0]));//cIndices[0]);
            }
        }
    }


    @Override
    public int getFilteredEventMask(int idx) {
        if (vars[idx].hasEnumeratedDomain()) {
            return IntVarEvent.REMVALbitvector;
        } else {
            return IntVarEvent.BOUNDSbitvector;
        }
    }


    public void awakeOnInf(int idx) throws ContradictionException {
        filter();
    }

    public void awakeOnSup(int idx) throws ContradictionException {
        filter();
    }

    public void awakeOnInst(int idx) throws ContradictionException {
        filter();
    }

    public void awakeOnRem(int idx, int x) throws ContradictionException {
        filter();
    }

    public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
        filter();
    }


    public void awakeOnBounds(int varIndex) throws ContradictionException {
        filter();
    }

    public void propagate() throws ContradictionException {
        filter();
    }

    public void awake() throws ContradictionException {
        filter();
    }

    public void addListener(SConstraint thecons) {
        if (thecons instanceof SoftIntSConstraint) {
            SoftIntSConstraint rcons = (SoftIntSConstraint) thecons;
            addListener(rcons.cons);
        }
        int n = thecons.getNbVars();
        for (int i = 0; i < n; i++) {
            thecons.setConstraintIndex(i, getIndex((AbstractVar) thecons.getVar(i)));
        }
    }

    public int getIndex(AbstractVar v) {
        for (int i = 0; i < vars.length; i++) {
            if (vars[i] == v) return cIndices[i];
        }
        return -1; //should never go there !
    }


    public void addListener(boolean dynamicAddition) {
        super.addListener(dynamicAddition);
        addListener(cons);
    }

    public String pretty() {
        StringBuffer sb = new StringBuffer("( ");
        sb.append(vars[0].getName()).append(" =0 ");
        sb.append("<=>").append(cons.pretty()).append(" )");
        return sb.toString();
    }

    /**
     * TEMPORARY: if not overriden by the constraint, throws an error
     * to avoid bug using reified constraints in constraints
     * that have not been changed to fulfill this api yet !
     *
     * @param tuple value for each variable
     * @return true if the tuple satisfies the constraint
     */
    public boolean isSatisfied(int[] tuple) {
        int val = tuple[0];
        for (int i = 0; i < tupleCons.length; i++) {
            tupleCons[i] = tuple[scopeCons[i]];
        }
        if (val == 0) {
            return cons.isSatisfied(tupleCons);
        } else {
            return !cons.isSatisfied(tupleCons);
        }
    }
}
