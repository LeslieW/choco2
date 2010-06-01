package samples.jobshop;

import choco.Choco;
import choco.Options;
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
import samples.tutorials.PatternExample;

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
		model = new CPModel();
		//variables
		durations =  Choco.makeIntVarArray("d", nbTasks,5, 55, Options.V_ENUM);
		tasks = Choco.makeTaskVarArray("t", 0, horizon, durations);
		for (TaskVariable t : tasks) t.start().addOption(Options.V_ENUM);
		sumDurations = Choco.makeIntVar("sumDur", nbTasks * MIN_DURATION, nbTasks * MAX_DURATION);
		model.addVariables(tasks);
		//constraints
		model.addConstraint(Choco.eq(sumDurations, Choco.sum(durations)));
		for (int i = 0; i < nbTasks; i++) {
			model.addConstraint( Choco.nth(tasks[i].start(), durationIfStartAt, tasks[i].duration()));
		}
		for (int i = 1; i < nbTasks; i++) {
			model.addConstraint( Choco.startsAfterEnd(tasks[i], tasks[i-1]));
		}

	}

	@Override
	public void buildSolver() {
		solver = new CPSolver();
		solver.read(model);
		final IntDomainVar[] v = { solver.getVar(sumDurations)};
		solver.attachGoal( new AssignOrForbidIntVarVal(new StaticVarOrder(solver, v), new MinVal()));
		solver.addGoal( new AssignVar(new MinDomain(solver, solver.getVar(durations)), new MinVal()));
		solver.addGoal( new IncompleteAssignvar(solver.getVar(tasks)));
	}

	@Override
	public void prettyOut() {
		if( LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info( StringUtils.pretty( solver.getVar(tasks)));
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
		solver.generateSearchStrategy();
		solver.setTimeLimit(120*1000);
		solver.minimize(solver.getVar(sumDurations), false);
	}


	class IncompleteAssignvar extends AssignVar {

		public IncompleteAssignvar(TaskVar[] tasks) {
			super(
					new StaticVarOrder(solver, VariableUtils.getStartVars(tasks)),
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
