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
package choco.model;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.DomOverWDegBranching;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.ChocoUtil;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static java.lang.System.currentTimeMillis;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 22 juil. 2008
 * Time: 10:27:41
 * Test suite concerning Decision variables
 */
public class ModelTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	@Test
	public void testOnDecisionVariables() {
		int n = 8;
		Model m = new CPModel();

		IntegerVariable[] queens = new IntegerVariable[n];
		IntegerVariable[] queensdual = new IntegerVariable[n];
		IntegerVariable toto = Choco.makeIntVar("toto", 1, 2);
		m.addVariable(toto);

		for (int i = 0; i < n; i++) {
			queens[i] = makeIntVar("Q" + i, 1, n);
			queensdual[i] = makeIntVar("QD" + i, 1, n);
		}

		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				int k = j - i;
				m.addConstraint(neq(queens[i], queens[j]));
				m.addConstraint(neq(queens[i], plus(queens[j], k)));  // diagonal constraints
				m.addConstraint(neq(queens[i], minus(queens[j], k))); // diagonal constraints
			}
		}
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				int k = j - i;
				m.addConstraint(neq(queensdual[i], queensdual[j]));
				m.addConstraint(neq(queensdual[i], plus(queensdual[j], k)));  // diagonal constraints
				m.addConstraint(neq(queensdual[i], minus(queensdual[j], k))); // diagonal constraints
			}
		}
		m.addConstraint(inverseChanneling(queens, queensdual));

		CPSolver s1 = new CPSolver();
		s1.read(m);
		s1.setVarIntSelector(new MinDomain(s1, s1.getVar(queens)));


		m.addVariables("cp:decision", queens);
		Solver s2 = new CPSolver();
		s2.read(m);
		s1.solveAll();
		s2.solveAll();
		Assert.assertEquals("No same number of solutions", s1.getNbSolutions(), s2.getNbSolutions());
		//Assert.assertEquals("No same number of nodes", s1.getSearchStrategy().getNodeCount(), s2.getSearchStrategy().getNodeCount());
	}

	@Test
	public void testOnNonDecisionVariables() {
		int n = 8;
		Model m = new CPModel();

		IntegerVariable[] queens = new IntegerVariable[n];
		IntegerVariable[] queensdual = new IntegerVariable[n];
		IntegerVariable toto = Choco.makeIntVar("toto", 1, 2);
		m.addVariable(toto);

		for (int i = 0; i < n; i++) {
			queens[i] = makeIntVar("Q" + i, 1, n);
			queensdual[i] = makeIntVar("QD" + i, 1, n);
		}

		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				int k = j - i;
				m.addConstraint(neq(queens[i], queens[j]));
				m.addConstraint(neq(queens[i], plus(queens[j], k)));  // diagonal constraints
				m.addConstraint(neq(queens[i], minus(queens[j], k))); // diagonal constraints
			}
		}
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				int k = j - i;
				m.addConstraint(neq(queensdual[i], queensdual[j]));
				m.addConstraint(neq(queensdual[i], plus(queensdual[j], k)));  // diagonal constraints
				m.addConstraint(neq(queensdual[i], minus(queensdual[j], k))); // diagonal constraints
			}
		}
		m.addConstraint(inverseChanneling(queens, queensdual));

		CPSolver s1 = new CPSolver();
		s1.read(m);
		//        s1.setVarIntSelector(new DomOverWDeg(s1, s1.getVar(queens)));
		s1.attachGoal(new DomOverWDegBranching(s1, new IncreasingDomain(), s1.getVar(queens)));

		m.addOption("cp:no_decision", toto);
		m.addOption("cp:no_decision", queensdual);
		Solver s2 = new CPSolver();
		s2.read(m);

		s1.solveAll();
		s2.solveAll();
		Assert.assertEquals("No same number of solutions", s1.getNbSolutions(), s2.getNbSolutions());
		//Assert.assertEquals("No same number of nodes", s1.getSearchStrategy().getNodeCount(), s2.getSearchStrategy().getNodeCount());
	}


	@Test
	public void testNullModel() {
		Solver s = new CPSolver();
		Model m = new CPModel();
		IntegerVariable v = makeIntVar("v", 0, 1);
		IntegerVariable w = makeIntVar("w", 0, 1);
		Constraint c = eq(v, 1);
		Constraint d = eq(v, w);

		for (int i = 0; i < 11; i++) {
			m.addVariables(v, w);
			m.addConstraints(c, d);
			String message = i + ": ";
			try {
				switch (i) {
				case 0:
					message += "No variable, no constraint";
					m.removeVariable(w);
					m.removeVariable(v);
					m.removeConstraint(c);
					m.removeConstraint(d);
					break;
				case 1:
					message += "One variable, no constraint";
					m.removeVariable(w);
					m.removeConstraint(c);
					m.removeConstraint(d);
					break;
				case 2:
					message += "One variable, One constraint";
					m.removeVariable(w);
					m.removeConstraint(d);
					break;
				case 3:
					message += "Two variables, no constraint";
					m.removeConstraint(c);
					m.removeConstraint(d);
					break;
				case 4:
					message += "Two variables, one constraint";
					m.removeConstraint(c);
					break;
				case 5:
					message += "Two variables, one constraint (2)";
					m.removeConstraint(d);
					break;
				case 6:
					message += "No variable, one constraint";
					m.removeVariables(v, w);
					m.removeConstraint(c);
					break;
				case 7:
					message += "No variable, one constraint(2)";
					m.removeVariables(v, w);
					m.removeConstraint(d);
					break;
				case 8:
					message += "No variable, two constraints";
					m.removeVariables(v, w);
					break;
				case 9:
					message += "One variable, two constraints";
					m.removeVariable(v);
					break;
				case 10:
					message += "One variable, two constraints(2)";
					m.removeVariable(w);
					break;
				}
				s.read(m);
			} catch (Exception e) {
				Assert.fail(message);
			}
			Iterator<Constraint> itc;
			try {
				itc = v.getConstraintIterator(m);
				while (itc.hasNext()) {
					itc.next();
				}
			} catch (Exception e) {
				Assert.fail("v iterator");
			}
			try {
				itc = w.getConstraintIterator(m);
				while (itc.hasNext()) {
					itc.next();
				}
			} catch (Exception e) {
				Assert.fail("w iterator");
			}
			Iterator<Variable> itv;
			try {
				itv = c.getVariableIterator();
				while (itv.hasNext()) {
					itv.next();
				}
			} catch (Exception e) {
				Assert.fail("c iterator");
			}
			try {
				itv = d.getVariableIterator();
				while (itv.hasNext()) {
					itv.next();
				}
			} catch (Exception e) {
				Assert.fail("d iterator");
			}
		}

	}



	@Test
	@Ignore
	public void testCharge1(){
		int cpt;
		int newcpt;
		int[] nbVar = new int[]{10, 100, 1000, 100000, 1000000};
		for(int i = 1; i < nbVar.length; i++) {
			Runtime.getRuntime().gc();
			long t = currentTimeMillis();
			int n = nbVar[i];
			int b = 100;
			Model m = new CPModel();
			IntegerVariable[] v = makeIntVarArray("v", n, 1, b);
			m.addVariables(v);
			newcpt = (int) Runtime.getRuntime().totalMemory() - (int) Runtime.getRuntime().freeMemory();
			//                if (cpt != 0) {
			//                    assertTrue(newcpt <= cpt + 10000);
			//                }
			cpt = newcpt;
            StringBuffer st = new StringBuffer();
			st.append(MessageFormat.format("|{0} |", ChocoUtil.pad("" + n, -9, " ")));
			st.append(MessageFormat.format("|{0} |", ChocoUtil.pad("" + (currentTimeMillis() - t), -5, " ")));
			st.append(MessageFormat.format("|{0} |", ChocoUtil.pad("" + cpt, -10, " ")));
            LOGGER.info(st.toString());
		}
	}


	@Test
	@Ignore
	public void testCharge2(){
		int cpt;
		int newcpt;
		int[] nbCstr = new int[]{10, 100, 1000, 100000, 1000000};
		for(int i = 1; i < nbCstr.length; i++) {
			Runtime.getRuntime().gc();
			long t = currentTimeMillis();
			int n = nbCstr[i];
			int b = 10;
			Model m = new CPModel();
			IntegerVariable v = makeIntVar("v", 1, b);
			for(int j=0; j < nbCstr[i]; j++){
				m.addConstraint(eq(v, 5));
			}
			newcpt = (int) Runtime.getRuntime().totalMemory() - (int) Runtime.getRuntime().freeMemory();
			//                if (cpt != 0) {
			//                    assertTrue(newcpt <= cpt + 10000);
			//                }
			cpt = newcpt;
            StringBuffer st = new StringBuffer();
			st.append(MessageFormat.format("|{0} |", ChocoUtil.pad("" + n, -9, " ")));
			st.append(MessageFormat.format("|{0} |", ChocoUtil.pad("" + (currentTimeMillis() - t), -5, " ")));
			st.append(MessageFormat.format("|{0} |", ChocoUtil.pad("" + cpt, -10, " ")));
            LOGGER.info(st.toString());
		}
	}


	@Test
	@Ignore
	public void testCharge3(){
		int cpt;
		int newcpt;
		int[] domSize = new int[]{10, 100, 1000, 100000};
		for(int i = 1; i < domSize.length; i++) {
			Runtime.getRuntime().gc();
			long t = currentTimeMillis();
			int n = domSize[i];
			IntegerVariable v = makeIntVar("v", 1, n);
			for(int j=0; j < n; j+=2){
				v.removeVal(j);
			}
			newcpt = (int) Runtime.getRuntime().totalMemory() - (int) Runtime.getRuntime().freeMemory();
			//                if (cpt != 0) {
			//                    assertTrue(newcpt <= cpt + 10000);
			//                }
			cpt = newcpt;
			StringBuffer st = new StringBuffer();
            st.append(MessageFormat.format("|{0} |", ChocoUtil.pad("" + n, -9, " ")));
			st.append(MessageFormat.format("|{0} |", ChocoUtil.pad("" + (currentTimeMillis() - t), -5, " ")));
			st.append(MessageFormat.format("|{0} |", ChocoUtil.pad("" + cpt, -10, " ")));
            LOGGER.info(st.toString());
		}
	}

	@Test
	public void testSeveralModels() {

		IntegerVariable v1 = makeIntVar("v1", 0, 10);
		IntegerVariable v2 = makeIntVar("v2", 0, 10);
		IntegerVariable v3 = makeIntVar("v3", 0, 10);

		CPModel m1 = new CPModel();
		CPModel m2 = new CPModel();

		m1.addConstraint(eq(v1, v2));
		m1.addConstraint(neq(v2, v3));

		Constraint ct = m1.getConstraint(0);
		m2.addConstraint(ct);

		CPSolver s1 = new CPSolver();
		CPSolver s2 = new CPSolver();

		s1.read(m1);
		s2.read(m2);

		LOGGER.info(MessageFormat.format("{0}", s1.pretty()));
		LOGGER.info(MessageFormat.format("{0}", s2.pretty()));

		s1.solveAll();
		s2.solveAll();
		org.junit.Assert.assertEquals(s2.getNbSolutions(), 11);
		org.junit.Assert.assertEquals(s1.getNbSolutions(), 110);


	}

}
