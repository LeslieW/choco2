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
package choco.kernel.memory;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 12 juil. 2007
 * Time: 10:24:12
 */
public interface IStateLong {
    /**
   * Returns the current value.
     */

    long get();


    /**
   * Modifies the value and stores if needed the former value on the
     * trailing stack.
     */

    void set(long y);

    /**
   * modifying a StoredInt by an increment
     *
     * @param delta
     */
    void add(long delta);

    /**
   * Retrieving the environment
     */
    IEnvironment getEnvironment();
}
