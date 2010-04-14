/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
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
package choco;

import choco.kernel.solver.branch.Extension;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 14 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 *
 * An interface to add IExtension to inherited object.
 */
public interface IExtensionnable {

    /**
	 * Returns the queried extension
	 *
	 * @param extensionNumber should use the number returned by getAbstractSConstraintExtensionNumber
	 * @return the queried extension
	 */
    Extension getExtension(int extensionNumber);

    /**
	 * Adds a new extension.
	 *
	 * @param extensionNumber should use the number returned by getAbstractSConstraintExtensionNumber
	 */
	void addExtension(int extensionNumber);
}
