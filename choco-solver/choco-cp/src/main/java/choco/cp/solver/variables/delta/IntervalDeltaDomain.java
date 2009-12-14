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

import choco.cp.solver.variables.integer.AbstractIntDomain;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.variables.delta.IDeltaDomain;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 déc. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class IntervalDeltaDomain implements IDeltaDomain {

    /**
     * The last value since the last coherent state
     */
    private int lastInfPropagated;
    /**
     * The last value since the last coherent state
     */
    private int lastSupPropagated;

    /**
     * for the delta domain: current value of the inf (domain lower bound) when the bound started beeing propagated
     * (just to check that it does not change during the propagation phase)
     */
    private int currentInfPropagated;

    /**
     * for the delta domain: current value of the sup (domain upper bound) when the bound started beeing propagated
     * (just to check that it does not change during the propagation phase)
     */
    private int currentSupPropagated;


    private AbstractIntDomain domain;

    public IntervalDeltaDomain(AbstractIntDomain domain, int lastInfPropagated, int lastSupPropagated) {
        this.domain = domain;
        this.lastInfPropagated = lastInfPropagated;
        this.lastSupPropagated = lastSupPropagated;
        this.currentInfPropagated = Integer.MIN_VALUE;
        this.currentSupPropagated = Integer.MAX_VALUE;
    }

    /**
     * The delta domain container is "frozen" (it can no longer accept new value removals)
     * so that this set of values can be iterated as such²
     */
    @Override
    public void freeze() {
        currentInfPropagated = domain.getInf();
        currentSupPropagated = domain.getSup();
    }

    /**
     * Update the delta domain
     * @param value removed
     */
    @Override
    public void remove(int value) {
        if(lastInfPropagated == Integer.MIN_VALUE){
            this.lastInfPropagated = domain.getInf();
            this.lastSupPropagated = domain.getSup();
        }
    }

    /**
     * cleans the data structure implementing the delta domain
     */
    @Override
    public void clear() {
        lastInfPropagated = Integer.MIN_VALUE;
        lastSupPropagated = Integer.MAX_VALUE;
        currentInfPropagated = Integer.MIN_VALUE;
        currentSupPropagated = Integer.MAX_VALUE;
    }

    /**
     * Check if the delta domain is released or frozen.
     *
     * @return true if release
     */
    @Override
    public boolean isReleased() {
        return currentInfPropagated == Integer.MIN_VALUE && currentSupPropagated == Integer.MAX_VALUE;
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
        boolean noNewUpdate = ((domain.getInf() == currentInfPropagated) && (domain.getSup() == currentSupPropagated));
      if (noNewUpdate) {
        lastInfPropagated = Integer.MIN_VALUE;
        lastSupPropagated = Integer.MAX_VALUE;
      } else {
        lastInfPropagated = currentInfPropagated;
        lastSupPropagated = currentSupPropagated;
      }
      currentInfPropagated = Integer.MIN_VALUE;
      currentSupPropagated = Integer.MAX_VALUE;
      return noNewUpdate;
    }

    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    @Override
    public String pretty() {
        return lastInfPropagated + "->" + lastSupPropagated;
    }

    /**
     * Iterator over delta domain
     * @return delta iterator
     */
    public DisposableIntIterator iterator() {
          IntervalIntDomainDeltaIterator iter = (IntervalIntDomainDeltaIterator) _deltaIterator;
        if (iter != null && iter.reusable) {
            iter.init();
            return iter;
        }
        _deltaIterator = new IntervalIntDomainDeltaIterator();
        return _deltaIterator;
      }

    private DisposableIntIterator _deltaIterator = null;

    private class IntervalIntDomainDeltaIterator extends DisposableIntIterator {

        int x;

        public IntervalIntDomainDeltaIterator() {
            init();
        }

        public void init() {
            super.init();
            x = lastInfPropagated - 1;
        }


        public boolean hasNext() {
            if (x + 1 == currentInfPropagated) return currentSupPropagated < lastSupPropagated;
            if (x > currentSupPropagated) return x < lastSupPropagated;
            return (x + 1 < currentInfPropagated);
        }

        public int next() {
            x++;
            if (x == currentInfPropagated) {
                x = currentSupPropagated + 1;
            }
            return x;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void dispose() {
        }
    }

    @Override
    public IDeltaDomain copy() {
        IntervalDeltaDomain delta = new IntervalDeltaDomain(this.domain, this.lastInfPropagated, this.lastSupPropagated);
        delta.currentInfPropagated = this.currentInfPropagated;
        delta.currentSupPropagated = this.currentSupPropagated;
        return delta;

    }

}
