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
package choco.cp.solver.constraints.reified;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.propagation.listener.IntPropagator;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

/**
 * A constraint that allows to reify another constraint into a boolean value.
 * b = 1 <=> cons is satisfied
 * b = 0 <=> oppositeCons is satisfied
 * <p/>
 * cons and oppositeCons do not need to be really the constraint and its
 * opposite, it can be two different constraints as well
 */
public class ReifiedIntSConstraint extends ReifiedSConstraint<IntDomainVar, AbstractIntSConstraint> implements IntPropagator {

    //scopeCons[i] = j means that the i-th variable of cons is the j-th in reifiedIntConstraint
    protected int[] scopeCons;
    //scopeOCons[i] = j means that the i-th variable of oppositeCons is the j-th in reifiedIntConstraint
    protected int[] scopeOCons;

    @SuppressWarnings({"unchecked"})
    public static IntDomainVar[] makeTableVar(IntDomainVar bool,
                                              AbstractIntSConstraint cons, AbstractIntSConstraint oppcons) {
        HashSet<IntDomainVar> consV = new HashSet<IntDomainVar>(cons.getNbVars() + oppcons.getNbVars()+1);
        for (int i = 0; i < cons.getNbVars(); i++){
            consV.add(cons.getVar(i));
        }
        for (int i = 0; i < oppcons.getNbVars(); i++){
            consV.add(oppcons.getVar(i));
        }
        consV.add(bool);
        IntDomainVar[] vars = new IntDomainVar[consV.size()];
        consV.remove(bool);
        vars[0] = bool;
        int i = 1;
        for (IntDomainVar var: consV) {
            vars[i] = var;
            i++;
        }
        return vars;
    }

    /**
     * A constraint that allows to reify another constraint into a boolean value.
     * b = 1 <=> cons is satisfied
     * b = 0 <=> oppositeCons is satisfied
     * if the opposite methode of the constraint is not defined, use the other constructor
     * by giving yourself the opposite constraint !
     * @param bool reified variable
     * @param cons the reified constraint
     * @param solver
     */
    ReifiedIntSConstraint(final IntDomainVar bool, final AbstractIntSConstraint cons, final Solver solver) {
        super(makeTableVar(bool, cons, (AbstractIntSConstraint) cons.opposite(solver)),
                bool, cons, (AbstractIntSConstraint) cons.opposite(solver));
        init();
    }

    /**
     * A constraint that allows to reify another constraint into a boolean value.
     * b = 1 <=> cons is satisfied
     * b = 0 <=> oppositeCons is satisfied
     * <p/>
     * cons and oppositeCons do not need to be really the constraint and its
     * opposite, it can be two different constraints as well
     * @param bool reified variable
     * @param cons the reified constraint
     * @param oppositeCons the opposite reified constraint
     */
    ReifiedIntSConstraint(final IntDomainVar bool, final AbstractIntSConstraint cons, final AbstractIntSConstraint oppositeCons) {
        super(makeTableVar(bool, cons, oppositeCons),
                bool, cons, oppositeCons);
        init();
    }

    final void init() {
        tupleCons = new int[cons.getNbVars()];
        tupleOCons = new int[oppositeCons.getNbVars()];
        scopeCons = new int[cons.getNbVars()];
        scopeOCons = new int[oppositeCons.getNbVars()];
        for (int i = 0; i < cons.getNbVars(); i++) {
            final IntDomainVar v = cons.getVar(i);
            for (int j = 0; j < vars.length; j++) {
                if (v.equals(vars[j])) {
                    scopeCons[i] = j;
                    break;
                }
            }
        }
        for (int i = 0; i < oppositeCons.getNbVars(); i++) {
            final IntDomainVar v = oppositeCons.getVar(i);
            for (int j = 0; j < vars.length; j++) {
                if (v.equals(vars[j])) {
                    scopeOCons[i] = j;
                    break;
                }
            }
        }
    }


    @Override
    public final int getFilteredEventMask(final int idx) {
        if (vars[idx].hasEnumeratedDomain()) {
            return IntVarEvent.REMVAL_MASK;
        } else {
            return IntVarEvent.BOUNDS_MASK;
        }
    }


    public final void awakeOnInf(final int idx) throws ContradictionException {
        filter();
    }

    public final void awakeOnSup(final int idx) throws ContradictionException {
        filter();
    }

    public final void awakeOnInst(final int idx) throws ContradictionException {
        filter();
    }

    public final void awakeOnRem(final int idx, final int x) throws ContradictionException {
        filter();
    }

    public final void awakeOnRemovals(final int idx, final DisposableIntIterator deltaDomain) throws ContradictionException {
        filter();
    }

    public final void awakeOnBounds(final int varIndex) throws ContradictionException {
        filter();
    }

    //temporary data to store tuples
    int[] tupleCons;
    int[] tupleOCons;

    /**
     * TEMPORARY: if not overriden by the constraint, throws an error
     * to avoid bug using reified constraints in constraints
     * that have not been changed to fulfill this api yet !
     *
     * @param tuple value for each variable
     * @return true if the tuple satisfies the constraint
     */
    public boolean isSatisfied(final int[] tuple) {
        final int val = tuple[0];
        for (int i = 0; i < tupleCons.length; i++) {
            tupleCons[i] = tuple[scopeCons[i]];
        }
        if (val == 1) {
            return cons.isSatisfied(tupleCons);
        } else {
            for (int i = 0; i < tupleOCons.length; i++) {
                tupleOCons[i] = tuple[scopeOCons[i]];
            }
            return oppositeCons.isSatisfied(tupleOCons);
        }
    }
}
