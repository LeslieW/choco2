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
package samples.scheduling.pert;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.CPOptions;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.VizFactory;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.TaskUtils;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.logging.Logger;


/**
 * The following example is inspired from an example read in the Ilog Scheduler userguide.
 * We keep the duration but we change the task network.
 * Instead of dealing with resources, we perform Pert/CPM calculations.
 * We also provide a simple decision tool to find the solution with the minimal makespan and minimal price (for the given makespan) to deal with alternatives.
 * @author Arnaud Malapert
 * Date : 2 déc. 2008
 * Since : 2.0.1
 * Update : 2.0.1
 */
public class DeterministicPert {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

	protected static final int[] ILOG_DURATIONS={7,3,8,3,1,2,1,2,1,1};

	protected static final int[] EXAMPLE_DURATIONS={7,3,8,3,1,2,1,2,1,1};


	public final static int NB_TASKS=10;

	protected final int horizon;
	protected final CPModel model;
	protected CPSolver solver;

	protected final TaskVariable masonry, carpentry, plumbing, ceiling, roofing;
	protected final TaskVariable painting, windows, facade, garden, moving;

	protected final IntegerVariable[] durations;

	protected final TaskVariable[] tasks;

	public DeterministicPert(int horizon) {
		this(horizon,constantArray(ILOG_DURATIONS));
	}

	public DeterministicPert(int horizon,IntegerVariable[] durations) {
		super();
		model=new CPModel();
		this.horizon = horizon;
		this.durations=durations;
		/* CREATE THE ACTIVITIES. */
		masonry=makeTaskVar("masonry", horizon, durations[0], CPOptions.V_BOUND);
		carpentry= makeTaskVar("carpentry", horizon, durations[1], CPOptions.V_BOUND);
		plumbing= makeTaskVar("plumbing", horizon, durations[2], CPOptions.V_BOUND);
		ceiling= makeTaskVar("ceiling", horizon,  durations[3], CPOptions.V_BOUND);
		roofing= makeTaskVar("roofing", horizon, durations[4], CPOptions.V_BOUND);
		painting= makeTaskVar("painting", horizon, durations[5], CPOptions.V_BOUND);
		windows= makeTaskVar("windows", horizon,  durations[6], CPOptions.V_BOUND);
		facade= makeTaskVar("facade", horizon,  durations[7], CPOptions.V_BOUND);
		garden= makeTaskVar("garden", horizon,  durations[8], CPOptions.V_BOUND);
		moving= makeTaskVar("moving", horizon,  durations[9], CPOptions.V_BOUND);
		tasks = new TaskVariable[]{masonry, carpentry, plumbing, ceiling, roofing,painting, windows, facade, garden, moving};
		addTemporalConstraints();
	}

	
	protected void addTemporalConstraints() {
		model.addConstraints(
				Choco.startsAfterEnd(carpentry,masonry),
				Choco.startsAfterEnd(plumbing,masonry),
				Choco.startsAfterEnd(ceiling,masonry),
				Choco.startsAfterEnd(roofing,carpentry),
				//model.pfactory.startsAfterEnd(painting,ceiling), //removed from the original network
				Choco.startsAfterEnd(roofing,ceiling), //precedence added
				Choco.startsAfterEnd(windows,roofing),
				Choco.startsAfterEnd(painting,windows), //added
				Choco.startsAfterEnd(facade,roofing),
				Choco.startsAfterEnd(facade,plumbing),
				Choco.startsAfterEnd(garden,roofing),
				Choco.startsAfterEnd(garden,plumbing),
				//model.pfactory.startsAfterEnd(moving,windows),//removed
				Choco.startsAfterEnd(moving,facade),
				Choco.startsAfterEnd(moving,garden),
				Choco.startsAfterEnd(moving,painting)
		);
	}

	/**
	 * function used in junit tests.
	 */
	public void requireUnaryResource() {
		model.addConstraints( Choco.disjunctive(tasks));
	}

	public final CPModel getModel() {
		return model;
	}

	public void draw() {
		//FIXME VizFactory.toDotty(solver.getSchedulerConfiguration().getPrecedenceNetwork());
	}

	protected void criticalPathMethod(CPSolver solver) {
		//precedence are represente with linear constraints
		solver.setHorizon(horizon);
		solver.read(model);
		solver.postMakespanConstraint();
		try {
			solver.propagate();
		} catch (ContradictionException e) {
			LOGGER.info("infeasible pert problem");
			e.printStackTrace();
		}
		try {
			//then we instantiate the makespan variable and compute slack times
			IntDomainVar e = solver.getMakespan();
			LOGGER.info(e.pretty());
			e.instantiate(e.getInf(), null, true);
			solver.propagate();
			LOGGER.info("\nCRITICAL PATH METHOD");
			//LOGGER.info(solver.pretty());
			//LOGGER.info(this);
		} catch (ContradictionException e) {
			LOGGER.severe("ERROR : problem should be feasible.");
			e.printStackTrace();
		}
	}

	public void criticalPathMethod() {
		solver=new CPSolver();
		criticalPathMethod(solver);
	}


	public int solveAll() {
		solver=new CPSolver();
		
		solver.read(model);
		solver.solveAll();
		return solver.getNbSolutions();
	}

	protected static IntegerVariable[] createDurationVariables(int[][] durations) {
		IntegerVariable[] vars = new IntegerVariable[NB_TASKS];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = makeIntVar("p-"+i, durations[i]);
		}
		return vars;
	}

	public final boolean isCritical(int i) {
		return solver.getVar(tasks[i]).isScheduled();
	}
	protected int getSlack(TaskVariable task) {
		return TaskUtils.getSlack(solver.getVar(task));
	}

	protected StringBuilder toString(int i) {
		StringBuilder buffer=new StringBuilder();
		buffer.append(solver.getVar(tasks[i]).pretty());
		buffer.append("\tslack=").append(getSlack(tasks[i]));
		return buffer;
	}


	public void generateDottyFile() {
		LOGGER.severe("not yet implemented");
		//FIXME VizFactory.toDotty(solver);
	}

	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("Cmax=").append( solver.getMakespan().getVal());
		buffer.append('\n');
		for (int i = 0; i < tasks.length; i++) {
			buffer.append(toString(i));
			buffer.append('\n');
		}
		return new String(buffer);
	}

	public static void main(String[] args) {
		DeterministicPert example=new DeterministicPert(28);
		example.criticalPathMethod();
		//example.generateDottyFile();
		LOGGER.severe(""+example.solveAll());
	//	ProbabilisticPert example1=new ProbabilisticPert(500,ProbabilisticPert.addExpectedTime(ProbabilisticPert.EXAMPLE_DURATIONS));
	//	example1.computeAllCPM();
	//	example1.generateDottyFile();
		int d=25;
	//	System.err.println(" probability of meeting the date "+d+": "+example1.computeProbability(25));
//		System.err.println(example1.computeProbability(26));
//		System.err.println(example1.computeProbability(27));
//		System.err.println(example1.computeProbability(30));
//		OptimizeAssignment example2=new OptimizeAssignment(200,OptimizeAssignment.EXAMPLE_DURATIONS,OptimizeAssignment.EXAMPLE_COSTS);
//		example2.computeAll();
//		example2.generateDottyFile();
	}


}

