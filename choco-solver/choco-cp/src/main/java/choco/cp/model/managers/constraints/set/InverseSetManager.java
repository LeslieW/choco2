package choco.cp.model.managers.constraints.set;

import choco.cp.model.managers.MixedConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.set.InverseSet;
import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.List;

public final class InverseSetManager extends MixedConstraintManager {

	@Override
	public SConstraint makeConstraint(Solver solver, Variable[] variables,
			Object parameters, List<String> options) {
		if (solver instanceof CPSolver) {
			final CPSolver s = (CPSolver) solver;
			final int nbIntVars = (Integer) parameters;
			return new InverseSet(
					VariableUtils.getIntVar(s, variables, 0, nbIntVars),
					VariableUtils.getSetVar(s, variables, nbIntVars, variables.length)
			);
		}
		return null;
	}

}
