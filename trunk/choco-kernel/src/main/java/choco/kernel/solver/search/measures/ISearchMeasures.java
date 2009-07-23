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
package choco.kernel.solver.search.measures;


public interface ISearchMeasures {
	   
	/**
     * Get the time count in milliseconds of the measure
     * @return time count
     */
    int getTimeCount();

    /**
     * Get the node count of the measure
     * @return node count
     */
    int getNodeCount();

    /**
     * Get the backtrack count of the measure
     * @return backtrack count
     */
    int getBackTrackCount();

    /**
     * Get the fail count of the measure
     * @return fail count
     */
    int getFailCount();
    
    /**
     * Get the iteration/restart count of the measure
     * @return iteration/restart count
     */
    int getIterationCount();
    
    
}
