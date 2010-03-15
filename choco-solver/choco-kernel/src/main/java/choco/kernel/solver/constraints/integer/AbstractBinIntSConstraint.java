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


    public AbstractBinIntSConstraint(IntDomainVar x0, IntDomainVar x1) {
        super(ConstraintEvent.HIGH, new IntDomainVar[]{x0, x1});
        v0 = x0;
        v1 = x1;
    }

 }
