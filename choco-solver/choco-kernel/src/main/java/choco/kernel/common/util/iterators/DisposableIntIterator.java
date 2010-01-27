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
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */

package choco.kernel.common.util.iterators;

/**
 * @author grochart
 */
public abstract class DisposableIntIterator implements IntIterator {

    public boolean reusable;

    public void init() {
        reusable = false;
    }

    /**
     * This method allows to declare that the iterator is not usefull anymoure. It
     * can be reused by another object.
     */
    public final void dispose() {
        reusable = true;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public DisposableIntIterator copy(){
        throw new UnsupportedOperationException();
    }
}
