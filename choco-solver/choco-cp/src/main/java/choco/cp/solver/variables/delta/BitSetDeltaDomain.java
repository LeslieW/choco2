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
import choco.kernel.solver.variables.delta.IDeltaDomain;

import java.util.BitSet;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 déc. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class BitSetDeltaDomain implements IDeltaDomain {

    private BitSet removedValues;

    private BitSet removedValuesToPropagate;

    private int offset;


    BitSetDeltaDomain() {}

    public BitSetDeltaDomain(int size, int offset) {
        this.removedValues = new BitSet(size);
        this.removedValuesToPropagate = new BitSet(size);
        this.offset = offset;
    }

    /**
     * The delta domain container is "frozen" (it can no longer accept new value removals)
     * so that this set of values can be iterated as such²
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
    public void remove(int value) {
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
        boolean empty = removedValues.isEmpty();
        removedValuesToPropagate.clear();
        return empty;
//        removedValues.clear();
//        return true;
    }

    /**
     * Iterator over delta domain
     *
     * @return delta iterator
     */
    @Override
    public DisposableIntIterator iterator() {
        BitSetIntDeltaDomainIterator iter = _deltaIterator;
        if (iter != null && iter.isReusable()) {
            iter.init();
            return iter;
        }
        _deltaIterator = new BitSetIntDeltaDomainIterator();
        return _deltaIterator;
      }

    private BitSetIntDeltaDomainIterator _deltaIterator = null;

    class BitSetIntDeltaDomainIterator extends DisposableIntIterator {
        protected int currentIndex = 0;

        private BitSetIntDeltaDomainIterator() {
            init();
        }

        @Override
        public void init() {
            currentIndex = removedValuesToPropagate.nextSetBit(0);
        }

        public boolean hasNext() {
            return currentIndex != -1;
        }

        public int next() {
            int v = currentIndex;
            currentIndex = removedValuesToPropagate.nextSetBit(currentIndex+1);
            return v+offset;
        }

        public void remove() {
            if (currentIndex == -1) {
                throw new IllegalStateException();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public BitSetDeltaDomain copy(){
        BitSetDeltaDomain dom =new BitSetDeltaDomain();
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
