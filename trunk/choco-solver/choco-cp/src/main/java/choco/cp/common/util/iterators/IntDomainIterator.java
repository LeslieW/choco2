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

import choco.cp.solver.variables.integer.AbstractIntDomain;
import choco.kernel.common.util.iterators.DisposableIntIterator;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 */
public class IntDomainIterator extends DisposableIntIterator {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// STATIC ///////////////////////////////////////////////////////////////
    static IntDomainIterator _cachedIterator;

    public static DisposableIntIterator getIterator(AbstractIntDomain dom) {
        IntDomainIterator iter = _cachedIterator;
        if (iter != null && iter.reusable) {
            iter.init(dom);
            return iter;
        }
        _cachedIterator = new IntDomainIterator(dom);
        return _cachedIterator;
    }
    ////////////////////////////////////////////\ STATIC ///////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    protected AbstractIntDomain domain;
    protected int nextValue;
    protected int supBound = -1;

    private IntDomainIterator(AbstractIntDomain dom) {
        init(dom);
    }

    public void init(AbstractIntDomain dom) {
        super.init();
        domain = dom;
        if (domain.getSize() >= 1) {
            nextValue = domain.getInf();
        } else {
            throw new UnsupportedOperationException();
        }
        supBound = domain.getSup();
        //currentValue = Integer.MIN_VALUE; // dom.getInf();
    }

    public boolean hasNext() {
        return /*(Integer.MIN_VALUE == currentValue) ||*/ (nextValue <= supBound);
        // if currentValue equals MIN_VALUE it will be less than the upper bound => only one test is needed ! Moreover
        // MIN_VALUE is a special case, should not be tested if useless !
    }

    public int next() {
        int v = nextValue;
        nextValue = domain.getNextValue(nextValue);
        return v;
    }

}
