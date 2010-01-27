package choco.kernel.solver.search;

import java.util.List;
import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;

public interface ISolutionPool {

	public final static Logger LOGGER = ChocoLogging.getEngineLogger();

	void clear();

	int getCapacity();

	void setCapacity(int capacity);

	boolean isEmpty();

	int size();

	/**
	 * the best/last recorded solution.
	 * @return
	 */
	Solution getBestSolution();

	/**
	 * use {@link AbstractSearchStrategy#writeSolution(Solution)} with the target object of the pool, if any.
	 * @param solver
	 */
	void recordSolution(Solver solver);

	List<Solution> asList();
}