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


import choco.kernel.solver.variables.real.RealVar;

/**
 * A real constraint with an undetermined number of variables.
 */
public abstract class AbstractLargeRealSConstraint extends AbstractRealSConstraint {

    /**
     * Builds such a constraint with the specified variables.
     *
     * @param vars the variables involved by this constraint
     */
    public AbstractLargeRealSConstraint(final RealVar[] vars) {
        super(vars);
    }

    /**
     * Makes a copy of this constraint.
     *
     * @return a copy of this constraint
     * @throws CloneNotSupportedException thrown if this constraint cannot be
     *                                    cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractLargeRealSConstraint newc =
                (AbstractLargeRealSConstraint) super.clone();
        newc.vars = new RealVar[this.vars.length];
        System.arraycopy(this.vars, 0, newc.vars, 0, this.vars.length);
        cIndices = new int[this.cIndices.length];
        System.arraycopy(this.cIndices, 0, newc.cIndices, 0, this.cIndices.length);
        return newc;
    }
}
