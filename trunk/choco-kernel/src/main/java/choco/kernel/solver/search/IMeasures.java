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

import java.util.logging.Logger;

import choco.IPretty;
import choco.kernel.common.logging.ChocoLogging;

public interface IMeasures extends IPretty {
   
	/**
	 * an object for logging trace statements
	 */
	public final static Logger LOGGER = ChocoLogging.getSearchLogger();
	
	/**
	 * indicates if the search find at least one solution.
	 */
	boolean existsSolution();

	/**
     * Get the objective value of the measure, optimization only.
     * @return objective value
     */
	Number getObjectiveValue();
			
	/**
     * Get the solution count of the measure
     * @return solution count
     */
    int getSolutionCount();
	
	/**
     * Get the time count in milliseconds of the measure
     * @return time count
     */
    int getTimeCount();

    /**
     * Get the cpu time count in milliseconds of the measure
     * @return cpu time count
     */
    int getCpuTimeCount();

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
