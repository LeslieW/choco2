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
import choco.kernel.memory.IStateBitSet;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class BitSetIntDomainIterator extends DisposableIntIterator {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// STATIC ///////////////////////////////////////////////////////////////
    static BitSetIntDomainIterator _cachedIterator;

    public static DisposableIntIterator getIterator(int inf, int offset, IStateBitSet contents) {
        BitSetIntDomainIterator iter = _cachedIterator;
        if (iter != null && iter.reusable) {
            iter.init(inf, offset, contents);
            return iter;
        }
        _cachedIterator = new BitSetIntDomainIterator(inf, offset, contents);
        return _cachedIterator;
    }
    ////////////////////////////////////////////\ STATIC ///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected int nextValue;
    protected int offset;
    protected IStateBitSet contents;

    private BitSetIntDomainIterator(int inf, int offset, IStateBitSet contents) {
        init(inf, offset, contents);
    }

    public void init(int inf, int offset, IStateBitSet contents) {
        super.init();
        this.contents = contents;
        this.offset = offset;
        nextValue = inf - offset;
    }

    public boolean hasNext() {
        return nextValue >= 0;
    }

    public int next() {
        int v = nextValue;
        nextValue = contents.nextSetBit(nextValue + 1);
        return v + offset;
    }

}
