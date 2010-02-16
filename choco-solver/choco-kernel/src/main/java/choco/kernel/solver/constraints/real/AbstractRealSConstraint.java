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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.kernel.solver.constraints.real;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.propagation.listener.RealPropagator;
import choco.kernel.solver.variables.real.RealVar;

/**
 * User : cprudhom
 * Mail : cprudhom(a)emn.fr
 * Date : 15 févr. 2010
 * Since : Choco 2.1.1
 */
public abstract class AbstractRealSConstraint extends AbstractSConstraint<RealVar> implements RealPropagator {

    /**
     * Constructs a constraint with the specified priority.
     *
     * @param priority The wished priority.
     */
    protected AbstractRealSConstraint(int priority, RealVar[] vars) {
        super(priority, vars);
    }

    /**
     * Constraucts a constraint with the priority 0.
     */
    protected AbstractRealSConstraint(RealVar[] vars) {
        super(vars);
    }

    /**
     * Default propagation on improved lower bound: propagation on domain revision.
     */
    @Override
    public void awakeOnInf(int idx) throws ContradictionException {
        this.constAwake(false);
    }

    /**
     * Default propagation on improved upper bound: propagation on domain revision.
     */
    @Override
    public void awakeOnSup(int idx) throws ContradictionException {
        this.constAwake(false);
    }

    @Override
    public SConstraintType getConstraintType() {
        return SConstraintType.REAL;
    }

}
