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

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.variables.Var;

public interface IObjectiveManager {

	Var getObjective();
	
	/**
	 * v1.0 accessing the objective value of an optimization model
	 * (note that the objective value may not be instantiated, while all other variables are)
	 *
	 * @return the current objective value
	 */
	Number getObjectiveValue();

	/**
	 * v1.0 accessing the best found objective value of an optimization model
	 * (note that the objective value may not be instantiated, while all other variables are)
	 *
	 * @return the best found objective value
	 */
	Number getBestObjectiveValue();

	/**
	 * the target for the objective function: we are searching for a solution at least as good as this (tentative bound)
	 */
	Number getObjectiveTarget();
	
	/**
	 * Currently best known bound on the optimal solution value of the problem.
	 */
	Number getObjectiveFloor();
	

	void writeObjective(Solution sol);
	
	/**
	 * initialization of the optimization bound data structure
	 */
	void initBounds();

	/**
	 * resetting the optimization bounds
	 */
	void setBound();
	/**
	 * resetting the values of the target bounds (bounds for the remaining search).
	 * @return <code>true</code> if the target bound is indeasible regarding to the objective domain.
	 */
	void setTargetBound();
	
	/**
	 * propagating the optimization cuts from the new target bounds
	 */
	void postTargetBound() throws ContradictionException;
	
	/**
	 * propagating the optimization cuts from the new floor bounds
	 */
	void postFloorBound() throws ContradictionException;
	
	void incrementFloorBound();
	
	void postIncFloorBound() throws ContradictionException;
	
	/**
	 * indicates if the target bound is infeasible, i.e. does not belong to the current objective domain.
	 * @return <code>true</code> if the target bound does not belong to the objective domain, <code>false</code> otherwise.
	 */
	boolean isTargetInfeasible();
}