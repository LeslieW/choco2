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
import gnu.trove.TIntArrayList;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 déc. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class StackDeltaDomain implements IDeltaDomain {

    /**
     * A chained list implementing two subsets of values:
     * - the removed values waiting to be propagated
     * - the removed values being propagated
     * (each element points to the index of the enxt element)
     * -1 for the last element
     */
    private TIntArrayList list;

    boolean freeze;
    private int from;
    private int to;

    public StackDeltaDomain() {
        list = new TIntArrayList();
        from = -1;
        to = 0;
        freeze = false;
    }

    /**
     * The delta domain container is "frozen" (it can no longer accept new value removals)
     * so that this set of values can be iterated as such²
     */
    @Override
    public void freeze() {
        // freeze all data associated to bounds for the the event
        // if the delta domain is already being iterated, it cannot be frozen
        if (!freeze) {
//        }//throw new IllegalStateException();
//        else {
            // the set of values waiting to be propagated is now "frozen" as such,
            // so that those value removals can be iterated and propagated
            // the container (link list) for values waiting to be propagated is reinitialized to an empty set
            from = to;
            to = list.size();
            freeze = true;
        }
    }

    /**
     * Update the delta domain
     *
     * @param value removed
     */
    @Override
    public void remove(int value) {
        list.add(value);
    }

    /**
     * cleans the data structure implementing the delta domain
     */
    @Override
    public void clear() {
        from = -1;
        to = 0;
        list.clear();
        freeze = false;
    }

    /**
     * Check if the delta domain is released or frozen.
     *
     * @return true if release
     */
    @Override
    public boolean isReleased() {
        return !freeze;
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
        try{
            return (to == list.size());
        }finally {
            from = -1;
            freeze = false;
        }
    }

    /**
     * Iterator over delta domain
     *
     * @return delta iterator
     */
    @Override
    public DisposableIntIterator iterator() {
        StackDeltaDomainIterator iter = (StackDeltaDomainIterator) _cachedDeltaIntDomainIterator;
        if (iter != null && iter.isReusable()) {
            iter.init();
            return iter;
        }
        _cachedDeltaIntDomainIterator = new StackDeltaDomainIterator();
        return _cachedDeltaIntDomainIterator;
    }

    private DisposableIntIterator _cachedDeltaIntDomainIterator = null;

    private class StackDeltaDomainIterator extends DisposableIntIterator {

        int idx;

        private StackDeltaDomainIterator() {
            init();
        }

        @Override
        public void init() {
            idx = from;
        }

        public boolean hasNext() {
            return idx < to;
        }

        public int next() {
            return list.get(idx++);
        }
    }

    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    @Override
    public String pretty() {
        return from +" -> "+ to;
    }

    @Override
    public IDeltaDomain copy() {
        throw new UnsupportedOperationException();
    }

}
