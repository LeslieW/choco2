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
package choco.kernel.solver.search;

import choco.kernel.common.logging.ChocoLogging;

import java.util.logging.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: 16 juil. 2007
 * Time: 09:57:11
 */
public interface ISearchLoop {

    final static Logger LOGGER = ChocoLogging.getSearchLogger();
	
    void initialize();
    
	Boolean run();

	//void init();

	//void openNode();

	//void upBranch();

	//void downBranch();
	
	//void restart();
}
