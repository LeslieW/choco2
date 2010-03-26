package choco.cp.model.managers.constraints.global;

import java.util.Set;

import choco.cp.model.managers.MixedConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.scheduling.disjunctive.ForbiddenIntervals;
import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class ForbiddenIntervalsManager extends MixedConstraintManager {


	private static int index = 0;

	@Override
	public SConstraint makeConstraint(Solver solver, Variable[] variables,
			Object parameters, Set<String> options) {
		if(solver instanceof CPSolver){
			CPSolver s = (CPSolver) solver;
			IntDomainVar uppBound;
			TaskVar[] taskvars;
			final int  ubIdx = variables.length -1;
			final String name = parameters == null ? "ForbInt-"+(index++) : parameters.toString();
			if( variables[ubIdx] instanceof IntegerVariable) {
				uppBound = s.getVar((IntegerVariable) variables[ubIdx]);
				taskvars = VariableUtils.getTaskVar(s, variables, 0, ubIdx);
			} else {
				uppBound = s.getSchedulerConfiguration().createMakespan(s);
				taskvars = VariableUtils.getTaskVar(s, variables, 0, variables.length);
			}
			return new ForbiddenIntervals(solver, name, taskvars, uppBound);
		}
		return null;
	}


}
