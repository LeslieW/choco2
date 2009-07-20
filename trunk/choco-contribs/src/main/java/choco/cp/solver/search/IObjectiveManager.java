package choco.cp.solver.search;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.variables.Var;

public interface IObjectiveManager {
	
	Var getObjective();
	
	/**
	 * fast access to objective value for integer optimization
	 */
	int getObjectiveIntValue();
	
	/**
	 * fast access to objective value for real optimization
	 */
	double getObjectiveRealValue();
	
	
	/**
	 * v1.0 accessing the objective value of an optimization model
	 * (note that the objective value may not be instantiated, while all other variables are)
	 *
	 * @return the current objective value
	 */
	Number getObjectiveValue();
	
	
	Number getBestObjectiveValue();
	
	/**
	 * the target for the objective function: we are searching for a solution at least as good as this (tentative bound)
	 */
	Number getObjectiveTarget();

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
	 * resetting the values of the target bounds (bounds for the remaining search)
	 */
	void setTargetBound();
	
	/**
	 * propagating the optimization cuts from the new target bounds
	 */
	void postTargetBound() throws ContradictionException;

	
}