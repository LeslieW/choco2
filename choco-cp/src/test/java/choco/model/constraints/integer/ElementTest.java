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
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Narendra Jussien
 * Date: 18 mai 2005
 * Time: 16:29:42
 */
public class ElementTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	CPModel m;
	CPSolver s;

	@Before
	public void before() {
		m = new CPModel();
		s = new CPSolver();
	}

	@After
	public void after() {
		m = null;
		s = null;
	}


	@Test
	public void test1() {
		int[] values = new int[]{1, 2, 0, 4, 3};
		IntegerVariable index = makeIntVar("index", -3, 10);
		IntegerVariable var = makeIntVar("value", -20, 20);
		m.addConstraint(nth(index, values, var));
		s.read(m);
		s.solve();
		do {
			LOGGER.info("index = " + s.getVar(index).getVal());
			LOGGER.info("value = " + s.getVar(var).getVal());
			assertEquals(s.getVar(var).getVal(), values[s.getVar(index).getVal()]);
		} while (s.nextSolution());

		assertEquals(5, s.getNbSolutions());
	}

	@Test
	public void test2() {

		int[] values = new int[]{1, 2, 0, 4, 3};
		IntegerVariable index = makeIntVar("index", 2, 10);
		IntegerVariable var = makeIntVar("value", -20, 20);
		m.addConstraint(nth(index, values, var));
		s.read(m);
		s.solve();
		do {
			LOGGER.info("index = " + s.getVar(index).getVal());
			LOGGER.info("value = " + s.getVar(var).getVal());
			assertEquals(s.getVar(var).getVal(), values[s.getVar(index).getVal()]);
		} while (s.nextSolution());

		assertEquals(3, s.getNbSolutions());
	}

	@Test
	public void test3() {

		IntegerVariable X = makeIntVar("X", 0, 5);
		IntegerVariable Y = makeIntVar("Y", 3, 7);
		IntegerVariable Z = makeIntVar("Z", 5, 8);
		IntegerVariable I = makeIntVar("index", -5, 12);
		IntegerVariable V = makeIntVar("V", -3, 20);
		m.addConstraint(nth(I, new IntegerVariable[]{X, Y, Z}, V));
		s.read(m);
		try {
			s.propagate();
			assertEquals(s.getVar(I).getInf(), 0);
			assertEquals(s.getVar(I).getSup(), 2);
			assertEquals(s.getVar(V).getInf(), 0);
			assertEquals(s.getVar(V).getSup(), 8);
			s.getVar(V).setSup(5);
			s.getVar(Z).setInf(6);
			s.propagate();
			assertEquals(s.getVar(I).getSup(), 1);
			s.getVar(Y).remVal(4);
			s.getVar(Y).remVal(5);
			s.getVar(V).remVal(3);
			s.propagate();
			assertTrue(s.getVar(I).isInstantiatedTo(0));
			s.getVar(V).setSup(2);
			s.getVar(V).remVal(1);
			s.propagate();
			assertEquals(s.getVar(X).getSup(), 2);
			assertFalse(s.getVar(X).canBeInstantiatedTo(1));
		} catch (ContradictionException e) {
			assertFalse(true);
		}
	}

	@Test
	public void test4() {

		IntegerVariable V = makeIntVar("V", 0, 20);
		IntegerVariable X = makeIntVar("X", 10, 50);
		IntegerVariable Y = makeIntVar("Y", 0, 1000);
		IntegerVariable I = makeIntVar("I", 0, 1);
		m.addConstraint(nth(I, new IntegerVariable[]{X, Y}, V));
		s.read(m);
		try {
			s.propagate();
			assertFalse(s.getVar(I).isInstantiated());
			s.getVar(Y).setInf(30);
			s.propagate();
			assertTrue(s.getVar(I).isInstantiatedTo(0));
			assertEquals(s.getVar(V).getInf(), s.getVar(X).getInf());
			assertEquals(s.getVar(V).getSup(), s.getVar(X).getSup());
		} catch (ContradictionException e) {
			assertFalse(true);
		}
	}

	/**
	 * testing the initial propagation and the behavior when the index variable is instantiated
	 */
	@Test
	public void test5() {

		int n = 2;
		IntegerVariable[] vars = new IntegerVariable[n];
		for (int idx = 0; idx < n; idx++) {
			vars[idx] = makeIntVar("t" + idx, 3 * idx, 2 + 3 * idx);
		}
		IntegerVariable index = makeIntVar("index", -3, 15);
		IntegerVariable var = makeIntVar("value", -25, 20);

		m.addConstraint(nth(index, vars, var));
		s.read(m);
		try {
			s.propagate();
		} catch (ContradictionException e) {
			assertFalse(true);
		}
		assertEquals(0, s.getVar(var).getInf());
		assertEquals(3 * n - 1, s.getVar(var).getSup());
		assertEquals(s.getVar(var).getDomainSize(), 3 * n);
		assertEquals(0, s.getVar(index).getInf());
		assertEquals(n - 1, s.getVar(index).getSup());
		assertEquals(s.getVar(index).getDomainSize(), n);

		assertEquals(0, s.getVar(vars[0]).getInf());
		assertEquals(2, s.getVar(vars[0]).getSup());
		assertEquals(s.getVar(vars[0]).getDomainSize(), 3);
		assertEquals(3, s.getVar(vars[1]).getInf());
		assertEquals(5, s.getVar(vars[1]).getSup());
		assertEquals(s.getVar(vars[1]).getDomainSize(), 3);

		try {
			s.getVar(index).setVal(1);
			s.propagate();
			assertEquals(3, s.getVar(var).getInf());
			assertEquals(5, s.getVar(var).getSup());
			assertEquals(0, s.getVar(vars[0]).getInf());
			assertEquals(2, s.getVar(vars[0]).getSup());
			assertEquals(3, s.getVar(vars[1]).getInf());
			assertEquals(5, s.getVar(vars[1]).getSup());
			s.getVar(vars[0]).setVal(0);
			s.propagate();
		} catch (ContradictionException e) {
			assertFalse(true);
		}
	}

	/**
	 * same as test5, but counting the number of solutions
	 */
	@Test
	public void test6() {
		subtest6(2);
		subtest6(3);
		subtest6(4);
	}

	private void subtest6(int n) {
		m = new CPModel();
		s = new CPSolver();		
		IntegerVariable[] vars = new IntegerVariable[n];
		for (int idx = 0; idx < n; idx++) {
			vars[idx] = makeIntVar("t" + idx, 3 * idx, 2 + 3 * idx);
		}
		IntegerVariable index = makeIntVar("index", -3, n + 15);
		IntegerVariable var = makeIntVar("value", -25, 4 * n + 20);

		m.addConstraint(nth(index, vars, var));
		s.read(m);
		try {
			s.propagate();
		} catch (ContradictionException e) {
			assertFalse(true);
		}
		assertEquals(0, s.getVar(var).getInf());
		assertEquals(3 * n - 1, s.getVar(var).getSup());
		assertEquals(s.getVar(var).getDomainSize(), 3 * n);
		assertEquals(0, s.getVar(index).getInf());
		assertEquals(n - 1, s.getVar(index).getSup());
		assertEquals(s.getVar(index).getDomainSize(), n);
		for (int i = 0; i < n; i++) {
			assertEquals(3 * i, s.getVar(vars[i]).getInf());
			assertEquals(2 + 3 * i, s.getVar(vars[i]).getSup());
			assertEquals(s.getVar(vars[i]).getDomainSize(), 3);
		}
		s.solveAll();
		assertEquals(Math.round(n * Math.pow(3, n)), s.getNbSolutions());

	}

	@Test
	public void testElement1() {
		for (int i = 0; i < 10; i++) {
			m = new CPModel();
			s = new CPSolver();			
			int[][] values = new int[][]{
					{1, 2, 0, 4, -323},
					{2, 1, 0, 3, 42},
					{6, 1, -7, 4, -40},
					{-1, 0, 6, 2, -33},
					{2, 3, 0, -1, 49}};
			IntegerVariable index1 = makeIntVar("index1", -3, 10);
			IntegerVariable index2 = makeIntVar("index2", -3, 10);
			IntegerVariable var = makeIntVar("value", -20, 20);
			Constraint c = nth(index1, index2, values, var);
			LOGGER.info("posted constraint = " + c.pretty());
			m.addConstraint(c);
			s.setVarIntSelector(new RandomIntVarSelector(s, i));
			s.setValIntSelector(new RandomIntValSelector(i + 1));
			s.read(m);
			s.solveAll();
			assertEquals(s.getNbSolutions(), 20);
		}
	}

	@Test
	public void testElement2() {
		for (int i = 0; i < 10; i++) {
			m = new CPModel();
			s = new CPSolver();			
			int[][] values = new int[][]{
					{1, 2, 0, 4, 3},
					{2, 1, 0, 3, 3},
					{6, 1, -7, 4, -4},
					{-1, 0, 6, 2, -33},
					{2, -3, 0, -1, 4}};
			IntegerVariable index1 = makeIntVar("index1", 2, 10);
			IntegerVariable index2 = makeIntVar("index2", -3, 2);
			IntegerVariable var = makeIntVar("value", -20, 20);
			m.addConstraint(nth(index1, index2, values, var));
			s.setVarIntSelector(new RandomIntVarSelector(s, i));
			s.setValIntSelector(new RandomIntValSelector(i + 1));
			s.read(m);
			s.solveAll();
			assertEquals(s.getNbSolutions(), 9);
		}
	}

	@Test
	public void testNthG() {
		for (int i = 0; i < 100; i++) {
			m = new CPModel();
			s = new CPSolver();			
			IntegerVariable X = makeIntVar("X", 0, 5);
			IntegerVariable Y = makeIntVar("Y", 3, 7);
			IntegerVariable Z = makeIntVar("Z", 5, 8);
			IntegerVariable I = makeIntVar("index", -5, 12);
			IntegerVariable V = makeIntVar("V", -3, 20);
			m.addConstraint(nth(I, new IntegerVariable[]{X, Y, Z}, V));
			s.setVarIntSelector(new RandomIntVarSelector(s, i));
			s.setValIntSelector(new RandomIntValSelector(i + 1));
			s.read(m);
			s.solveAll();
			int nbSol = s.getNbSolutions();
			//LOGGER.info("nbsol " + nbSol);
			assertEquals(nbSol, 360);
		}

	}

	@Test
	public void testNthG2() {
		for (int i = 0; i < 30; i++) {
			m = new CPModel();
			s = new CPSolver();			
			IntegerVariable X = makeIntVar("X", 0, 5);
			IntegerVariable Z = makeIntVar("Z", 5, 8);
			IntegerVariable I = makeIntVar("index", -5, 12);
			IntegerVariable V = makeIntVar("V", -3, 20);
			m.addConstraint(nth(I, new IntegerVariable[]{X, V, Z}, V));
			s.setVarIntSelector(new RandomIntVarSelector(s, i));
			s.setValIntSelector(new RandomIntValSelector(i + 1));
			s.read(m);
			s.solveAll();
			int nbSol = s.getNbSolutions();
			//LOGGER.info("nbsol " + nbSol);
			assertEquals(nbSol,624);
		}
	}

	@Test
	public void testNthG3() {
		for (int i = 0; i < 30; i++) {
			m = new CPModel();
			s = new CPSolver();			
			IntegerVariable X = makeIntVar("X", 0, 5);
			IntegerVariable Z = makeIntVar("Z", 5, 8);
			IntegerVariable I = makeIntVar("index", -5, 12);
			IntegerVariable V = makeIntVar("V", -3, 20);
			m.addConstraint(nth(I, new IntegerVariable[]{X, I, Z}, V));
			s.setVarIntSelector(new RandomIntVarSelector(s, i));
			s.setValIntSelector(new RandomIntValSelector(i + 1));
			s.read(m);
			s.solveAll();
			int nbSol = s.getNbSolutions();
			//LOGGER.info("nbsol " + nbSol);
			assertEquals(nbSol,72);
		}

	}
	
	@Test
    //BUG ID: 2860512
	public void testNthManager() {
		IntegerVariable I = makeIntVar("index", 0, 2);
		IntegerVariable V = makeIntVar("V", 0, 5);
		m.addConstraint(nth(I, new IntegerVariable[]{constant(0),constant(1), makeIntVar("VV",2,3)}, V));
		s.read(m);
		//ChocoLogging.setVerbosity(Verbosity.SOLUTION);
		s.solveAll();
		assertEquals(6, s.getSolutionCount());	
	}

    @Test
    //BUG ID: 2860512
    public void testNthManager2() {
        IntegerVariable I = makeIntVar("index", 0, 2);
        IntegerVariable V = makeIntVar("V", 0, 5);
        m.addConstraint(nth(I, new IntegerVariable[]{constant(0),constant(1), constant(2)}, V));
        s.read(m);
        //ChocoLogging.setVerbosity(Verbosity.SOLUTION);
        s.solveAll();
        assertEquals(3, s.getSolutionCount());
    }

    @Test
    //BUG ID: 2860512
	public void testNthManager3() {
		IntegerVariable I = makeIntVar("index", 0, 2);
		IntegerVariable V = makeIntVar("V", 0, 5);
		m.addConstraint(nth(I, new IntegerVariable[]{makeIntVar("VV",0,1), makeIntVar("VV",1,2), makeIntVar("VV",2,3)}, V));
		s.read(m);
		//ChocoLogging.setVerbosity(Verbosity.SOLUTION);
		s.solveAll();
		assertEquals(24, s.getSolutionCount());
	}

}
