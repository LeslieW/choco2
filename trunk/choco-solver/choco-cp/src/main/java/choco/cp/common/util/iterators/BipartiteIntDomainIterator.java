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
package choco.cp.common.util.iterators;

import choco.kernel.common.util.iterators.DisposableIntIterator;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class BipartiteIntDomainIterator extends DisposableIntIterator {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// STATIC ///////////////////////////////////////////////////////////////
    static BipartiteIntDomainIterator _cachedIterator;

    public static DisposableIntIterator getIterator(int firstIdx, int[] values) {
        BipartiteIntDomainIterator iter = _cachedIterator;
        if (iter != null && iter.reusable) {
            iter.init(firstIdx, values);
            return iter;
        }
        _cachedIterator = new BipartiteIntDomainIterator(firstIdx, values);
        return _cachedIterator;
    }
    ////////////////////////////////////////////\ STATIC ///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected int nextIdx;
    int[] values;

    private BipartiteIntDomainIterator(int firstIdx, int[] values) {
        init(firstIdx, values);
    }

    public void init(int firstIdx, int[] values) {
        super.init();
        nextIdx = firstIdx;
        this.values = values;
    }

    public boolean hasNext() {
        return nextIdx >= 0;
    }

    public int next() {
        int v = nextIdx;
        nextIdx--;
        return values[v];
    }
}
