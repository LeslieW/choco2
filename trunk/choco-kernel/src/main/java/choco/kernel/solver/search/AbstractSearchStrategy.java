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
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.solver.variables.set.SetVar;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	public ArrayList<Solution> solutions; //Solution[]

	/**
	 * capacity of the history record (keeping solutions)
	 */
	public int maxNbSolutionStored = 5;



	public AbstractSearchStrategy() {
		solutions = new ArrayList<Solution>();
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
	 * recording the current state as a solution
	 * stores information from the current state in the next solution of the model
	 * note: only instantiated variables are recorded in the Solution object
	 * either all variables or a user-defined subset of them are recorded
	 * this may erase a soolution that was previously stored in the ith position
	 * this may also increase the size of the pb.solutions vector.
	 */
	public void recordSolution() {
		//    Solution sol = makeSolutionFromCurrentState();
		Solution sol = solver.recordSolution();
		storeSolution(sol);
	}

	protected Solution makeSolutionFromCurrentState() {
		int nbv = solver.getNbIntVars();
		Solution sol = new Solution(solver);
		// sol.time = time_read()
		for (int i = 0; i < nbv; i++) {
			IntDomainVar vari = (IntDomainVar) solver.getIntVar(i);
			if (vari.isInstantiated()) {
				sol.recordIntValue(i, vari.getVal());
			}
		}
		int nbsv = solver.getNbSetVars();
		for (int i = 0; i < nbsv; i++) {
			SetVar vari = solver.getSetVar(i);
			if (vari.isInstantiated()) {
				sol.recordSetValue(i, vari.getValue());
			}
		}
		int nbrv = solver.getNbRealVars();
		for (int i = 0; i < nbrv; i++) {
			RealVar vari = solver.getRealVar(i);
			//if (vari.isInstantiated()) { // Not always "instantiated" : for instance, if the branching
			// does not contain the variable, the precision can not be reached....
			sol.recordRealValue(i, vari.getValue());
			//}
		}
		if (this instanceof AbstractOptimize) {
			sol.recordIntObjective(((AbstractOptimize) this).getObjectiveValue());
		}
		return sol;
	}

	/**
	 * showing information about the last solution
	 */
	public void showSolution() {
		if(LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info(solver.pretty());
		}
	}

	public void storeSolution(Solution sol) {
		//[SVIEW] store solution ~S // sol,
		if (solutions.size() == maxNbSolutionStored) {
			solutions.remove(solutions.size() - 1);
		}
		solutions.add(0, sol);
	}

	public boolean existsSolution() {
		return (solutions.size() > 0);
	}

	protected Solution getBestSolution() {
		return existsSolution() ? solutions.get(0) : null;
	}
	public void restoreBestSolution() {
		solver.restoreSolution(getBestSolution());
	}

	public ArrayList<Solution> getStoredSolutions(){
		return solutions;
	}

	/**
	 * main entry point: running the search algorithm controlled the CPSolver object
	 * @deprecated
	 */
	//public abstract void run();
}
