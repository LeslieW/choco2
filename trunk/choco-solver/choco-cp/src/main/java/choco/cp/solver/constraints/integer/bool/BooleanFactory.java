/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _        _                           *
 *         |   (..)  |                           *
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
package choco.cp.solver.constraints.integer.bool;

import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 18 mai 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class BooleanFactory {

    /**
     * Builder for AND constraint over integer variables
     *
     * @param booleans booleans variables
     * @return AND constraint
     */
    public static AbstractSConstraint<IntDomainVar> and(IntDomainVar... booleans) {
        if (booleans.length == 2) {
            return new BinAnd(booleans[0], booleans[1]);
        } else {
            return new LargeAnd(booleans);
        }
    }

    /**
     * Builder for NAND constraint over integer variables
     *
     * @param booleans booleans variables
     * @return NAND constraint
     */
    public static AbstractSConstraint<IntDomainVar> nand(final IEnvironment environment, IntDomainVar... booleans) {
        if (booleans.length == 2) {
            return new BinNand(booleans[0], booleans[1]);
        } else {
            return new LargeNand(booleans, environment);
        }
    }

    /**
     * Builder for OR constraint over integer variables
     *
     * @param environment
     * @param booleans    booleans variables
     * @return OR constraint
     */
    public static AbstractSConstraint<IntDomainVar> or(final IEnvironment environment, IntDomainVar... booleans) {
        if (booleans.length == 2) {
            return new BinOr(booleans[0], booleans[1]);
        } else {
            return new LargeOr(booleans, environment);
        }
    }

    /**
     * Builder for NOR constraint over integer variables
     *
     * @param environment
     * @param booleans    booleans variables
     * @return NOR constraint
     */
    public static AbstractSConstraint<IntDomainVar> nor(IntDomainVar... booleans) {
        if (booleans.length == 2) {
            return new BinNor(booleans[0], booleans[1]);
        } else {
            return new LargeNor(booleans);
        }
    }

    /**
     * Builder for XOR constraint over integer variables
     *
     * @param booleans    booleans variables
     * @return XOR constraint
     */
    public static AbstractSConstraint<IntDomainVar> xor(IntDomainVar... booleans) {
        if (booleans.length == 2) {
            return new BinXor(booleans[0], booleans[1]);
        } else {
            return new LargeXor(booleans);
        }
    }

    /**
     * Builder for XOR constraint over integer variables
     *
     * @param booleans    booleans variables
     * @return XOR constraint
     */
    public static AbstractSConstraint<IntDomainVar> xnor(IntDomainVar... booleans) {
        if (booleans.length == 2) {
            return new BinXnor(booleans[0], booleans[1]);
        } else {
            return new LargeXnor(booleans);
        }
    }

    /**
     * Builder for NOT constraint over an integer variable
     *
     * @param bool    boolean variable
     * @return NOT constraint
     */
    public static AbstractSConstraint<IntDomainVar> not(IntDomainVar bool) {
        return new Not(bool);
    }

    /**
     * Builder for NOT constraint over an integer variable
     *
     * @param bool    boolean variable
     * @return NOT constraint
     */
    public static AbstractSConstraint<IntDomainVar> identity(IntDomainVar bool) {
        return new Identity(bool); 
    }

}
