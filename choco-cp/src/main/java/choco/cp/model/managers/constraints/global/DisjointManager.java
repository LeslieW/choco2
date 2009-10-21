package choco.cp.model.managers.constraints.global;

import choco.cp.model.managers.IntConstraintManager;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.HashSet;

public class DisjointManager extends IntConstraintManager {

	@Override
	public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables,
			Object parameters, HashSet<String> options) {
		throw new UnsupportedOperationException("not yet implemented");
	}

}
