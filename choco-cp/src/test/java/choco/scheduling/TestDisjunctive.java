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

import static choco.cp.solver.SettingType.DEFAULT_FILTERING;
import static choco.cp.solver.SettingType.DETECTABLE_PRECEDENCE;
import static choco.cp.solver.SettingType.EDGE_FINDING_D;
import static choco.cp.solver.SettingType.NF_NL;
import static choco.cp.solver.SettingType.OVERLOAD_CHECKING;
import static choco.cp.solver.SettingType.VILIM_FILTERING;

import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;

import samples.scheduling.DisjunctiveWebEx;
import choco.Choco;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.BitFlags;
import choco.cp.solver.constraints.global.scheduling.Disjunctive;
import choco.cp.solver.constraints.global.scheduling.Disjunctive.Rule;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;


class DisjProblem extends AbstractTestProblem {

	public boolean forbidInt=false;


	public DisjProblem(IntegerVariable[] durations) {
		super(durations);
	}

	public DisjProblem(IntegerVariable[] starts, IntegerVariable[] durations) {
		super(starts, durations);
	}

	public DisjProblem() {
		super();
	}


	@Override
	protected Constraint[] generateConstraints() {
		return new Constraint[]{ Choco.disjunctive(tasks)};
	}

	@Override
	public void generateSolver() {
		generateSolver(Rule.NONE);
	}

	protected void setFlags(Rule rule) {
		if(rule==Rule.NONE) {
			setFlags(TestDisjunctive.SETTINGS);
		}else {
			Disjunctive cstr = (Disjunctive) solver.getCstr(this.rsc);
			cstr.setSingleRule(rule);
		}
	}

	public void generateSolver(Rule rule) {
		super.generateSolver();
		setFlags(rule);
	}


}

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
			//Rule[] rules = new Rule[]{ Rule.NONE,Rule.EF_LCT};
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
		final DisjunctiveWebEx cwe = new DisjunctiveWebEx();
		cwe.execute(Boolean.FALSE);
		final Number obj = cwe._s.getObjectiveValue();
		cwe.execute(Boolean.TRUE);
		Assert.assertEquals("Disjunctive Website Example",obj, cwe._s.getObjectiveValue());
	}

}
