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

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.BranchingFactory;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

import java.util.Random;
import java.util.logging.Logger;

import static choco.Choco.*;

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
public class AirPlaneLanding {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

	public final static Random RND=new Random(1);


	/**
	 * Each plane is represented as a task. its starting time is its landing time and its duration its landing time.
	 * the landing strip is represented as an unary resource (capacity 1)
	 */
	protected TaskVariable[] planes;

	/** Each plane has a tardiness*/
	protected IntegerVariable[] tardiness;

	/** the objective to minimize*/
	protected IntegerVariable objective;

	/**
	 * It represent the arrival time of each plane.
	 */
	protected final IntegerVariable[] arrivalTimes;

	/** Each plane has a landing duration*/
	protected final IntegerVariable[] landingTimes;

	/** Each plane has a number of passengers.*/
	protected final int[] numberOfPassengers;

	/** The scheduling horizon. */
	protected int horizon = 2000;

	public CPModel model;

	public CPSolver solver;

	public AirPlaneLanding(int[] arrivalTimes,int[] landingTimes,int[] numberOfPassengers) {
		super();
		this.arrivalTimes= constantArray(arrivalTimes);
		this.landingTimes= constantArray(landingTimes);
		this.numberOfPassengers=numberOfPassengers;
	}


	public static int[] generateRandomBoundedArray(int n,int lb,int ub) {
		final int[] tmp = new int[n];
		final int m = ub-lb;
		for (int i = 0; i < tmp.length; i++) {
			tmp[i]=lb + RND.nextInt(m);
		}
		return tmp;
	}
	public static AirPlaneLanding generateInstance(int n,int maxArrivalTime, int minDuration, int maxDuration, int minPassengers, int maxPassengers) {
		return new AirPlaneLanding(generateRandomBoundedArray(n, 0, maxArrivalTime),
				generateRandomBoundedArray(n, minDuration, maxDuration),
				generateRandomBoundedArray(n, minPassengers, maxPassengers));
	}

	public static AirPlaneLanding generateInstanceSmall(int n) {
		return generateInstance(n, 200, 5, 20, 5, 20);
	}

	public static AirPlaneLanding generateInstanceMedium(int n) {
		return generateInstance(n, 1000, 5, 100, 20, 80);
	}

	public static AirPlaneLanding generateInstanceLarge(int n) {
		return generateInstance(n, 5000, 10, 200, 50, 250);
	}

	public  void createModel() {
		model= new CPModel();
		//create Tasks
		planes = Choco.makeTaskVarArray("plane", 0, horizon, landingTimes);
		//create resource
		model.addConstraint( Choco.disjunctive("landingStrip", planes));
		//create Tardiness
		tardiness = makeIntVarArray("tardiness", planes.length, 0, horizon, Options.V_BOUND, Options.V_NO_DECISION);
		//create objective
		for (int i = 0; i < planes.length; i++) {
			// tardiness[i] = start[i] - arrival[i]
			model.addConstraint(eq(tardiness[i], minus(planes[i].start(),arrivalTimes[i])));
			//arrival time constraint
			model.addConstraint(geq(planes[i].start(),arrivalTimes[i]));
			planes[i].end().addOption(Options.V_NO_DECISION);
		}
		objective = makeIntVar("objective",0, planes.length*horizon, Options.V_BOUND, Options.V_OBJECTIVE, Options.V_NO_DECISION);
		model.addConstraint(eq(objective,scalar(numberOfPassengers, tardiness)));
		//model.addConstraint(Scheduling.pert());
			
	}



	public  void createSolver() {
		solver = new CPSolver();
		solver.setHorizon(horizon);
		solver.read(model);
		//LOGGER.info(model.pretty());
		//LOGGER.info(solver.pretty());
		//TODO add (find !) a good search strategy
		solver.attachGoal(BranchingFactory.setTimes(solver));
		solver.minimize(false);
		solver.printRuntimeStatistics();
        LOGGER.info(solver.pretty());

	}
	public static void main(String[] args) {
		int n = 5;
		AirPlaneLanding apl = generateInstanceSmall(n);
		apl.createModel();
		apl.createSolver();

	}
}

