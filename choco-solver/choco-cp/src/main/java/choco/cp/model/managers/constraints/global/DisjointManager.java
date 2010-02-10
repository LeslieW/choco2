package choco.cp.model.managers.constraints.global;

import java.util.Set;

import choco.cp.model.managers.IntConstraintManager;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

public class DisjointManager extends IntConstraintManager {

	@Override
	public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables,
			Object parameters, Set<String> options) {
		throw new UnsupportedOperationException("not yet implemented");
	}

}
