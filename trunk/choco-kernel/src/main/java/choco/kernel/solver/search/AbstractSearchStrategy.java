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
import java.util.logging.Level;
import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.measures.ISolutionMeasures;
import choco.kernel.solver.variables.integer.IntVar;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.solver.variables.set.SetVar;


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


	protected ISolutionPool solutionPool = SolutionPoolFactory.makeSingleSolutionPool();

	/**
	 * count of the solutions found during search
	 */
	private int nbSolutions = 0;


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
			this.solutionPool = EmptySolutionPool.SINGLETON;
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
		sol.recordSolutionCount(nbSolutions);
		//record values
		for (int i = 0; i < solver.getNbIntVars(); i++) {
			final IntVar vari = solver.getIntVar(i);
			sol.recordIntValue(i, vari.isInstantiated() ? vari.getVal() : Integer.MAX_VALUE);

		}
		for (int i = 0; i < solver.getNbSetVars(); i++) {
			final SetVar vari = solver.getSetVar(i);
			sol.recordSetValue(i, vari.isInstantiated() ? vari.getValue() : null);
		}

		for (int i = 0; i < solver.getNbRealVars(); i++) {
			RealVar vari = solver.getRealVar(i);
			// if (vari.isInstantiated()) { // Not always "instantiated" : for
			// instance, if the branching
			// does not contain the variable, the precision can not be
			// reached....
			sol.recordRealValue(i, vari.getValue());
			// }
		}
		//AbstractOptimize overrides the method.
		//record objective
//		final AbstractGlobalSearchStrategy strategy = solver.getSearchStrategy();
//		if (solver.getSearchStrategy() instanceof AbstractOptimize) {
//			sol.recordIntObjective(((AbstractOptimize) strategy)
//					.getObjectiveValue());
//		}else {
//			sol.recordIntObjective(Integer.MAX_VALUE);
//		}
	}

	/**
	 * showing information about the last solution
	 */
	public void showSolution() {
		if(LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info(solver.pretty());
		}
	}


	public void restoreBestSolution() {
		solver.restoreSolution(solutionPool.getBestSolution());
	}

	public final List<Solution> getStoredSolutions(){
		return solutionPool.asList();
	}

	
	/**
	 * main entry point: running the search algorithm controlled the CPSolver object
	 * @deprecated
	 */
	//public abstract void run();
}
