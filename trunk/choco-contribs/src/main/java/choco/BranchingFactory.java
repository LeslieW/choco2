package choco;

import java.util.Arrays;
import java.util.Comparator;

import choco.cp.solver.search.integer.branching.AssignOrForbidIntVarVal;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valselector.MinVal;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.cp.solver.search.set.AssignSetVar;
import choco.cp.solver.search.set.MinEnv;
import choco.cp.solver.search.set.RandomSetValSelector;
import choco.cp.solver.search.set.RandomSetVarSelector;
import choco.cp.solver.search.set.StaticSetVarOrder;
import choco.cp.solver.search.task.SetTimes;
import choco.kernel.common.util.comparator.TaskComparators;
import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.ITask;
import choco.kernel.solver.variables.scheduling.TaskVar;
import choco.kernel.solver.variables.set.SetVar;

public final class BranchingFactory {

	private BranchingFactory() {
		super();
	}

	//*************************************************************************//

	public static AssignVar minDomMinVal(Solver s, IntDomainVar[] vars) {
		return new AssignVar( new MinDomain(s, vars), new MinVal());
	}

	public static AssignVar minDomMinVal(Solver s) {
		return minDomMinVal(s, VariableUtils.getIntVars(s));
	}

	//*************************************************************************//

	public static AssignVar lexicographic(Solver solver, IntDomainVar[] vars) {
		return new AssignVar( new StaticVarOrder(solver, vars), new MinVal());
	}

	public static AssignSetVar lexicographic(Solver solver, SetVar[] vars) {
		return new AssignSetVar( new StaticSetVarOrder(solver, vars), new MinEnv());
	}

	//*************************************************************************//

	public static AssignOrForbidIntVarVal randomIntBinSearch(Solver solver, long seed) {
		return randomBinSearch(solver, VariableUtils.getIntVars(solver), seed);
	}

	public static AssignOrForbidIntVarVal randomBinSearch(Solver solver, IntDomainVar[] vars, long seed) {
		return new AssignOrForbidIntVarVal( new RandomIntVarSelector(solver, vars, seed), new RandomIntValSelector(seed));
	}

	//*************************************************************************//

	public static AssignVar randomIntSearch(Solver solver, long seed) {
		return randomSearch(solver, VariableUtils.getIntVars(solver), seed);
	}

	public static AssignVar randomSearch(Solver solver, IntDomainVar[] vars, long seed) {
		return new AssignVar( new RandomIntVarSelector(solver, vars, seed), new RandomIntValSelector(seed));
	}

	//*************************************************************************//

	public static AssignSetVar randomSetSearch(Solver solver, long seed) {
		return randomSearch(solver, VariableUtils.getSetVars(solver), seed);
	}

	public static AssignSetVar randomSearch(Solver solver, SetVar[] vars, long seed) {
		return new AssignSetVar(new RandomSetVarSelector(solver, vars, seed), new RandomSetValSelector(seed));
	}

	//*************************************************************************//

	public static SetTimes setTimes(final Solver solver) {
		final TaskVar[] tasks = VariableUtils.getTaskVars(solver);
		Arrays.sort(tasks,TaskComparators.makeRMinDurationCmp()); 
		return new SetTimes(solver,Arrays.asList(tasks) , TaskComparators.makeEarliestStartingTimeCmp(), false);
	}

	//FIXME set array in constructor
	public static SetTimes setTimes(final Solver solver, final TaskVar[] tasks, final Comparator<ITask> comparator, final boolean randomized) {
		return new SetTimes(solver, Arrays.asList(tasks), comparator, randomized);
	}


}
