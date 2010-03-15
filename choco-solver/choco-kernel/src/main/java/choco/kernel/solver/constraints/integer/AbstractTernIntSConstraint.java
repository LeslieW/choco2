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

    
}
