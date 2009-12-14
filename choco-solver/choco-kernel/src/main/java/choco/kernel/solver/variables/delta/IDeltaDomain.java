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
package choco.kernel.solver.variables.delta;

import choco.IPretty;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.iterators.DisposableIntIterator;

import java.util.logging.Logger;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 11 déc. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*
* Interface for delta domain.
*
* A delta domain is used to store removal values during propagation.
*/
public interface IDeltaDomain extends IPretty {

    Logger LOGGER = ChocoLogging.getEngineLogger();

    /**
     * The delta domain container is "frozen" (it can no longer accept new value removals)
     * so that this set of values can be iterated as such²
     */
    public void freeze();


    /**
     * Update the delta domain
     * @param value removed
     */
    public void remove(int value);

  /**
   * cleans the data structure implementing the delta domain
   */
    public void clear();

    /**
     * Check if the delta domain is released or frozen.
     * @return true if release
     */
    public boolean isReleased();

  /**
   * after an iteration over the delta domain, the delta domain is reopened again.
   *
   * @return true iff the delta domain is reopened empty (no updates have been made to the domain
   *         while it was frozen, false iff the delta domain is reopened with pending value removals (updates
   *         were made to the domain, while the delta domain was frozen).
   */
    public boolean release();

    /**
     * Iterator over delta domain
     * @return delta iterator
     */
    public DisposableIntIterator iterator();


    public IDeltaDomain copy();

}
