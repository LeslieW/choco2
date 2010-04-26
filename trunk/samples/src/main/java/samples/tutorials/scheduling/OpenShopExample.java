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
import choco.cp.solver.search.task.SetTimes;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.comparator.TaskComparators;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.variables.scheduling.ITask;

import java.util.Comparator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Arnaud Malapert
 *
 */
public class OpenShopExample {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

	public static int MAX_DURATION=200;

	public final int n;

	public boolean restart=true;

	public Random rnd;

	public TaskVariable[][] tasks;

	public final Constraint[] jobs;

	public final Constraint[] machines;

	public final int[][] durations;

	public CPModel model;

	public OpenShopExample(int n) {
		super();
		this.n=n;
		this.durations=new int[n][n];
		//this.tasks=new TaskVariable[n][n];
		this.jobs=new Constraint[n];
		this.machines=new Constraint[n];
		this.rnd=new Random();
	}


	public void generateInstance() {
		LOGGER.log(Level.INFO,"generate a new open shop instance {0}x{0}",n);
		for (int i = 0; i < durations.length; i++) {
			for (int j = 0; j < durations[i].length; j++) {
				durations[i][j]=rnd.nextInt(MAX_DURATION);
			}
		}
	}

	public void generateModel() {
		model=new CPModel();
		//TASKS
		tasks = Choco.makeTaskVarArray("T", 0, MAX_DURATION*n, durations, Options.V_BOUND);
		//RESOURCES
		for (int i = 0; i < n; i++) {
			machines[i] = Choco.disjunctive(tasks[i]);
			TaskVariable[] job = ArrayUtils.getColumn(tasks, i);
			jobs[i] = Choco.disjunctive(job);
		}
		model.addConstraints(jobs);
		model.addConstraints(machines);
		//model.addConstraint(Scheduling.pert());
		//LOGGER.info(model.pretty());
	}


	public void setTimes(CPSolver solver, Comparator<ITask> cmp, boolean randomized) {
		solver.attachGoal(new SetTimes(solver, solver.getTaskDecisionVars(), cmp, randomized));
		solver.addGoal(solver.generateDefaultIntGoal());
	}

	public CPSolver[] generateSolvers() {
		int n=5;
		CPSolver[] solvers = new CPSolver[n];
		for (int i = 0; i < solvers.length; i++) {
			solvers[i]=new CPSolver();
		}
		for (CPSolver s : solvers) {
			//s.setHorizon(MAX_DURATION*n*n);
			s.read(model);
			s.setDoMaximize(false);
			s.setObjective(s.getMakespan());
			s.setRestart(restart);
			s.setFirstSolution(false);
			s.setTimeLimit(5000);
		}
		 setTimes(solvers[1], TaskComparators.makeEarliestStartingTimeCmp(), false);
		 setTimes(solvers[2], TaskComparators.makeEarliestStartingTimeCmp(), true);
		 solvers[3].setRandomSelectors();

//		solvers[4].attachGoal(new CstrBranching(new ProbProfileSelector(solvers[4],solvers[4].getUnaryResources(),new RandomReverse())));
//		solvers[4].addGoal(solvers[4].generateIntGoal());
//		solvers[4].addGoal(new FinishProject(solvers[4]));
		return solvers;
	}
}
