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

import static choco.Choco.constantArray;
import static choco.Choco.disjunctive;
import static choco.Choco.eq;
import static choco.Choco.makeIntVar;
import static choco.Choco.makeIntVarArray;
import static choco.Choco.makeTaskVarArray;
import static choco.Choco.minus;
import static choco.Choco.scalar;
import static choco.Options.V_BOUND;
import static choco.Options.V_NO_DECISION;
import static choco.Options.V_OBJECTIVE;

import java.util.Arrays;
import java.util.logging.Level;

import samples.tutorials.PatternExample;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.cp.solver.preprocessor.PreProcessConfiguration;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.visu.components.chart.ChocoChartFactory;
import choco.visu.components.chart.renderer.MyXYBarRenderer.ResourceRenderer;

/**
 *
 * n planes must land on a landing strip.
 * Each plane has an arrival time, a landing duration time and a number of passengers.
 * We want to prioritize planes according to the number of passengers.
 * the objective is to minimize the weighted sum of tardiness.
 *
 *
 * @author Arnaud Malapert
 *
 */
public class AirPlaneLanding extends PatternExample {

	//20 Tasks ; Horizon 12 hours : 12 x 4 = 48 (15mn)
	private final static int[][] APL = {
		{
			0, 0, 2, 4, 8,
			8, 12, 12, 12, 12, 
			16, 18, 22, 24, 28,
			32, 32, 35, 35, 40
		},
		{
			16, 18, 14, 18, 21,
			25, 26, 27, 29, 32,
			34, 35, 35, 37, 38,
			40, 40, 43, 46,
			48, 46, 46, 48, 48
		},
		{ 
			2, 3, 2, 1, 3, 
			3, 2, 1, 3, 2,
			1, 3, 2, 4, 2,
			3, 2, 2, 1, 2
		},
		{
			200, 400, 250, 125, 450,
			500, 250, 150, 500, 220,
			125, 400, 200, 800, 175,
			400, 250, 175, 80, 250			
		}
	};

	//DATA

	/** Each plane has an arrival time. */
	private int[] arrivalTimes;

	/** Each plane has a landing deadline.*/
	private int[] deadlines;

	/** Each plane has a landing duration*/
	private int[] landingDurations;

	/** Each plane has a number of passengers.*/
	private int[] numberOfPassengers;

	private int maxTardiness;
	//vARIABLES

	/**
	 * Each plane is represented as a task. its starting time is its landing time and its duration its landing time.
	 */
	protected TaskVariable[] planes;

	/** Each plane has a tardiness (starting time - arrival time)*/
	protected IntegerVariable[] tardiness;

	/** the objective to minimize*/
	protected IntegerVariable weightedSumOfCompletionTimes;

	private boolean useDisjMod = true;

	@Override
	public void setUp(Object parameters) {
		super.setUp(parameters);
		if (parameters instanceof int[][]) {
			int[][] params = (int[][]) parameters;
			this.arrivalTimes= params[0];
			this.deadlines = params[1];
			this.landingDurations= params[2];
			this.numberOfPassengers=params[3];
			this.maxTardiness = 0;
			for (int i = 0; i < arrivalTimes.length; i++) {
				maxTardiness = Math.max( maxTardiness, deadlines[i] - landingDurations[i] - arrivalTimes[i]);
			}
		}
	}


	@Override
	public  void buildModel() {
		model = new CPModel();
		//create Tasks
		planes = makeTaskVarArray("plane", arrivalTimes, deadlines, constantArray(landingDurations));
		/* the landing strip is represented as an unary resource (capacity 1) */
		model.addConstraint( disjunctive("LandS", planes));
		// tardiness = start - arrivalTime;
		tardiness = makeIntVarArray("tardiness", planes.length, 0, maxTardiness, V_BOUND, V_NO_DECISION);
		for (int i = 0; i < planes.length; i++) {
			model.addConstraint( eq(tardiness[i], minus(planes[i].start(),arrivalTimes[i])));
		}
		//create objective
		weightedSumOfCompletionTimes = makeIntVar("objective",0, planes.length* maxTardiness * MathUtils.max(numberOfPassengers), V_BOUND, V_OBJECTIVE, V_NO_DECISION);
		model.addConstraint(eq(weightedSumOfCompletionTimes,scalar(numberOfPassengers, tardiness)));
	}


	@Override
	public void buildSolver() {
		if(useDisjMod) {
			solver = new PreProcessCPSolver();
			PreProcessConfiguration.keepSchedulingPreProcess(solver);
			
		} else {
			solver = new CPSolver();
		}
		solver.read(model);
		solver.setTimeLimit(2000);
		
		//solver.clearGoals();
	}


	@Override
	public void solve() {
		LOGGER.info(solver.pretty());
		ChocoLogging.setVerbosity(Verbosity.VERBOSE);
		solver.minimize(false);
	}

	
	@Override
	public void execute() {
		super.execute(APL);
	}

	@Override
	public void prettyOut() {
		if(LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info( (useDisjMod? "Disjunctive" : "Simple")+" Model: ");
			if(solver.existsSolution()) {
				LOGGER.info("cost: "+solver.getObjectiveValue()+"\n"+Arrays.toString(solver.getVar(planes)));
				final String title = "Landing Strip Visualization";
				ChocoChartFactory.createAndShowGUI(title, ChocoChartFactory.createUnaryVChart(title, solver));
			}
		}
	}


	public static void main(String[] args) {
		(new AirPlaneLanding()).execute();
	}
}

