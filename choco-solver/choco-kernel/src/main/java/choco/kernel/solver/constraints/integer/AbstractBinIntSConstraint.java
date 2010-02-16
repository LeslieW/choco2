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

// import choco.kernel.solver.search.AbstractIntConstraint;

/**
 * An abstract class for all implementations of (binary) constraints over two search variable.
 */
public abstract class AbstractBinIntSConstraint extends AbstractIntSConstraint {

    /**
     * Variables of the constraint.
     */

    public final IntDomainVar v0, v1;

    /**
     * The index of the constraint among all listeners of its variables.
     */

    public int cIdx0, cIdx1;


    public AbstractBinIntSConstraint(IntDomainVar x0, IntDomainVar x1) {
        super(ConstraintEvent.HIGH, new IntDomainVar[]{x0, x1});
        v0 = x0;
        v1 = x1;
    }

    /**
     * Checks if all the variables are instantiated.
     */

    @Override
    public final boolean isCompletelyInstantiated() {
        return (v0.isInstantiated() && v1.isInstantiated());
    }


    /**
     * Returns the number of varibles.
     */
    @Override
    public final int getNbVars() {
        return (2);
    }


    /**
     * Returns the specified variable.
     */
    @Override
    public final IntDomainVar getVar(int i) {
        switch (i){
            case 0:
                return v0;
            case 1:
                return v1;
            default:
                return null;
        }
    }


    /**
     * Returns the index of the constraint in the specified variable.
     */
    @Override
    public final int getConstraintIdx(int i) {
        switch (i){
            case 0:
                return cIdx0;
            case 1:
                return cIdx1;
            default:
                return -1;
        }
    }

    /**
     * Let v be the i-th var of c, records that c is the n-th constraint involving v.
     */
    @Override
    public final void setConstraintIndex(int i, int val) {
        super.setConstraintIndex(i, val);
        switch (i){
            case 0:
                cIdx0 = val;
                break;
            case 1:
                cIdx1 = val;
                break;
        }
    }
}
