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

import java.util.List;
import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.measure.ISolutionMeasures;


/**
 * An abstract class handling the control for solving a model
 */
public abstract class AbstractSearchStrategy implements ISolutionMeasures {

	/**
	 * an object for logging trace statements
	 */
	protected final static Logger LOGGER = ChocoLogging.getSearchLogger();

	/**
	 * The (optimization or decision) model to which the entity belongs.
	 */

	public Solver solver;


	protected ISolutionPool solutionPool;

	/**
	 * count of the solutions found during search
	 */
	protected int nbSolutions = 0;


	public AbstractSearchStrategy() {
		super();
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

	@Override
	public final boolean existsSolution() {
		return nbSolutions > 0;
	}

	@Override
	public final int getSolutionCount() {
		return nbSolutions;
	}


	public final ISolutionPool getSolutionPool() {
		return solutionPool;
	}

	/**
	 * a null argument cancel the solution recording.
	 */
	public final void setSolutionPool(ISolutionPool solutionPool) {
		if(solutionPool == null) {
			this.solutionPool = NoSolutionPool.SINGLETON;
		}
		this.solutionPool = solutionPool;
	}

	
	public final void resetSolutions() {
		solutionPool.clear();
		nbSolutions = 0;
		solver.setFeasible(null);
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
		solver.setFeasible(Boolean.TRUE);
		nbSolutions++;
		solutionPool.recordSolution(solver);
	}


	public void writeSolution(Solution sol) {
		sol.setSolver(solver);
		sol.recordSolutionCount(nbSolutions);
		//record values
		sol.recordIntValues();
		sol.recordSetValues();
		sol.recordRealValues();
	}


	public void restoreBestSolution() {
		solver.restoreSolution(solutionPool.getBestSolution());
	}

	public final List<Solution> getStoredSolutions(){
		return solutionPool.asList();
	}

	
}
