/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package samples.tutorials.scheduling;


import static choco.Choco.boolChanneling;
import static choco.Choco.constant;
import static choco.Choco.constantArray;
import static choco.Choco.cumulativeMax;
import static choco.Choco.eq;
import static choco.Choco.makeBooleanVarArray;
import static choco.Choco.makeIntVar;
import static choco.Choco.makeTaskVarArray;
import static choco.Choco.startsAt;
import static choco.Choco.sum;
import static choco.Options.NO_OPTION;
import static choco.Options.V_BOUND;
import static choco.Options.V_OBJECTIVE;

import java.util.Arrays;
import java.util.logging.Level;

import samples.tutorials.PatternExample;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.visu.components.chart.ChocoChartFactory;


public class CumulativeScheduling extends PatternExample {

	protected final static int NT = 11, NF = 3, N =  NT + NF;

	private final static int[] HEIGHTS =  new int[]{2, 1, 4, 2, 3, 1, 5, 6, 2, 1, 3, 1, 1, 2};

	private final static int[] DURATIONS =  new int[]{1, 1, 1, 2, 1, 3, 1, 1, 3, 4, 2, 3, 1, 1};

	private final static int HORIZON = 6;

	private final static int CAPACITY = 7;

	private boolean useAlternativeResource;

	private IntegerVariable[] usages, heights;

	private TaskVariable[] tasks;

	private Constraint cumulative;

	@Override
	public void setUp(Object parameters) {
		if (parameters instanceof Boolean) {
			useAlternativeResource = ((Boolean) parameters).booleanValue();
		}else {
			useAlternativeResource = true;
		}
		super.setUp(parameters);
	}

	@Override
	public void buildModel() {
		//the fake tasks to establish the profile capacity of the ressource are the NF firsts.
		tasks = makeTaskVarArray("t", 0, HORIZON, DURATIONS, V_BOUND);
		model = new CPModel();
		//set fake tasks to establish the profile capacity
		model.addConstraints(
				startsAt(tasks[0], 1),
				startsAt(tasks[1], 2),
				startsAt(tasks[2], 3)
		);
		usages = makeBooleanVarArray("U", NT);
		//state the objective function
		model.addConstraint(eq( sum(usages), makeIntVar("obj", 0, NT, V_BOUND, V_OBJECTIVE)));
		if(useAlternativeResource) {
			heights = constantArray(HEIGHTS);
			cumulative = cumulativeMax("alt-cumulative", tasks, heights, usages, constant(CAPACITY),NO_OPTION);
		}else {
			heights = new IntegerVariable[N];
			//post the channeling to know if the task uses the resource or not.
			for (int i = 0; i < NF; i++) {
				heights[i] = constant(HEIGHTS[i]);
			}
			for (int i = NF; i < N; i++) {
				heights[i] =  makeIntVar("H_" + i, new int[]{0, HEIGHTS[i]});
				model.addConstraint(boolChanneling(usages[i- NF], heights[i], HEIGHTS[i]));
			}
			cumulative =cumulativeMax("cumulative", tasks, heights, constant(CAPACITY), "");
		}
		model.addConstraint(cumulative);
	}

	@Override
	public void buildSolver() {
		solver = new CPSolver();
		solver.read(model);
	}

	@Override
	public void prettyOut() {
		if(LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info((useAlternativeResource ? "Alternative Resource" : "Channeling Constraints")+" Model: \n");
			if(solver.existsSolution()) {
				LOGGER.info("makespan: "+solver.getObjectiveValue()+"\n"+Arrays.toString(usages));
				final String title = "Cumulative Packing Constraint Visualization";
				ChocoChartFactory.createAndShowGUI(title, ChocoChartFactory.createCumulativeChart(title, (CPSolver) solver, cumulative, true));
			}
		}
	}

	@Override
	public void solve() {
		solver.maximize(false);
	}

	@Override
	public void execute() {
		super.execute(Boolean.TRUE);
	}

	public static void main(String[] args) {
		new CumulativeScheduling().execute(Boolean.FALSE);
		new CumulativeScheduling().execute(Boolean.TRUE);
	}

}
