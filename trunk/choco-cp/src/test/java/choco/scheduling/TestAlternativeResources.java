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
package choco.scheduling;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import org.junit.Test;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * @author Arnaud Malapert</br>
 * @since 2 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
class AltDisjProblem extends DisjProblem {



	protected IntegerVariable[] usages;

	protected final int type;

	public AltDisjProblem(int[] durations, int type) {
		super(Choco.constantArray(durations));
		this.type = type;
	}

	@Override
	protected Constraint[] generateConstraints() {
		return new Constraint[]{ Choco.disjunctive(tasks, usages)};
	}

	@Override
	public void initializeModel() {
		usages = Choco.makeBooleanVarArray("U", durations.length);
		super.initializeModel();
		model.addConstraint(Choco.eq(Choco.sum(usages), tasks.length - type));

	}

}



class AltCumulProblem extends CumulProblem {

	protected IntegerVariable[] usages;

	protected final int type;

	public AltCumulProblem(int[] durations, int[] heights, int type) {
		super(durations, heights);
		this.type = type;
	}

	@Override
	protected Constraint[] generateConstraints() {
		return new Constraint[]{ Choco.cumulative(null, tasks, heights, usages, consumption, capacity)};
	}

	@Override
	public void initializeModel() {
		usages = Choco.makeBooleanVarArray("U", durations.length);
		super.initializeModel();
		model.addConstraint(Choco.eq(Choco.sum(usages), tasks.length - type));

	}

}

