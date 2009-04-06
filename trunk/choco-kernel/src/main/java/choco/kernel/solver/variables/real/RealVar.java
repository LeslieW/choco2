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
package choco.kernel.solver.variables.real;

import choco.kernel.solver.constraints.real.RealExp;
import choco.kernel.solver.variables.Var;

/**
 * An interface for a real variable : an expression and a variable with a domain.
 */
public interface RealVar extends Var, RealExp {

    /**
     * <b>Public user API:</b>
     * static constants associated to the encoding of the variable domain
     * these constants are passed as parameters to the constructor of RealVars
     */
    public static int BOUNDS = 0;

    public RealDomain getDomain();

    /**
     * Modifies bounds silently (does not propagate modifications). This is usefull for box cosistency.
     *
     * @param i
     */
    void silentlyAssign(RealInterval i);

    public RealInterval getValue();
}
