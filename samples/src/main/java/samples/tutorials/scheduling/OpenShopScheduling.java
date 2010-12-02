/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package samples.tutorials.scheduling;

import static choco.Choco.disjunctive;
import static choco.Choco.makeIntVar;
import static choco.Choco.makeTaskVarArray;
import static choco.Options.V_BOUND;
import static choco.Options.V_MAKESPAN;
import static choco.Options.V_NO_DECISION;
import static choco.Options.V_OBJECTIVE;

import java.util.logging.Level;

import samples.tutorials.PatternExample;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.configure.LimitFactory;
import choco.cp.solver.configure.StrategyFactory;
import choco.cp.solver.search.BranchingFactory;
import choco.kernel.common.util.comparator.TaskComparators;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.model.ModelException;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.Configuration;
import choco.kernel.solver.search.limit.Limit;
import choco.visu.components.chart.ChocoChartFactory;
import choco.visu.components.chart.renderer.MyXYBarRenderer.ResourceRenderer;

public class OpenShopScheduling extends PatternExample {

	public final static int[][] GP03_01 = {
		{661, 168, 171},
		{6, 489, 505},
		{333, 343, 324}
	};

	private int[][] durations;

	/**
	 * range from 0 to 3
	 */
	public int branching = 3;


	public final void setBranching(int branching) {
		this.branching = branching;
	}

	@Override
	public void setUp(Object parameters) {
		super.setUp(parameters);
		if (parameters instanceof int[][]) {
			durations = (int[][]) parameters;
		} else throw new ModelException("invalid parameters");
	}

	@Override
	public void buildModel() {
		model=new CPModel();
		//Variables
		final int h = MathUtils.sum(durations); // dummy horizon
		final TaskVariable[][] tasks = makeTaskVarArray("T", 0, h, durations, V_BOUND);
		model.addVariable(makeIntVar("makespan",0, h, V_BOUND, V_OBJECTIVE, V_MAKESPAN, V_NO_DECISION));
		//Machines
		for (int i = 0; i < tasks.length; i++) {
			model.addConstraint( disjunctive(tasks[i]));
		}
		//Jobs
		for (int i = 0; i < tasks[0].length; i++) {
			model.addConstraint( disjunctive(ArrayUtils.getColumn(tasks, i)));
		}
	}

	@Override
	public void buildSolver() {
		solver = new CPSolver();
		solver.read(model);
		StrategyFactory.setDoOptimize(solver, false);
		solver.getConfiguration().putBoolean(Configuration.STOP_AT_FIRST_SOLUTION, false);
		LimitFactory.setSearchLimit(solver, Limit.TIME, 5000);
		solver.clearGoals();
		switch (branching) {
		case 0: solver.addGoal(BranchingFactory.setTimes(solver));break;
		case 1: solver.addGoal(BranchingFactory.setTimes(solver, TaskComparators.makeEarliestStartingTimeCmp(), true));break;
		case 2: ((CPSolver) solver).setRandomSelectors();break;
		default: //default branching
			break;
		}
	}

	@Override
	public void prettyOut() {
		if(LOGGER.isLoggable(Level.INFO) && solver.existsSolution()) {
				LOGGER.log(Level.INFO, "makespan: {0}", solver.getObjectiveValue());
				final String title = "Disjunctive Constraints Visualization";
				ChocoChartFactory.createAndShowGUI(title, ChocoChartFactory.createUnaryHChart(title, solver));
		}
	}

	@Override
	public void solve() {
		solver.generateSearchStrategy();
		solver.launch();
	}

	@Override
	public void execute() {
		this.execute(GP03_01);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new OpenShopScheduling().execute();

	}

}
