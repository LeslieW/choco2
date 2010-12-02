package samples.tutorials.visu;

import static choco.Choco.constantArray;
import static choco.Choco.cumulativeMax;
import static choco.Choco.makeIntVar;
import static choco.Choco.makeTaskVarArray;
import static choco.Choco.pack;
import static choco.Options.V_BOUND;
import static choco.Options.V_MAKESPAN;
import static choco.Options.V_NO_DECISION;
import static choco.Options.V_OBJECTIVE;

import java.util.logging.Level;

import samples.tutorials.PatternExample;
import choco.cp.model.CPModel;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.cp.solver.preprocessor.PreProcessConfiguration;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.common.util.tools.VariableUtils;
import choco.kernel.model.constraints.pack.PackModel;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.visu.components.chart.ChocoChartFactory;

public class ResourceGUI extends PatternExample {

	private final static int[] DURATIONS = {
		5, 7, 9, 10, 5, 4, 3, 6, 11, 8, 4, 6, 2, 2, 7, 3, 7, 10, 3, 5	
	};

	private final static int[][] HEIGHTS = {
		{3, 7, 6, 6, 5, 4, 5, 2, 1, 3, 5, 2, 4, 2, 1, 5, 3, 2, 1, 2},
		{2, 4, 5, 1, 2, 7, 7, 4, 2, 1, 5, 7, 1, 7, 2, 1, 2, 3, 4, 5},
		{2, 1, 3, 4, 5, 5, 4, 4, 3, 3, 2, 1, 4, 5, 5, 3, 4, 2, 5, 2}
	};

	@Override
	public void buildModel() {
		model = new CPModel();
		final int horizon = 200;
		final TaskVariable[] tasks = makeTaskVarArray("T", 0, horizon, DURATIONS);
		final PackModel pm = new PackModel(VariableUtils.getStartVariables(tasks), constantArray(HEIGHTS[2]), 10);
		model.addConstraints(
				cumulativeMax(tasks, HEIGHTS[0], 10),
				cumulativeMax(tasks, HEIGHTS[1], 10),
			pack(pm)
		);
		model.addVariable( makeIntVar("makespan", 0, horizon, V_OBJECTIVE, V_MAKESPAN, V_NO_DECISION, V_BOUND));
	}

	@Override
	public void buildSolver() {
		ChocoLogging.setVerbosity(Verbosity.SEARCH);
		solver = new PreProcessCPSolver();
		PreProcessConfiguration.cancelPreProcess(solver);
		solver.getConfiguration().putTrue(PreProcessConfiguration.DISJUNCTIVE_FROM_CUMULATIVE_DETECTION);
		solver.read(model);
		solver.setTimeLimit(1000);
		
	}

	@Override
	public void prettyOut() {
		if(LOGGER.isLoggable(Level.INFO) && solver.existsSolution()) {
				LOGGER.log(Level.INFO, "makespan: {0}", solver.getObjectiveValue());
				ChocoChartFactory.createAndShowGUI("Resource Constraints Visualization", solver);
		}
	}

	@Override
	public void solve() {
		solver.minimize(false);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		(new ResourceGUI()).execute();
	}

}
