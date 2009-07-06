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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;

/**
 * An abstract class handling the control for solving a model
 */
public abstract class AbstractSearchStrategy {

	/**
	 * an object for logging trace statements
	 */
	protected final static Logger LOGGER = ChocoLogging.getSearchLogger();

	/**
	 * The (optimization or decision) model to which the entity belongs.
	 */

	public Solver solver;
	/**
	 * The historical record of solutions that were found
	 */
	private final LinkedList<Solution> solutions; //Solution[]

	/**
	 * capacity of the history record (keeping solutions)
	 */
	private int maxNbSolutionStored = 5;


	public AbstractSearchStrategy() {
		solutions = new LinkedList<Solution>();
	}

	/**
	 * Retrieves the solver of the entity
	 */

	public Solver getSolver() {
		return solver;
	}

	public void setSolver(Solver solver) {
		this.solver = solver;
	}
	
	/**
	 * indicates if we will store the next solution
	 */
	public boolean isRecordingNextSolution() {
		return maxNbSolutionStored > 0;
	}

	public final int getStoredSolutionsCapacity() {
		return maxNbSolutionStored;
	}

	public final void setRecordingSolutions(int capacity) {
		this.maxNbSolutionStored = capacity;
	}


	public final void clearSolutions() {
		solutions.clear();
	}



	/**
	 * recording the current state as a solution
	 * stores information from the current state in the next solution of the model
	 * note: only instantiated variables are recorded in the Solution object
	 * either all variables or a user-defined subset of them are recorded
	 * this may erase a soolution that was previously stored in the ith position
	 * this may also increase the size of the pb.solutions vector.
	 */
	public void recordSolution() {
		Solution sol;
		if(solutions.size() < maxNbSolutionStored) {
			sol = solver.recordSolution();
		}else {
			sol = solutions.removeLast();
			sol.setSolver(solver);
			sol.save();
		}
		solutions.addFirst(sol);
	}

	/**
	 * showing information about the last solution
	 */
	public void showSolution() {
		if(LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info(solver.pretty());
		}
	}

	public final void storeSolution(Solution sol) {
		//[SVIEW] store solution ~S // sol,
		if(solutions.size() < maxNbSolutionStored) {
			solutions.removeLast();
		}
		solutions.addFirst(sol);
	}

	public final boolean existsStoredSolution() {
		return solutions.size() > 0;
	}

	public final Solution getBestSolution() {
		return solutions.isEmpty() ? null : solutions.getFirst();
	}

	public void restoreBestSolution() {
		solver.restoreSolution(getBestSolution());
	}

	public final int getNbStoredSolutions(){
		return solutions.size();
	}

	public final List<Solution> getStoredSolutions(){
		return Collections.unmodifiableList(solutions);
	}

	/**
	 * main entry point: running the search algorithm controlled the CPSolver object
	 * @deprecated
	 */
	//public abstract void run();
}
