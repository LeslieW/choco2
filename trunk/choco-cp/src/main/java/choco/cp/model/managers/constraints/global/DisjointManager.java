package choco.cp.model.managers.constraints.global;

import java.util.HashSet;

import choco.cp.model.managers.IntConstraintManager;
import choco.kernel.model.variables.Variable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

public class DisjointManager extends IntConstraintManager {

	@Override
	public SConstraint makeConstraint(Solver solver, Variable[] variables,
			Object parameters, HashSet<String> options) {
		throw new UnsupportedOperationException("not yet implemented");
	}

}