/**
 * @author Arnaud Malapert</br>
 * @since 2 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class TestAlternativeResources {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	protected void testAltDisjunctive(int nbTests, int[] durations, int makespan, int type) {
		int nbSols = solveDisjSubProblems(durations, makespan, type);
		AltDisjProblem pb = new AltDisjProblem(durations, type);
		pb.setHorizon(makespan);
		TestDisjunctive.launchAllRules(pb,nbTests, nbSols);
	}

	protected void testAltCumulative(int nbTests, int[] durations, int[] heights, int capa, int makespan, int type) {
		int nbSols = solveCumulSubProblems(durations, heights, capa, makespan, type);
		AltCumulProblem pb = new AltCumulProblem(durations, heights,type);
		pb.setCapacity(capa);
		pb.setHorizon(makespan);
		TestCumulative.launchOnlysweep(pb, nbTests, nbSols, SchedUtilities.NO_CHECK_NODES);
	}

	protected int[] getNbSolutionFactors(int[] durations, int makespan, int type) {
		final int n = durations.length;
		switch (type) {
		case 1: {
			int[] factors = new int[n];
			for (int i = 0; i < n; i++) {
				factors[i] = makespan - durations[i] + 1;
			}
			return factors;
		}
		case 2: {
			int[] factors = new int[(n * (n - 1)) / 2];
			int cpt = 0;
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					factors[cpt++] = (makespan - durations[i] + 1)
					* (makespan - durations[j] + 1);
				}
			}
			return factors;
		}
		default:
			return null;
		}

	}

	private static int[][] createHeightSubsets(int[] tasks, int type) {
		final int n = tasks.length;
		switch (type) {
		case 1: {
			int[][] subsets = new int[n][n - type];
			for (int i = 0; i < n; i++) {
                System.arraycopy(tasks, 0, subsets[i], 0, i);
                System.arraycopy(tasks, i + 1, subsets[i], i + 1 - 1, n - (i + 1));
			}
			return subsets;
		}
		case 2: {
			int[][] subsets = new int[(n * (n - 1)) / 2][n
			                                             - type];
			int line = 0;
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					int cpt = 0;
					for (int k = 0; k < n; k++) {
						if (k != i && k != j) {
							subsets[line][cpt++] = tasks[k];
						}
					}
					++line;
				}
			}
			return subsets;
		}
		default:
			return null;
		}
	}

	private static TaskVariable[][] createTaskSubsets(TaskVariable[] tasks, int type) {
		final int n = tasks.length;
		switch (type) {
		case 1: {
			TaskVariable[][] subsets = new TaskVariable[n][n - type];
			for (int i = 0; i < n; i++) {
                System.arraycopy(tasks, 0, subsets[i], 0, i);
                System.arraycopy(tasks, i + 1, subsets[i], i + 1 - 1, n - (i + 1));
			}
			return subsets;
		}
		case 2: {
			TaskVariable[][] subsets = new TaskVariable[(n * (n - 1)) / 2][n
			                                                               - type];
			int line = 0;
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					int cpt = 0;
					for (int k = 0; k < n; k++) {
						if (k != i && k != j) {
							subsets[line][cpt++] = tasks[k];
						}
					}
					++line;
				}
			}
			return subsets;
		}
		default:
			return null;
		}
	}

	protected int solveDisjSubProblems(int[] durations, int makespan, int type) {
		final TaskVariable[] tasks = Choco.makeTaskVarArray("T", 0, makespan,
				durations);
		TaskVariable[][] subsets = createTaskSubsets(tasks, type);
		int[] factors = getNbSolutionFactors(durations, makespan, type);
		int nbSols = 0;
		for (int i = 0; i < factors.length; i++) {
			Model m = new CPModel();
			m.addConstraint(Choco.disjunctive(subsets[i]));
			CPSolver solver = new CPSolver();
			solver.setHorizon(makespan);
			solver.read(m);
			solver.solveAll();
			int subNbSols = solver.getNbSolutions() * factors[i];
			LOGGER.info("Nb sols reduced subproblem " + i + ": "
					+ factors[i] + " x " + solver.getNbSolutions() + " = "
					+ subNbSols);
			// The eliminated task could be anywhere in the schedule
			nbSols += subNbSols;
		}
		return nbSols;
	}

	protected int solveCumulSubProblems(int[] durations, int[] heights, int capa, int makespan, int type) {
		final TaskVariable[] tasks = Choco.makeTaskVarArray("T", 0, makespan,
				durations);
		TaskVariable[][] subsets = createTaskSubsets(tasks, type);
		int[][] hsubsets = createHeightSubsets(heights, type);
		int[] factors = getNbSolutionFactors(durations, makespan, type);
		int nbSols = 0;
		for (int i = 0; i < factors.length; i++) {
			Model m = new CPModel();
			m.addConstraint(Choco.cumulativeMax(subsets[i], hsubsets[i], capa));
			CPSolver solver = new CPSolver();
			solver.setHorizon(makespan);
			solver.read(m);
			solver.solveAll();
			int subNbSols = solver.getNbSolutions() * factors[i];
			LOGGER.info("Nb sols reduced subproblem " + i + ": "
					+ factors[i] + " x " + solver.getNbSolutions() + " = "
					+ subNbSols);
			// The eliminated task could be anywhere in the schedule
			nbSols += subNbSols;
		}
		return nbSols;
	}

	@Test
	public void testAltDisj1() {
		int n = 4;
		int[] durations = ArrayUtils.oneToN(n);
		testAltDisjunctive(TestDisjunctive.NB_TESTS, durations, (n * (n - 1)) / 2, 2);
	}

	@Test
	public void testAltDisj2() {
		int n = 5;
		int[] durations = ArrayUtils.oneToN(n);
		testAltDisjunctive(1,durations, (n * (n - 1)) / 2, 1);
	}

	@Test
	public void testAltDisj3() {
		int n = 5;
		int[] durations = ArrayUtils.oneToN(n);
		testAltDisjunctive(1, durations, ((n - 1) * (n - 2)) / 2 + n / 2, 2);
	}

	@Test
	public void testEmptyResource() {
		int n = 4;
		int[] durations = ArrayUtils.oneToN(n);
		AltDisjProblem pb = new AltDisjProblem(durations, 4);
		pb.setHorizon(4);
		TestDisjunctive.launchAllRules(pb, 2 * 3 * 4);
	}

	@Test
	public void testAltCumul1() {
		int n = 3;
		int[] durations = ArrayUtils.oneToN(n);
		int[] heights = new int[n];
		Arrays.fill(heights, n/2);
		testAltCumulative(TestCumulative.NB_TEST, durations, heights, n, n*(n+1)/4+3, 1);
		testAltCumulative(TestCumulative.NB_TEST, durations, heights, n, n*(n+1)/4+3, 2);
	}

	@Test
	public void testAltCumul2() {
		int n = 4;
		int[] durations = ArrayUtils.oneToN(n);
		int[] heights = new int[n];
		Arrays.fill(heights, n/2);
		testAltCumulative(TestCumulative.NB_TEST, durations, heights, n, n*(n+1)/4-1, 1);
		testAltCumulative(TestCumulative.NB_TEST, durations, heights, n, n*(n+1)/4+3, 2);	
	}
	
	@Test
	public void testAltCumul3() {
		int n = 5;
		int[] durations = ArrayUtils.oneToN(n);
		int[] heights = new int[n];
		Arrays.fill(heights, n/2);
		testAltCumulative(5, durations, heights, n, n*(n+1)/4-2, 1);
		testAltCumulative(4, durations, heights, n, n*(n+1)/4+2, 1);
		testAltCumulative(4, durations, heights, n, n*(n+1)/4, 2);
	}
	



}