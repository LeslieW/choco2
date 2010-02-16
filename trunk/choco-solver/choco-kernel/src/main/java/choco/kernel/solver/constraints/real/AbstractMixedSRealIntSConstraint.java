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
package choco.kernel.solver.constraints.real;

import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.propagation.listener.IntPropagator;
import choco.kernel.solver.propagation.listener.RealPropagator;
import choco.kernel.solver.variables.Var;

/**
 * An interface for mixed constraint : interger and flot variables.
 */
public abstract class AbstractMixedSRealIntSConstraint extends AbstractSConstraint<Var> implements IntPropagator, RealPropagator{

    /**
     * Constraucts a constraint with the priority 0.
     */
    protected AbstractMixedSRealIntSConstraint(Var[] vars) {
        super(vars);
    }
}
