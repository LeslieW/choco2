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

import choco.cp.solver.variables.integer.BooleanDomain;
import choco.kernel.common.util.iterators.DisposableIntIterator;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class BooleanDomainIterator extends DisposableIntIterator {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// STATIC ///////////////////////////////////////////////////////////////
    static BooleanDomainIterator _cachedIterator;

    public static DisposableIntIterator getIterator(BooleanDomain domain) {
        BooleanDomainIterator iter = _cachedIterator;
        if (iter != null && iter.isReusable()) {
            iter.init(domain);
            return iter;
        }
        _cachedIterator = new BooleanDomainIterator(domain);
        return _cachedIterator;
    }
    ////////////////////////////////////////////\ STATIC ///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected BooleanDomain domain;
    protected int nextValue;

    private BooleanDomainIterator(BooleanDomain domain) {
        init(domain);
    }

    public void init(BooleanDomain domain) {
        super.init();
        this.domain = domain;
        nextValue = domain.getInf();
    }

    public boolean hasNext() {
        return nextValue <= 1;
    }

    public int next() {
        int v = nextValue;
        if (v == 0 && domain.contains(1))
            nextValue = 1;
        else nextValue = 2;
        return v;
    }
}
