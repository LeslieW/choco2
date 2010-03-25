package samples.jobshop;

import choco.Choco;
import choco.cp.CPOptions;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.AssignOrForbidIntVarVal;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valselector.MinVal;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;
import samples.Examples.PatternExample;

import java.util.Random;
import java.util.logging.Level;

public class TaskPlanif2 extends PatternExample {

	private final static int MIN_DURATION = 10;
	private final static int MAX_DURATION = 50;


	/** number of tasks */
	public int nbTasks;

	/** horizon (number of periods */
	public int horizon;

	protected int[] durationIfStartAt;

	protected IntegerVariable[] durations;

	protected TaskVariable[] tasks;

	protected IntegerVariable sumDurations;

	@Override
	public void buildModel() {
		_m = new CPModel();
		//variables
		durations =  Choco.makeIntVarArray("d", nbTasks,5, 55, CPOptions.V_ENUM);
		tasks = Choco.makeTaskVarArray("t", 0, horizon, durations);
		for (TaskVariable t : tasks) t.start().addOption(CPOptions.V_ENUM);
		sumDurations = Choco.makeIntVar("sumDur", nbTasks * MIN_DURATION, nbTasks * MAX_DURATION);
		_m.addVariables(tasks);
		//constraints
		_m.addConstraint(Choco.eq(sumDurations, Choco.sum(durations)));
		for (int i = 0; i < nbTasks; i++) {
			_m.addConstraint( Choco.nth(tasks[i].start(), durationIfStartAt, tasks[i].duration()));
		}
		for (int i = 1; i < nbTasks; i++) {
			_m.addConstraint( Choco.startsAfterEnd(tasks[i], tasks[i-1]));
		}

	}

	@Override
	public void buildSolver() {
		_s = new CPSolver();
		_s.read(_m);
		final IntDomainVar[] v = { _s.getVar(sumDurations)};
		_s.attachGoal( new AssignOrForbidIntVarVal(new StaticVarOrder(_s, v), new MinVal()));
		_s.addGoal( new AssignVar(new MinDomain(_s, _s.getVar(durations)), new MinVal()));
		_s.addGoal( new IncompleteAssignvar(_s.getVar(tasks)));
	}

	@Override
	public void prettyOut() {
		if( LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info( StringUtils.pretty( _s.getVar(tasks)));
		}

	}

	@Override
	public void setUp(Object parameters) {
		final Object[] params = (Object[]) parameters;
		nbTasks = (Integer) params[0];
		horizon = (Integer) params[1];
		Random rnd = params.length == 3 ? new Random((Integer) params[0]) : new Random();
		durationIfStartAt = new int[horizon];
		for (int i = 0; i < horizon; i++) {
			durationIfStartAt[i] = MIN_DURATION + rnd.nextInt(MAX_DURATION - MIN_DURATION);
		}
	}

	@Override
	public void solve() {
		//ChocoLogging.setVerbosity(Verbosity.VERBOSE);
		_s.generateSearchStrategy();
		_s.setTimeLimit(120*1000);
		_s.minimize(_s.getVar(sumDurations), false);
	}


	class IncompleteAssignvar extends AssignVar {

		public IncompleteAssignvar(TaskVar[] tasks) {
			super(
					new StaticVarOrder(_s, VariableUtils.getStartVars(tasks)),
					new MinVal()
			);
		}

		@Override
		public boolean finishedBranching(IntBranchingDecision decision) {
			return true; //explore only one branch.
		}

		

	}

	
	public static void main(String[] args) {
		new TaskPlanif2().execute( new Object[]{10, 2000, 0});
	}

}
