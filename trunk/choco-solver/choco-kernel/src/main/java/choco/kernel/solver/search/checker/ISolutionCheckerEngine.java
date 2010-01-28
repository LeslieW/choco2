package choco.kernel.solver.search.checker;

import java.util.logging.Logger;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.Var;

public interface ISolutionCheckerEngine {

	public final static Logger LOGGER = ChocoLogging.getEngineLogger();

	void checkSolution(Solver solver) throws SolutionCheckerException;

	void checkVariables(Solver solver) throws SolutionCheckerException;

	void checkConstraints(Solver solver) throws SolutionCheckerException;

	void checkVariable(Var var) throws SolutionCheckerException;

	void checkConstraint(SConstraint c) throws SolutionCheckerException;

	boolean inspectSolution(Solver solver);

	boolean inspectVariables(Solver solver);

	boolean inspectConstraints(Solver solver);

	boolean inspectVariable(Var var);

	boolean inspectConstraint(SConstraint c);


}