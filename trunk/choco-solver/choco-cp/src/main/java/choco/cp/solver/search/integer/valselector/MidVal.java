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
package choco.cp.solver.search.integer.valselector;

import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class MidVal implements ValSelector {
    /**
     * selecting a value in the middle of the domain
     *
     * @param x the variable under consideration
     * @return what seems the most interesting value for branching
     */
    public int getBestVal(IntDomainVar x) {
        if (x.getDomain().isEnumerated()) {
            final int midVal = x.getInf() + (x.getSup() - x.getInf()) / 2;
            // -1 is mandatory in case of instantiation
            return x.getNextDomainValue(midVal-1);
        } else {
            // otherwise, always return lower bound
            return x.getInf();
        }
    }
}
