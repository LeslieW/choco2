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

package choco.kernel.solver.constraints.integer;

import choco.kernel.solver.propagation.event.ConstraintEvent;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * An abstract class for all implementations of (unary) listeners over
 * one search variable.
 */
public abstract class AbstractUnIntSConstraint extends AbstractLargeIntSConstraint {

    /**
     * The unique variable of the constraint.
     */

    protected final IntDomainVar v0;


    /**
     * The index of the constraint among all listeners of its first (and unique)
     * variable.
     */

    protected int cIdx0;


    protected AbstractUnIntSConstraint(IntDomainVar v0) {
        super(ConstraintEvent.HIGH, new IntDomainVar[]{v0});
        this.v0 = v0;
    }

    /**
     * Returns the number of variables: 1 for an unIntConstraint.
     */
    @Override
    public final int getNbVars() {
        return 1;
    }


    /**
     * Returns the variable number <code>i</code>. Here, <code>i</code>
     * should be 0.
     */
    @Override
    public final IntDomainVar getVar(int i) {
        if (i == 0) {
            return v0;
        }
        return null;
    }


    /**
     * Returns the index of this constraints in all constraints involving v0.
     */
    @Override
    public final int getConstraintIdx(int idx) {
        if (idx == 0) {
            return cIdx0;
        } else {
            return -1;
        }
    }


    /**
     * Let v be the i-th var of c, records that c is the n-th constraint involving v.
     */
    @Override
    public final void setConstraintIndex(int i, int val) {
        super.setConstraintIndex(i, val);
        if (i == 0) {
            cIdx0 = val;
        }
    }

    /**
     * Checks if all the variables of the constraint are instantiated.
     */

    @Override
    public final boolean isCompletelyInstantiated() {
        return v0.isInstantiated();
    }

}
