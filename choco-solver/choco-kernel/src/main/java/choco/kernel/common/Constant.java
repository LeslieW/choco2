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
package choco.kernel.common;

import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.ConstantSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 8 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 *
 * Class for constant declaration.
 */
public class Constant {

    /**
     * Initial capacity for array containinc static elements.
     */
    public static final int INITIAL_STATIC_CAPACITY = 16;

    /**
     * Initial capacity for array containinc dynamic elements.
     */
    public static final int INITIAL_STORED_CAPACITY = 16;

    /**
     * Offset of the first dynamic element.
     */
    public static final int STORED_OFFSET = 1000000;

    /**
     * Initial capacity of bipartite set
     */
    public static final int SET_INITIAL_CAPACITY = 8;

    /**
     * A constant denoting the true constraint (always satisfied)
     */
    public static final ConstantSConstraint TRUE = new ConstantSConstraint(true) {
        /**
         * Get the opposite constraint
         *
         * @return the opposite constraint  @param solver
         */
        @Override
        public AbstractSConstraint<IntDomainVar> opposite(final Solver solver) {
            return Constant.FALSE;
        }
    };

    /**
     * A constant denoting the false constraint (never satisfied)
     */
    public static final ConstantSConstraint FALSE = new ConstantSConstraint(false) {
        /**
         * Get the opposite constraint
         *
         * @return the opposite constraint  @param solver
         */
        @Override
        public AbstractSConstraint<IntDomainVar> opposite(final Solver solver) {
            return Constant.TRUE;
        }
    };

    /**
     * Defines the rounding precision for multicostregular algorithm
     */
    public static final int MCR_PRECISION = 4; // MUST BE < 13 as java messes up the precisions starting from 10E-12 (34.0*0.05 == 1.70000000000005)

      /**
     * Defines the smallest used double for multicostregular
     */
    public static final double MCR_DECIMAL_PREC = Math.pow(10.0,-MCR_PRECISION);
    
}
