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
package choco.kernel.common;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 8 mars 2010<br/>
 * Since : Choco 2.1.1<br/>
 *
 * Class for constant declaration.
 */
public class Constant {

    /**
     * Initial capacity for array containinc static elements.
     */
    public static final int INITIAL_STATIC_CAPACITY = 16;

    /**
     * Initial capacity for array containinc dynamic elements.
     */
    public static final int INITIAL_STORED_CAPACITY = 16;

    /**
     * Offset of the first dynamic element.
     */
    public static final int STORED_OFFSET = 1000000;

    /**
     * Initial capacity of bipartite set
     */
    public static final int SET_INITIAL_CAPACITY = 8;

}
