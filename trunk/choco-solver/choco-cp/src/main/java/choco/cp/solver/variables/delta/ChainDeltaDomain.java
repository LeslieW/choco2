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

import java.util.Arrays;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 déc. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class ChainDeltaDomain implements IDeltaDomain {

    /**
     * A chained list implementing two subsets of values:
     * - the removed values waiting to be propagated
     * - the removed values being propagated
     * (each element points to the index of the enxt element)
     * -1 for the last element
     */
    private int[] chain;

    /**
     * start of the chain for the values waiting to be propagated
     * -1 for empty chains
     */
    private int firstIndexToBePropagated;

    /**
     * start of the chain for the values being propagated
     * -1 for empty chains
     */
    private int firstIndexBeingPropagated;

    private final int offset;

    public ChainDeltaDomain(int size, int offset) {
        LOGGER.warning("BEWARE this DELTA DOMAIN is full of bugs !!");
        chain = new int[size];
        firstIndexToBePropagated = -1;
        firstIndexBeingPropagated = -1;
        this.offset = offset;

    }

    /**
     * The delta domain container is "frozen" (it can no longer accept new value removals)
     * so that this set of values can be iterated as such²
     */
    @Override
    public void freeze() {
        // freeze all data associated to bounds for the the event
        // if the delta domain is already being iterated, it cannot be frozen
        if (firstIndexBeingPropagated == -1) {
//        }//throw new IllegalStateException();
//        else {
            // the set of values waiting to be propagated is now "frozen" as such,
            // so that those value removals can be iterated and propagated
            firstIndexBeingPropagated = firstIndexToBePropagated;
            // the container (link list) for values waiting to be propagated is reinitialized to an empty set
            firstIndexToBePropagated = -1;
        }
    }

    /**
     * Update the delta domain
     *
     * @param value removed
     */
    @Override
    public void remove(int value) {
        if (value == firstIndexToBePropagated)
            LOGGER.severe("RemoveIndex BIZARRE !!!!!!!!!!!!");
        chain[value-offset] = firstIndexToBePropagated;
        firstIndexToBePropagated = value-offset;
    }

    /**
     * cleans the data structure implementing the delta domain
     */
    @Override
    public void clear() {
        firstIndexBeingPropagated = -1;
        firstIndexToBePropagated = -1;
        Arrays.fill(chain, 0);
    }

    /**
     * Check if the delta domain is released or frozen.
     *
     * @return true if release
     */
    @Override
    public boolean isReleased() {
        return ((firstIndexBeingPropagated == -1) && (firstIndexToBePropagated == -1));
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
        // release all data associated to bounds for the the event
        // special case: the set of removals was not being iterated (because the variable was instantiated, or a bound was updated)
        if (firstIndexBeingPropagated == -1) {
            // remove all values that are waiting to be iterated
            firstIndexToBePropagated = -1;
            // return true because the event has been "flushed" (nothing more is awaiting)
            return true;
        } else { // standard case: the set of removals was being iterated
            // empty the set of values that were being propagated
            firstIndexBeingPropagated = -1;
            // if more values are waiting to be propagated, return true
            return (firstIndexToBePropagated == -1);
        }
    }

    /**
     * Iterator over delta domain
     *
     * @return delta iterator
     */
    @Override
    public DisposableIntIterator iterator() {
        DeltaIntDomainIterator iter = _cachedDeltaIntDomainIterator;
        if (iter != null && iter.isReusable()) {
            iter.init();
            return iter;
        }
        _cachedDeltaIntDomainIterator = new DeltaIntDomainIterator();
        return _cachedDeltaIntDomainIterator;
    }

    protected DeltaIntDomainIterator _cachedDeltaIntDomainIterator = null;

    class DeltaIntDomainIterator extends DisposableIntIterator {
        protected int currentIndex = -1;

        private DeltaIntDomainIterator() {
            init();
        }

        @Override
        public void init() {
            super.init();
            currentIndex = -1;
        }

        public boolean hasNext() {
            if (currentIndex == -1) {
                return (firstIndexBeingPropagated != -1);
            } else {
                return (chain[currentIndex] != -1);
            }
        }

        public int next() {
            if (currentIndex == -1) {
                currentIndex = firstIndexBeingPropagated;
            } else {
                currentIndex = chain[currentIndex];
            }
            return currentIndex + offset;
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
    public IDeltaDomain copy() {
        throw new UnsupportedOperationException();
    }

    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    @Override
    public String pretty() {
        return firstIndexBeingPropagated +" -> "+firstIndexToBePropagated;
    }
}
