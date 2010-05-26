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

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.propagation.listener.IntPropagator;
import choco.kernel.solver.propagation.listener.RealPropagator;
import choco.kernel.solver.propagation.listener.SetPropagator;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A constraint that allows to reify another constraint into a boolean value.
 * b = 1 <=> cons is satisfied
 * b = 0 <=> oppositeCons is satisfied
 * <p/>
 * cons and oppositeCons do not need to be really the constraint and its
 * opposite, it can be two different constraints as well
 */
public class ReifiedAllSConstraint extends ReifiedSConstraint<Var, AbstractSConstraint> 
        implements IntPropagator, SetPropagator, RealPropagator {

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
    ReifiedAllSConstraint(final IntDomainVar bool, final AbstractSConstraint cons, final Solver solver) {
        super(bool, cons, cons.opposite(solver));
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
    ReifiedAllSConstraint(final IntDomainVar bool, final AbstractSConstraint cons,
                                 final AbstractSConstraint oppositeCons) {
        super(bool, cons, oppositeCons);
    }

    /**
     * Default propagation on improved lower bound: propagation on domain revision.
     */
    @Override
    public void awakeOnInf(final int varIdx) throws ContradictionException {
        filter();
    }

    /**
     * Default propagation on improved upper bound: propagation on domain revision.
     */
    @Override
    public void awakeOnSup(final int varIdx) throws ContradictionException {
        filter();
    }

    /**
     * Default propagation on one value removal: propagation on domain revision.
     */
    @Override
    public void awakeOnRem(final int varIdx, final int val) throws ContradictionException {
        filter();
    }

    @Override
    public void awakeOnRemovals(final int varIdx, final DisposableIntIterator deltaDomain) throws ContradictionException {
        filter();
    }

    @Override
    public void awakeOnBounds(final int varIdx) throws ContradictionException {
        filter();
    }

    /**
     * Default propagation on kernel modification: propagation on adding a value to the kernel.
     */
    @Override
    public void awakeOnKer(final int varIdx, final int x) throws ContradictionException {
        filter();
    }

    /**
     * Default propagation on enveloppe modification: propagation on removing a value from the enveloppe.
     */
    @Override
    public void awakeOnEnv(final int varIdx, final int x) throws ContradictionException {
        filter();
    }

    /**
     * Default propagation on instantiation.
     */
    @Override
    public void awakeOnInst(final int varIdx) throws ContradictionException {
        filter();
    }

    @Override
    public void awakeOnkerAdditions(final int sIdx, final DisposableIntIterator deltaDomain) throws ContradictionException {
        filter();
    }

    @Override
    public void awakeOnEnvRemovals(final int sIdx, final DisposableIntIterator deltaDomain) throws ContradictionException {
        filter();
    }

    @Override
    public boolean isSatisfied(final int[] tuple) {
        throw new UnsupportedOperationException(this + " needs to implement isSatisfied(int[] tuple) to be embedded in reified constraints");
    }

}