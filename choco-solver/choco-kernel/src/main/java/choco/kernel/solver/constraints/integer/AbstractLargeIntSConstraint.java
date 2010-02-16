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
 * An abstract class for all implementations of listeners over many search variables.
 */
public abstract class AbstractLargeIntSConstraint extends AbstractIntSConstraint {


    public AbstractLargeIntSConstraint(IntDomainVar[] vars) {
        super(ConstraintEvent.LOW, vars);
    }

    protected AbstractLargeIntSConstraint(int priority, IntDomainVar[] vars) {
        super(priority, vars);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractLargeIntSConstraint newc = (AbstractLargeIntSConstraint) super.clone();
        newc.vars = new IntDomainVar[this.vars.length];
        System.arraycopy(this.vars, 0, newc.vars, 0, this.vars.length);
        cIndices = new int[this.cIndices.length];
        System.arraycopy(this.cIndices, 0, newc.cIndices, 0, this.cIndices.length);
        return newc;
    }
}
