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
package scheduling;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.SettingType;
import static choco.cp.solver.SettingType.*;
import choco.cp.solver.constraints.BitFlags;
import choco.cp.solver.constraints.global.scheduling.disjunctive.Disjunctive.Rule;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import junit.framework.Assert;
import org.junit.Test;
import samples.tutorials.scheduling.DisjunctiveWebEx;

import java.util.logging.Logger;


/**
 * @author Arnaud Malapert
 */
public class TestDisjunctive {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	public final static BitFlags SETTINGS = new BitFlags();

	public final static int NB_TESTS = 4;

	protected static void solveAll(DisjProblem dp, final int nbSol) {
		dp.generateSolver();
		//LOGGER.info(dp.solver.pretty());
		SchedUtilities.solveRandom(dp.solver, nbSol, -1, "disj " + SETTINGS.toSettingsLabels());
		//Utilities.solveTotOrder(apc.createProblem(),nbSol,-1,"disj Total Order "+apc.DisjSettings.toString());
	}

	public static void launchAllRules(DisjProblem apc, final int nbSol) {
		launchAllRules(apc, NB_TESTS, nbSol);
	}
	public static void launchAllRules(DisjProblem apc, final int nbTests, final int nbSol) {
		apc.initializeModel();
		for (int i = 0; i < nbTests; i++) {
			//Rule[] rules = new Rule[]{Rule.NOT_LAST};
			Rule[] rules = Rule.values();
			for (int j = 1; j < rules.length; j++) {
				apc.generateSolver(rules[j]);
				SchedUtilities.solveRandom(apc.solver, nbSol, -1, "disj " + rules[j]);
			}
			SETTINGS.clear();
			SETTINGS.set(DEFAULT_FILTERING);
			SETTINGS.set(OVERLOAD_CHECKING);
			solveAll(apc, nbSol);

			SETTINGS.set(NF_NL);
			SETTINGS.set(DETECTABLE_PRECEDENCE);
			solveAll(apc, nbSol);

			SETTINGS.set(EDGE_FINDING_D);
			apc.generateSolver();
			CPSolver s = apc.solver;
			SETTINGS.set(VILIM_FILTERING);
			apc.generateSolver();
			SchedUtilities.compare(nbSol, -1, "Default vs Vilim", s, apc.solver);
		}
	}

	

	@Test
	public void testToyProblem() {
		final int[] pt = {4, 6, 2};
		final int[] min = {1, 3, 6};
		final int[] max = {4, 7, 11};
		IntegerVariable[] s = SchedUtilities.makeIntvarArray("start", min, max);
		IntegerVariable[] d = Choco.constantArray(pt);
		launchAllRules(new DisjProblem(s, d), 1);
	}


	@Test
	public void testToyProblem2() {
		final int[] pt = {11, 10, 5};
		final int[] min = {0, 1, 14};
		final int[] max = {14, 17, 30};
		IntegerVariable[] s = SchedUtilities.makeIntvarArray("start", min, max);
		IntegerVariable[] d = Choco.constantArray(pt);
		launchAllRules(new DisjProblem(s, d), 238);
	}


	@Test
	public void testToyProblem3() {
		final int[] pt = {5, 16, 9};
		final int[] min = {0, 0, 0};
		final int[] max = {25, 14, 21};
		IntegerVariable[] s = SchedUtilities.makeIntvarArray("start", min, max);
		IntegerVariable[] d = Choco.constantArray(pt);
		launchAllRules(new DisjProblem(s, d), 6);
	}

	@Test
	public void testToyProblem4() {
		final int[] pt = {4, 5, 6};
		final int[] min = {0, 0, 0};
		final int[] max = {11, 10, 9};
		IntegerVariable[] s = SchedUtilities.makeIntvarArray("start", min, max);
		IntegerVariable[] d = Choco.constantArray(pt);
		launchAllRules(new DisjProblem(s, d), 6);
	}

