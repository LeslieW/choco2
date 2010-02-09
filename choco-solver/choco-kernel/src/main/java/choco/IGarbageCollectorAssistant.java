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
package choco;

/**
 * The interface notifies that the object is not "in use" anymore.
 * The method prepprocesses the object for the garbage collector.
 * For example, it deletes some strong cross-references between objetcs.
 * 
 * @author Arnaud Malapert</br> 
 * @since 9 févr. 2010 version 2.1.1</br>
 * @version 2.1.1</br>
 */
public interface IGarbageCollectorAssistant {

	/**
	 * Preprocessing that helps the garbage collector.
	 */
	void freeMemory();
}
