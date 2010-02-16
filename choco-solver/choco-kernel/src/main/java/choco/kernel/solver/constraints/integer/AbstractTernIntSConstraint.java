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
 * An abstract class for all implementations of (unary) listeners over one
 * search variable.
 */
public abstract class AbstractTernIntSConstraint extends AbstractIntSConstraint {

    /**
     * Variables of the constraint.
     */
    protected final IntDomainVar v0, v1, v2;

    /**
     * The index of the constraint among all listeners of its variables.
     */
    protected int cIdx0, cIdx1, cIdx2;

    /**
     * Builds a ternary constraint with the specified variables.
     *
     * @param x0 the first variable
     * @param x1 the second variable
     * @param x2 the third variable
     */
    public AbstractTernIntSConstraint(final IntDomainVar x0,
                                      final IntDomainVar x1, final IntDomainVar x2) {
        super(ConstraintEvent.HIGH, new IntDomainVar[]{x0, x1, x2});
        v0 = x0;
        v1 = x1;
        v2 = x2;
    }

    /**
     * Let v be the i-th var of c,
     * records that c is the constraint n according to v.
     *
     * @param i   the variable index
     * @param val the constraint index according to the variable
     */
    @Override
    public final void setConstraintIndex(final int i, final int val) {
        super.setConstraintIndex(i, val);
        switch (i) {
            case 0:
                cIdx0 = val;
                break;
            case 1:
                cIdx1 = val;
                break;
            case 2:
                cIdx2 = val;
                break;
        }
    }

    /**
     * Checks if all the variables are instantiated.
     *
     * @return true if all variables are sintantiated
     */
    @Override
    public final boolean isCompletelyInstantiated() {
        return (v0.isInstantiated() && v1.isInstantiated() && v2.isInstantiated());
    }

    /**
     * Returns the number of variables.
     *
     * @return the number of variables, here always 3.
     */
    @Override
    public final int getNbVars() {
        return 3;
    }

    /**
     * Gets the specified variable.
     *
     * @param i the variable index
     * @return the variable with the specified index according to this constraint
     */
    @Override
    public final IntDomainVar getVar(final int i) {
        switch (i) {
            case 0:
                return v0;
            case 1:
                return v1;
            case 2:
                return v2;
            default:
                return null;
        }
    }

    /**
     * Returns the index of the constraint in the specified variable.
     */
    @Override
    public final int getConstraintIdx(int i) {
        switch (i) {
            case 0:
                return cIdx0;
            case 1:
                return cIdx1;
            case 2:
                return cIdx2;
            default:
                return -1;
        }
    }
}
