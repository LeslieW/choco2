/* ************************************************
*           _       _                            *
*          |  �(..)  |                           *
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

import choco.cp.solver.variables.delta.iterators.BitSetIterator;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.variables.delta.IDeltaDomain;

import java.util.BitSet;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 d�c. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public final class BitSetDeltaDomain implements IDeltaDomain {

    private BitSet removedValues;

    private BitSet removedValuesToPropagate;

    private int offset;


    private BitSetDeltaDomain() {}

    public BitSetDeltaDomain(final int size, final int theOffset) {
        this.removedValues = new BitSet(size);
        this.removedValuesToPropagate = new BitSet(size);
        this.offset = theOffset;
    }

    /**
     * The delta domain container is "frozen" (it can no longer accept new value removals)
     * so that this set of values can be iterated as such�
     */
    @Override
    public void freeze() {
        removedValuesToPropagate.clear();
        removedValuesToPropagate.or(removedValues);
    }

    /**
     * Update the delta domain
     *
     * @param value removed
     */
    @Override
    public void remove(final int value) {
        removedValues.set(value-offset);
    }

    /**
     * cleans the data structure implementing the delta domain
     */
    @Override
    public void clear() {
        removedValues.clear();
        removedValuesToPropagate.clear();
    }

    /**
     * Check if the delta domain is released or frozen.
     *
     * @return true if release
     */
    @Override
    public boolean isReleased() {
        return removedValues.isEmpty();
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
        removedValues.andNot(removedValuesToPropagate);
        final boolean empty = removedValues.isEmpty();
        removedValuesToPropagate.clear();
        return empty;
    }

    /**
     * Iterator over delta domain
     *
     * @return delta iterator
     */
    @Override
    public DisposableIntIterator iterator() {
        return BitSetIterator.getIterator(offset, removedValuesToPropagate);
      }

    @Override
    public BitSetDeltaDomain copy(){
        final BitSetDeltaDomain dom =new BitSetDeltaDomain();
        dom.removedValues = (BitSet)this.removedValues.clone();
        dom.removedValuesToPropagate = (BitSet)this.removedValuesToPropagate.clone();
        dom.offset = this.offset;
        return dom;
    }

    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    @Override
    public String pretty() {
        return "";
    }
}