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

import choco.cp.solver.variables.integer.IntervalIntDomain;
import choco.kernel.common.util.iterators.DisposableIntIterator;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class IntervalIntDomainIterator extends DisposableIntIterator {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// STATIC ///////////////////////////////////////////////////////////////
    static IntervalIntDomainIterator _iterator;

    public static DisposableIntIterator getIterator(IntervalIntDomain domain) {
        IntervalIntDomainIterator iter = _iterator;
        if (iter != null && iter.reusable) {
            iter.init(domain);
            return iter;
        }
        _iterator = new IntervalIntDomainIterator(domain);
        return _iterator;
    }
    ////////////////////////////////////////////\ STATIC ///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    IntervalIntDomain domain;
    int x;

    public IntervalIntDomainIterator(IntervalIntDomain domain) {
        init(domain);
    }

    public void init(IntervalIntDomain domain) {
        super.init();
        this.domain = domain;
        x = domain.getInf() - 1;
    }

    public boolean hasNext() {
        return x < domain.getSup();
    }

    public int next() {
        return ++x;
    }
}
