package choco.kernel.solver.search.checker;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.Var;

import java.util.logging.Logger;

public interface ISolutionCheckerEngine {

	public final static Logger LOGGER = ChocoLogging.getEngineLogger();

    /**
     * Check the current solution of the {@code solver}.
     * It runs over variables (check instantiation) and constraints (call isSatisfied).
     * By defautlt, it checks the consistency and ignore the nogood recording.
     *
     * @param solver involving solver
     * @throws SolutionCheckerException if the current solution is not correct.
     */
	void checkSolution(Solver solver) throws SolutionCheckerException;

    /**
     * Check instantiation of every variables involved within the {@code solver}.
     * @param solver containing solver
     * @throws SolutionCheckerException if one or more variable is not instantiated.
     */
	void checkVariables(Solver solver) throws SolutionCheckerException;

    /**
     * Check satisfaction of every constraints involved within the {@code solver}.
     * @param solver containing solver
     * @throws SolutionCheckerException if one or more constraint is not satisfied.
     */
	void checkConstraints(Solver solver) throws SolutionCheckerException;

    /**
     * Check the instantiation of {@code var}.
     * @param var variable to check
     * @throws SolutionCheckerException if {@code var} is not instantiated.
     */
	void checkVariable(Var var) throws SolutionCheckerException;

    /**
     * Check the satisfaction of {@code c}.
     * @param c constraint to check
     * @throws SolutionCheckerException if {@code c} is not satisfied
     */
	void checkConstraint(SConstraint<?> c) throws SolutionCheckerException;

    /**
     * Inspect the current solution of {@code solver}.
     * It runs over variables (check instantiation) and constraints (call isSatisfied).
     * By defautlt, it checks the consistency and ignore the nogood recording.
     * @param solver involving solver
     * @return false if the current solution is not correct
     */
	boolean inspectSolution(Solver solver);

    /**
     * Inspect instantiation of every variables involved in {@code solver}.
     * @param solver containing solver.
     * @return false if one or more variable is not instantiated.
     */
	boolean inspectVariables(Solver solver);

    /**
     * Inspect satisfaction of every constraints declared in {@code solver}.
     * @param solver containing solver
     * @return false if one or more constraint is not satisfied.
     */
	boolean inspectConstraints(Solver solver);

    /**
     * Inspect the instantiation of {@code var}.
     * @param var variable to check
     * @return false if the variable is not instantiated.
     */
	boolean inspectVariable(Var var);

    /**
     * Inspect the satisfaction of {@code c}.
     * @param c contraint to check
     * @return false if the constraint is not satisfied.
     */
	boolean inspectConstraint(SConstraint<?> c);


}