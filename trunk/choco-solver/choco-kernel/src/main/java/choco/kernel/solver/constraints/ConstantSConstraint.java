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

package choco.kernel.solver.constraints;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.propagation.event.ConstraintEvent;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class ConstantSConstraint extends AbstractIntSConstraint {

    private final boolean satisfied;

    protected ConstantSConstraint(boolean value) {
        super(ConstraintEvent.HIGH, new IntDomainVar[]{});
        satisfied = value;
    }


    @Override
    public final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    @Override
    public final boolean isSatisfied(int[] tuple) {
        return satisfied;
    }

    @Override
    public final boolean isSatisfied() {
        return satisfied;
    }

    @Override
    public final void propagate() throws ContradictionException {
        if (!satisfied) {
            fail();
        }
    }

    @Override
    public final Boolean isEntailed() {
        return satisfied;
    }

    @Override
    public final boolean isConsistent() {
        return satisfied;
    }

    @Override
    public final String pretty() {
        return "Constant constraint: " + satisfied;
    }
    
}
