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
package choco.model.constraints.integer;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.MathUtil;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import static java.text.MessageFormat.format;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 17 janv. 2007
 * Time: 14:44:10
 */
public class MaxTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	private CPModel m;
	private CPSolver s;

	@Before
	public void before(){
		m = new CPModel();
		s = new CPSolver();
	}

	@After
	public void after(){
		m = null;
		s = null;
	}

	@Test
	public void test1() {
		for (int i = 0; i <= 10; i++) {
			m = new CPModel();
			s= new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 5);
			IntegerVariable y = makeIntVar("y", 1, 5);
			IntegerVariable z = makeIntVar("z", 1, 5);
			IntegerVariable w = makeIntVar("w", 1, 5);
			m.addConstraint(max(new IntegerVariable[]{x, y, z},w));
			s.read(m);
			s.setVarIntSelector(new RandomIntVarSelector(s, i));
			s.setValIntSelector(new RandomIntValSelector(i + 1));
			s.solve();
			do {
				/*LOGGER.info("" + x.getVal() + "=max(" + y.getVal() + "," +
                z.getVal()+")");*/
			} while (s.nextSolution() == Boolean.TRUE);
			LOGGER.info("" + s.getSearchStrategy().getNodeCount());
			assertEquals(125, s.getNbSolutions());
			//LOGGER.info("Nb solution : " + s.getNbSolutions());
		}
	}



	@Test
	public void test2() {
		for (int i = 0; i <= 10; i++) {
			m = new CPModel();
			s= new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 5);
			IntegerVariable y = makeIntVar("y", 1, 5);
			IntegerVariable z = makeIntVar("z", 1, 5);
			m.addVariables("cp:bound", x, y, z);
			IntegerVariable w = makeIntVar("z", 1, 5);
			m.addConstraint(max(new IntegerVariable[]{x, y, z},w));
			s.setVarIntSelector(new RandomIntVarSelector(s, i));
			s.setValIntSelector(new RandomIntValSelector(i + 1));
			s.read(m);
			s.solve();
			do {
				//LOGGER.info("" + x.getVal() + "=max(" + y.getVal() + "," +
				//�    z.getVal()+")");
			} while (s.nextSolution() == Boolean.TRUE);
			LOGGER.info("" + s.getSearchStrategy().getNodeCount());
			assertEquals(125, s.getNbSolutions());
			//LOGGER.info("Nb solution : " + s.getNbSolutions());
		}
	}

	@Test
	public void test2bis() {
		for (int i = 0; i <= 10; i++) {
			m = new CPModel();
			s= new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 5);
			IntegerVariable y = makeIntVar("y", 1, 5);
			IntegerVariable z = makeIntVar("z", 1, 5);
			m.addVariables("cp:bound", x, y, z);
			m.addConstraint(max(y, z, x));
			s.setVarIntSelector(new RandomIntVarSelector(s, i));
			s.setValIntSelector(new RandomIntValSelector(i + 1));
			s.read(m);
			s.solve();
			do {
				//LOGGER.info("" + x.getVal() + "=max(" + y.getVal() + "," +
				//�    z.getVal()+")");
			} while (s.nextSolution() == Boolean.TRUE);
			LOGGER.info("" + s.getSearchStrategy().getNodeCount());
			assertEquals(25, s.getNbSolutions());
			//LOGGER.info("Nb solution : " + s.getNbSolutions());
		}
	}


	@Test
	public void test3() {
		Random rand = new Random();
		for (int i = 0; i <= 10; i++) {
			m = new CPModel();
			s= new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 5);
			if (rand.nextBoolean()) {
				m.addVariable("cp:bound", x);
			}
			IntegerVariable y = makeIntVar("y", 1, 5);
			if (rand.nextBoolean()) {
				m.addVariable("cp:bound", y);
			}
			IntegerVariable z = makeIntVar("z", 1, 5);
			if (rand.nextBoolean()) {
				m.addVariable("cp:bound", z);
			}

			m.addConstraint(max(new IntegerVariable[]{y, z}, x));
			s.setVarIntSelector(new RandomIntVarSelector(s, i));
			s.setValIntSelector(new RandomIntValSelector(i + 1));
			s.read(m);
			s.solve();
			do {
				/*LOGGER.info("" + x.getVal() + "=max(" + y.getVal() + "," +
                z.getVal()+")");*/
			} while (s.nextSolution() == Boolean.TRUE);
			assertEquals(25, s.getNbSolutions());
			//LOGGER.info("Nb solution : " + s.getNbSolutions());
		}
	}

	@Test
	public void testPropagMaxTern1() {
		IntegerVariable y = makeIntVar("y", 1, 5);
		IntegerVariable z = makeIntVar("z", 1, 2);
		IntegerVariable max = makeIntVar("max", 1, 5);
		m.addConstraint(max(z, y, max));
		s.read(m);
		try {
			s.getVar(max).remVal(3);
			s.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		LOGGER.info(format("max {0}", s.getVar(max).getDomain().pretty()));
		LOGGER.info(format("y {0}", s.getVar(y).getDomain().pretty()));
		LOGGER.info(format("z {0}", s.getVar(z).getDomain().pretty()));
		LOGGER.info(format("{0}", !s.getVar(y).canBeInstantiatedTo(3)));
		assertTrue(!s.getVar(y).canBeInstantiatedTo(3));
	}

	@Test
	public void testPropagMaxTern2() {
		IntegerVariable y = makeIntVar("y", 1, 5);
		IntegerVariable z = makeIntVar("z", 1, 5);
		IntegerVariable max = makeIntVar("max", 1, 5);
		m.addConstraint(max(z, y, max));
		s.read(m);
		try {
			s.getVar(y).remVal(3);
			s.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		LOGGER.info(format("max {0}", s.getVar(max).getDomain().pretty()));
		LOGGER.info(format("y {0}", s.getVar(y).getDomain().pretty()));
		LOGGER.info(format("z {0}", s.getVar(z).getDomain().pretty()));
		LOGGER.info(format("{0}", s.getVar(z).canBeInstantiatedTo(3) && s.getVar(max).canBeInstantiatedTo(3)));
		assertTrue(s.getVar(z).canBeInstantiatedTo(3) && s.getVar(max).canBeInstantiatedTo(3));
	}

	@Test
	public void testPropagMaxTern3() {
		IntegerVariable y = makeIntVar("y", 1, 5);
		IntegerVariable z = makeIntVar("z", 1, 5);
		IntegerVariable max = makeIntVar("max", 1, 5);
		m.addConstraint(max(z, y, max));
		s.read(m);
		try {
			s.getVar(max).remVal(3);
			s.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		LOGGER.info(format("max {0}", s.getVar(max).getDomain().pretty()));
		LOGGER.info(format("y {0}", s.getVar(y).getDomain().pretty()));
		LOGGER.info(format("z {0}", s.getVar(z).getDomain().pretty()));
		LOGGER.info(format("{0}", s.getVar(y).canBeInstantiatedTo(3) && s.getVar(z).canBeInstantiatedTo(3)));
		assertTrue(s.getVar(y).canBeInstantiatedTo(3) && s.getVar(z).canBeInstantiatedTo(3));
	}

	@Test
	public void testPropagMaxTern4() {
		IntegerVariable y = makeIntVar("y", 1, 3);
		IntegerVariable z = makeIntVar("z", 4, 6);
		IntegerVariable max = makeIntVar("max", 1, 6);
		m.addConstraint(max(z, y, max));
		s.read(m);
		try {
			s.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		LOGGER.info(format("max {0}", s.getVar(max).getDomain().pretty()));
		LOGGER.info(format("y {0}", s.getVar(y).getDomain().pretty()));
		LOGGER.info(format("z {0}", s.getVar(z).getDomain().pretty()));
		LOGGER.info(format("{0}", s.getVar(max).getDomain().getSize() == 3));
		assertTrue(s.getVar(max).getDomain().getSize() == 3);
	}

	@Test
	public void testPropagMaxTern5() {
		IntegerVariable y = makeIntVar("y", 1, 4);
		IntegerVariable z = makeIntVar("z", 4, 8);
		IntegerVariable max = makeIntVar("max", 1, 8);
		m.addConstraint(max(z, y, max));
		s.read(m);
		try {
			s.getVar(z).remVal(5);
			s.getVar(max).remVal(8);
			s.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		LOGGER.info(format("max {0}", s.getVar(max).getDomain().pretty()));
		LOGGER.info(format("y {0}", s.getVar(y).getDomain().pretty()));
		LOGGER.info(format("z {0}", s.getVar(z).getDomain().pretty()));
		LOGGER.info(format("{0}", s.getVar(max).getDomain().getSize() == 3));
		assertEquals(s.getVar(max).getDomain().getSize(),3);
		assertEquals(s.getVar(z).getDomainSize(),3);
	}

	@Test
	public void testRandom() {
		for (int i = 0; i < 10; i++) {

			m = new CPModel();
			s= new CPSolver();


			IntegerVariable varA = makeIntVar("varA", 0, 3);
			IntegerVariable varB = makeIntVar("varB", 0, 3);
			IntegerVariable varC = makeIntVar("varC", 0, 3);
			m.addConstraint(max(varA, varB, varC));

			//-----Now get solutions

			s.read(m);
			s.setFirstSolution(true);
			s.generateSearchStrategy();
			s.setValIntSelector(new RandomIntValSelector(100 + i));
			s.setVarIntSelector(new RandomIntVarSelector(s, i));


			//LOGGER.info("Choco Solutions");
			int nbSolution = 0;
			if (s.solve() == Boolean.TRUE) {
				do {
					//LOGGER.info("Max(" + ((IntegerVariable) chocoCSP.getIntVar(0)).getVal() + ", " + ((IntegerVariable) chocoCSP.getIntVar(1)).getVal() + ") = " + ((IntegerVariable) chocoCSP.getIntVar(2)).getVal());
					nbSolution++;
				} while (s.nextSolution() == Boolean.TRUE);
			}

			assertEquals(nbSolution, 16);
		}
	}

	@Test
	public void testConstant(){
		for (int i = 0; i < 10; i++) {

			m = new CPModel();
			s= new CPSolver();


			IntegerVariable x = makeIntVar("x", 0, 3);
			IntegerVariable y = makeIntVar("y", 0, 3);
			m.addConstraint(eq(y, max(x, 1)));

			//-----Now get solutions

			s.read(m);
			s.setFirstSolution(true);
			s.generateSearchStrategy();
			s.setValIntSelector(new RandomIntValSelector(100 + i));
			s.setVarIntSelector(new RandomIntVarSelector(s, i));


			//LOGGER.info("Choco Solutions");
			int nbSolution = 0;
			if (s.solve() == Boolean.TRUE) {
				do {
					//LOGGER.info("Max(" + ((IntegerVariable) chocoCSP.getIntVar(0)).getVal() + ", " + ((IntegerVariable) chocoCSP.getIntVar(1)).getVal() + ") = " + ((IntegerVariable) chocoCSP.getIntVar(2)).getVal());
					nbSolution++;
				} while (s.nextSolution() == Boolean.TRUE);
			}

			assertEquals(nbSolution, 4);
		}
	}


	public final static int NB_ITERATION=1;

	protected static void testAll(boolean minOrMax, boolean bounded) {
		testAll(minOrMax,NB_ITERATION,3,2,bounded);
		testAll(minOrMax,NB_ITERATION,5,3,bounded);
		testAll(minOrMax,NB_ITERATION,4,6,bounded);
	}

	protected static void testAll(boolean minOrMax,int nbIter, int nbVars,int domSize, boolean bounded) {
		LOGGER.info("%%%%%%% TEST MIN/MAX %%%%%%%%%%%%%%");
		CPModel m = new CPModel();
		SetVariable set  = makeSetVar("set", 0, nbVars-1);
		IntegerVariable[] vars = makeIntVarArray("v",nbVars,1, domSize);
		if(bounded) {m.addVariables("cp:bound", vars);}
		IntegerVariable w = makeIntVar("bound", 1, domSize);
        IntegerVariable c  = makeIntVar("card", 0, nbVars+1);
        Constraint ccard = eq(c, 0);
        m.addConstraint(ccard);
        m.addConstraint( minOrMax ?
				min(set,vars,w) :
					max(set,vars,w)
		);

		m.addConstraint(leqCard(set,c));
		int sum=0;
		for (int k = 0; k < nbVars+1; k++) {
			m.remove(ccard);
            ccard = eq(c, k);
            m.addConstraint(ccard);
			int nbSets = MathUtil.combinaison(nbVars, k);
			int nbAssign = (int) Math.pow(domSize, k==0 ? nbVars+1 : nbVars);
			sum += nbSets*nbAssign;
			LOGGER.info("NB solutions : "+sum);
			for (int i = 0; i < nbIter; i++) {
				CPSolver s = new CPSolver();
				s.read(m);
				s.setRandomSelectors();
				//CPSolver.setVerbosity(CPSolver.SEARCH);
				//s.setLoggingMaxDepth(4);
				s.solveAll();
				assertEquals("set of cardinality <= "+k,sum, s.getNbSolutions());

			}
		}
	}

	@Test(expected=SolverException.class)
	public void badSetArg() {
		m = new CPModel();
		SetVariable set  = makeSetVar("set", 0, 4);
		IntegerVariable x = makeIntVar("x", 1, 2);
		IntegerVariable y = makeIntVar("y", 1, 2);
		IntegerVariable z = makeIntVar("z", 1, 2);
		IntegerVariable w = makeIntVar("bound", 1, 2);
		m.addConstraint(min(set,new IntegerVariable[]{x, y, z},w));
		s.read(m);
	}




	@Test
	public void testSet1() {
		testAll(false,true);
	}

	@Test
	public void testSet2() {
		testAll(false,false);
	}

         @Test
    public void testOneVarMax() {
        IntegerVariable[] vars = makeIntVarArray("vars", 1, 3, 5);
        IntegerVariable max = makeIntVar("min", 1, 6);
        m.addConstraint(eq(max, max(vars)));
        s.read(m);
        try {
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        LOGGER.info("max " + s.getVar(max).getDomain().pretty());
        assertTrue(s.getVar(max).getDomain().getSize() == 3);
    }
}
