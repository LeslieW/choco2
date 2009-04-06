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
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.kernel.common;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 17 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*
* Compute a unique index for object
*/
public class IndexFactory {

    public int index = 33;

    /**
     * Return a unique index
     * @return
     */
    public final int getIndex(){
        return ++index;
    }


    public static int base = (int)System.currentTimeMillis();

    /**
     * STATIC = STRONGLY JVM DEPENDANT!!
     * Be sure you are using the correct method!!
     * Must not be used as hashCode!
     *
     * Return a unique id
     * @return
     */
    public static synchronized int getId(){
        return ++base;
    }


}
