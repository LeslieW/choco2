/* ************************************************
*           _       _                            *
*          |  °(..)  |                           *
*          |_  J||L _|        CHOCO solver       *
*                                                *
*     Choco is a java library for constraint     *
*     satisfaction problems (CSP), constraint    *
*     programming (CP) and explanation-based     *
*     constraint solving (e-CP). It is built     *
*     on a event-based propagation mechanism     *
*     with backtrackable structures.             *
*                                                *
*     Choco is an open-source software,          *
*     distributed under a BSD licence            *
*     and hosted by sourceforge.net              *
*                                                *
*     + website : http://choco.emn.fr            *
*     + support : choco@emn.fr                   *
*                                                *
*     Copyright (C) F. Laburthe,                 *
*                   N. Jussien    1999-2009      *
**************************************************/
package choco.cp.solver.variables.delta;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.EmptyIntIterator;
import choco.kernel.common.util.iterators.OneValueIterator;
import choco.kernel.solver.variables.delta.IDeltaDomain;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 14 déc. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public final class BooleanDeltaDomain implements IDeltaDomain{

    private int valueToPropagate;

    public BooleanDeltaDomain() {
        valueToPropagate = Integer.MIN_VALUE;
    }

    /**
     * The delta domain container is "frozen" (it can no longer accept new value removals)
     * so that this set of values can be iterated as such²
     */
    @Override
    public void freeze() {
        //useless
    }

    /**
     * Update the delta domain
     * @param value removed
     */
    @Override
    public void remove(final int value) {
        valueToPropagate = value;
    }

    /**
     * cleans the data structure implementing the delta domain
     */
    @Override
    public void clear() {
        valueToPropagate = Integer.MIN_VALUE;
    }

    /**
     * Check if the delta domain is released or frozen.
     *
     * @return true if release
     */
    @Override
    public boolean isReleased() {
        // useless
        return true;
    }

    /**
     * after an iteration over the delta domain, the delta domain is reopened again.
     *
     * @return true iff the delta domain is reopened empty (no updates have been made to the domain
     *         while it was frozen, false iff the delta domain is reopened with pending value removals (updates
     *         were made to the domain, while the delta domain was frozen).
     */
    @Override
    public boolean release() {
        valueToPropagate = Integer.MIN_VALUE;
        return true;
    }

    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    @Override
    public String pretty() {
        return String.valueOf(valueToPropagate);
    }


    public DisposableIntIterator iterator() {
        if(valueToPropagate == Integer.MIN_VALUE){
            return EmptyIntIterator.getIterator();
        }
        return OneValueIterator.getIterator(valueToPropagate);
    }


    @Override
    public IDeltaDomain copy() {
        final BooleanDeltaDomain delta = new BooleanDeltaDomain();
        delta.valueToPropagate = valueToPropagate;
        return delta;

    }

}