	@Test
	public void gotToBeFactoriel() {
		LOGGER.info("warning : be patient");
		int[] sizes = {3, 4, 5};
		int[] horizon = {30,25, 15};
		DisjProblem[] pb={new DisjProblem()};
		//pb[1].forbidInt=true;
		//DisjProblem[] pb={new DisjSchedProblem()};
        for (DisjProblem aPb : pb) {
            //pb[k].forbidInt=true;
            for (int aHorizon : horizon) {
                aPb.setHorizon(aHorizon);
                LOGGER.info("" + aPb.horizon);
                for (int size : sizes) {
                    LOGGER.info("sizes=" + size);
                    aPb.setRandomProblem(size);
                    launchAllRules(aPb, Math.max(NB_TESTS / 2, 1),(int) MathUtils.factoriel(size));
                }
            }
        }
	}


	@Test
	public void testVariableDuration0() {
		int[] min = {2, 2};
		int[] max = {3, 2};
		DisjProblem dp = new DisjProblem(SchedUtilities.makeIntvarArray("d", min, max));
		dp.setHorizon(4);
		launchAllRules(dp, 2);
	}

	@Test
	public void testVariableDuration1() {
		int[] min = {2, 2};
		int[] max = {3, 3};
		DisjProblem dp = new DisjProblem(SchedUtilities.makeIntvarArray("d", min, max));
		dp.setHorizon(5);
		launchAllRules(dp, 10);
	}

	@Test
	public void testVariableDuration2() {
		// triplet (3,5,3) : 6 permutations * 4 configuration : 24 solutions
		// triplet (3,5,4),(4,5,3) : 2*6 solutions
		//triplet (4,5,4) infaisable
		int[] min = {3, 5, 3};
		int[] max = {4, 5, 4};
		DisjProblem dp = new DisjProblem(SchedUtilities.makeIntvarArray("d", min, max));
		dp.setHorizon(12);
		launchAllRules(dp, 36);
	}


	
	@Test
	public void testExampleDisjunctiveWebSite() {
		//ChocoLogging.setVerbosity(Verbosity.SEARCH);
		final DisjunctiveWebEx cwe = new DisjunctiveWebEx();
		cwe.execute(Boolean.FALSE);
		final Number obj = cwe._s.getObjectiveValue();
		cwe.execute(Boolean.TRUE);
		Assert.assertEquals("Disjunctive Website Example",obj, cwe._s.getObjectiveValue());
	}

	@Test
	public void testBugNilDuration1() throws ContradictionException {
		//bug detected on: [T_5_7[0, 31], T_6_7[31, 191], T_4_7[199, 325], T_2_7[315, 523], T_1_7[513, 860], T_0_7[885, 1098], T_7_7[1104, 1104], T_3_7[1097, 1104]]
		final TaskVariable[] tasks = { Choco.makeTaskVar("t1", 0, 5, 5), Choco.makeTaskVar("t2", 5, 10, 5),Choco.makeTaskVar("t2", 9, 16, 6), Choco.makeTaskVar("t1", 16,16, 0)};
		Model m = new CPModel();
		m.addConstraint( Choco.disjunctive(tasks, SettingType.NF_NL.getOptionName()));
		Solver s = new CPSolver();
		s.read(m);
		s.propagate();
	}
	
	@Test
	public void testBugNilDuration2() throws ContradictionException {
		//bug detected on: [T_5_7[0, 31], T_6_7[31, 191], T_4_7[199, 325], T_2_7[315, 523], T_1_7[513, 860], T_0_7[885, 1098], T_7_7[1104, 1104], T_3_7[1097, 1104]]
		final TaskVariable[] tasks = { Choco.makeTaskVar("t1", 0, 5, 5), Choco.makeTaskVar("t2", 5, 10, 5),Choco.makeTaskVar("t2", 9, 16, 6), Choco.makeTaskVar("t1", 16,16, 0)};
		final IntegerVariable[] usages = Choco.makeBooleanVarArray("u", 2);
		Model m = new CPModel();
		m.addConstraint( Choco.disjunctive(tasks, usages, SettingType.NF_NL.getOptionName()));
		Solver s = new CPSolver();
		s.read(m);
		s.getVar(usages[0]).setVal(1);
		s.getVar(usages[1]).setVal(1);
		s.propagate();
	}
	
}
