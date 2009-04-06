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
package choco.model.constraints.reified;

import static choco.Choco.*;
import static choco.Choco.ifThenElse;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.reified.ExpressionSConstraint;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.extension.LargeRelation;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.Choco;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 30 juin 2004
 */
public class ReifiedSearchTest {

	Model m;
	Solver s;

	@Before
	public void b() {
		m = new CPModel();
		s = new CPSolver();
	}

	@After
	public void a() {
		m = null;
		s = null;
	}

	@Test
	public void testMultBoundVar() {
		int nbexpectedsol = 44;
		for (int seed = 0; seed < 10; seed++) {
			m = new CPModel();
			s = new CPSolver();
			m.setDefaultExpressionDecomposition(true);
			IntegerVariable x = makeIntVar("x", 1, 10);
			IntegerVariable y = makeIntVar("y", 1, 10);
			IntegerVariable z = makeIntVar("z", 1, 10);
            m.addVariables("cp:bound", x, y, z);

            m.addConstraint(or(eq(mult((x), (y)), (z)),
											  eq(mult((z), (y)), (x))));
			s.read(m);
			System.out.println(s.pretty());

			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));

			s.solveAll();
			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();

			System.out.println("" + s.getNbSolutions());

			assertEquals(nbexpectedsol, s.getNbSolutions());

		}
	}

	@Test
    @Ignore
    public void testAbsBoundVar() {
		int nbexpectedsol = 289;
		for (int seed = 0; seed < 10; seed++) {
			m = new CPModel();
			s = new CPSolver();
			m.setDefaultExpressionDecomposition(true);
			IntegerVariable x = makeIntVar("x", -10, 10);
			IntegerVariable y = makeIntVar("y", -10, 10);
			IntegerVariable z = makeIntVar("z", -10, 10);
            m.addVariables("cp:bound",x ,y, z);

            m.addConstraint(or(eq(mult(abs(minus((x),(3))), (y)), (z)),
											  eq(mult((z), (y)), abs((x)))));
			s.read(m);
			System.out.println(s.pretty());

			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));

			s.solveAll();
			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();

			System.out.println("" + s.getNbSolutions());

			assertEquals(nbexpectedsol, s.getNbSolutions());

		}
	}



	@Test
	public void testOrBoundVar() {
		IntegerVariable x = makeIntVar("x", 1, 3);
		IntegerVariable y = makeIntVar("y", 1, 3);
		IntegerVariable z = makeIntVar("z", 1, 3);
        m.addVariables("cp:bound",x ,y, z);

        m.addConstraint(or(lt(x, y), lt(y, x)));
		m.addConstraint(or(lt(y, z), lt(z, y)));
		s.read(m);
		System.out.println(s.pretty());
		s.solveAll();

		assertEquals(12, s.getNbSolutions());
	}


	@Test
	public void testOrBoundVarDecomp() {
		m.setDefaultExpressionDecomposition(false);
		IntegerVariable x = makeIntVar("x", 1, 3);
		IntegerVariable y = makeIntVar("y", 1, 3);
		IntegerVariable z = makeIntVar("z", 1, 3);
        m.addVariables("cp:bound",x ,y, z);
        Constraint e1 = or(lt((x), (y)), lt((y), (x)));
        //e1.setDecomposeExp(true);
		Constraint e2 = or(lt((y), (z)), lt((z), (y)));
        //e2.setDecomposeExp(true);

		m.addConstraint(e1);
		m.addConstraint(e2);

		s.read(m);
		System.out.println(s.pretty());
		s.solveAll();
		System.out.println("" + s.getNbSolutions());
		assertEquals(12, s.getNbSolutions());
	}


	@Test
	public void testOrEnumVar() {

		IntegerVariable x = makeIntVar("x", 1, 3);
		IntegerVariable y = makeIntVar("y", 1, 3);
		IntegerVariable z = makeIntVar("z", 1, 3);

		m.addConstraint(or(lt((x), (y)), lt((y), (x))));
		m.addConstraint(or(lt((y), (z)), lt((z), (y))));
		s.read(m);
		System.out.println(s.pretty());
		s.solveAll();

		assertEquals(12, s.getNbSolutions());
	}

	@Test
	public void testLargeOrForGecode() {
		//public static void main(String[] args) {
		int nbexpectedsol = -1;
		for (int seed = 0; seed < 10; seed++) {

			CPModel m = new CPModel();
			CPSolver s = new CPSolver();

			IntegerVariable x1 = makeIntVar("x", 1, 10);
			IntegerVariable x2 = makeIntVar("y", 1, 10);
			IntegerVariable y1 = makeIntVar("x", 1, 10);
			IntegerVariable y2 = makeIntVar("y", 1, 10);

			int s1 = 3, s2 = 4;

			m.addConstraint(or(gt(minus((x1), (x2)), (s1)),
					gt(minus((x2), (x1)), (s2)),
					gt(minus((y1), (y2)), (s1)),
					gt(minus((y2), (y1)), (s2))));

			s.read(m);

			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));

			s.solveAll();
			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();

			System.out.println("" + s.getNbSolutions());

			assertEquals(nbexpectedsol, s.getNbSolutions());
		}
	}


	@Test
	public void testIfThenElse() {
		//public static void main(String[] args) {
		int nbexpectedsol = -1;
		for (int seed = 0; seed < 10; seed++) {
			CPModel m = new CPModel();
			CPSolver s = new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 3);
			IntegerVariable y = makeIntVar("y", 1, 3);
			IntegerVariable z = makeIntVar("z", 1, 3);

			m.addConstraint(ifThenElse(lt((x), (y)), gt((y), (z)), FALSE));
			s.read(m);

			System.out.println(s.pretty());

			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));

			s.solveAll();

			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();

			System.out.println("" + s.getNbSolutions());

			assertEquals(nbexpectedsol, s.getNbSolutions());
		}
	}

    @Test
	public void testIfThenElse2() {
		//public static void main(String[] args) {
		int nbexpectedsol = -1;
		for (int seed = 0; seed < 10; seed++) {
			CPModel m1 = new CPModel();
            m1.setDefaultExpressionDecomposition(false);
            m1.setDefaultExpressionDecomposition(true);
            CPModel m2 = new CPModel();
            CPSolver s1 = new CPSolver();
            CPSolver s2 = new CPSolver();
            IntegerVariable x = makeIntVar("x", 0, 1);
			IntegerVariable y = makeIntVar("y", 1, 3);
			IntegerVariable z = makeIntVar("z", 1, 3);

            Constraint c = ifThenElse(eq(x, 1), and(eq(y, 3), eq(z, 3)), and(eq(y, 1), eq(z, 2)));
            m1.addConstraint(c);
            m2.addConstraint(c);

            s1.read(m1);
			s2.read(m2);

//			System.out.println(s1.pretty());
//            System.out.println(s2.pretty());

            s1.setVarIntSelector(new RandomIntVarSelector(s1, seed));
            s1.setValIntSelector(new RandomIntValSelector(seed + 1));

            s2.setVarIntSelector(new RandomIntVarSelector(s2, seed));
			s2.setValIntSelector(new RandomIntValSelector(seed + 1));

			s1.solveAll();
            s2.solveAll();

            if (nbexpectedsol == -1){
				nbexpectedsol = s1.getNbSolutions();
                nbexpectedsol = s2.getNbSolutions();
            }

            assertEquals(nbexpectedsol, s1.getNbSolutions());
            assertEquals(nbexpectedsol, s2.getNbSolutions());
            assertEquals(s1.getNbSolutions(), s2.getNbSolutions());
        }
	}

    @Test
	public void testOrEnumVarDecomp() {

		IntegerVariable x = makeIntVar("x", 1, 3);
		IntegerVariable y = makeIntVar("y", 1, 3);
		IntegerVariable z = makeIntVar("z", 1, 3);

		Constraint e1 = or(lt((x), (y)), lt((y), (x)));
        //e1.setDecomposeExp(true);
		Constraint e2 = or(lt((y), (z)), lt((z), (y)));
        //e2.setDecomposeExp(true);
		m.addConstraints(e1, e2);
		s.read(m);
		System.out.println(s.pretty());

		s.solveAll();

		assertEquals(12, s.getNbSolutions());
	}

	@Test
	public void testEquiv() {
		int nbexpectedsol = 11;
		for (int seed = 0; seed < 10; seed++) {
			m = new CPModel();
			s = new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 3);
			IntegerVariable y = makeIntVar("y", 1, 3);
			IntegerVariable z = makeIntVar("z", 1, 3);
            m.addVariables("cp:bound",x ,y, z);

            m.addConstraint(ifOnlyIf(lt((x), (y)), lt((y), (z))));
			s.read(m);
			System.out.println(s.pretty());
			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));
			s.solveAll();
			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();
			assertEquals(nbexpectedsol, s.getNbSolutions());
		}
	}

	@Test
	public void testEquivDecomp() {

		int nbexpectedsol = 11;
		for (int seed = 0; seed < 10; seed++) {
			CPModel m = new CPModel();
			CPSolver s = new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 3);
			IntegerVariable y = makeIntVar("y", 1, 3);
			IntegerVariable z = makeIntVar("z", 1, 3);
            m.addVariables("cp:bound",x ,y, z);

            Constraint e1 = ifOnlyIf(lt((x), (y)), lt((y), (z)));
            //1.setDecomposeExp(true);

			m.addConstraint(e1);
			s.read(m);
			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));
			System.out.println(s.pretty());

			s.solveAll();
			System.out.println("NBSOLUTION: " + s.getNbSolutions());
			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();
			assertEquals(nbexpectedsol, s.getNbSolutions());
		}
	}

	@Test
	public void testImplies() {

		IntegerVariable x = makeIntVar("x", 1, 2);
		IntegerVariable y = makeIntVar("y", 1, 2);
		IntegerVariable z = makeIntVar("z", 1, 2);
        m.addVariables("cp:bound",x ,y, z);

        m.addConstraint(implies(leq((x), (y)), leq((x), (z))));
		s.read(m);
		System.out.println(s.pretty());
		s.solveAll();

		assertEquals(7, s.getNbSolutions());
	}

	@Test
	public void testImpliesDecomp() {

		IntegerVariable x = makeIntVar("x", 1, 2);
		IntegerVariable y = makeIntVar("y", 1, 2);
		IntegerVariable z = makeIntVar("z", 1, 2);
        m.addVariables("cp:bound",x ,y, z);

        Constraint e1 = implies(leq((x), (y)), leq((x), (z)));
        //e1.setDecomposeExp(true);

		m.addConstraint(e1);
		s.read(m);
		System.out.println(s.pretty());

		s.solveAll();

		assertEquals(7, s.getNbSolutions());
	}

	@Test
	public void testAnd() {

		IntegerVariable x = makeIntVar("x", 1, 3);
		IntegerVariable y = makeIntVar("y", 1, 3);
		IntegerVariable z = makeIntVar("z", 1, 3);
        m.addVariables("cp:bound",x ,y, z);

        m.addConstraint(implies(lt((x), (2)), and(lt((x), (y)), lt((y), (z)))));
		s.read(m);
		System.out.println(s.pretty());
		s.solveAll();
	}

	@Test
	public void testAndDecomp() {

		IntegerVariable x = makeIntVar("x", 1, 3);
		IntegerVariable y = makeIntVar("y", 1, 3);
		IntegerVariable z = makeIntVar("z", 1, 3);
        m.addVariables("cp:bound",x ,y, z);

        Constraint e1 = implies(lt((x), (2)), and(lt((x), (y)), lt((y), (z))));
        //e1.setDecomposeExp(true);

		m.addConstraint(e1);
		s.read(m);
		System.out.println(s.pretty());

		s.solveAll();
	}

	@Test
	public void testLargeOr() {

		IntegerVariable x = makeIntVar("x", 1, 2);
		IntegerVariable y = makeIntVar("y", 1, 2);
		IntegerVariable z = makeIntVar("z", 1, 2);
        m.addVariables("cp:bound",x ,y, z);

        m.addConstraint(or(lt((x), (y)), lt((x), (z)), lt((y), (z))));
		s.read(m);
		System.out.println(s.pretty());
		s.solve();
		do {
			System.out.println("x = " + s.getVar(x).getVal());
			System.out.println("y = " + s.getVar(y).getVal());
			System.out.println("z = " + s.getVar(z).getVal());

		} while (s.nextSolution());

		assertEquals(4, s.getNbSolutions());
	}

	@Test
	public void testLargeOrDecomp() {

		IntegerVariable x = makeIntVar("x", 1, 2);
		IntegerVariable y = makeIntVar("y", 1, 2);
		IntegerVariable z = makeIntVar("z", 1, 2);
        m.addVariables("cp:bound",x ,y, z);

        Constraint e1 = or(lt((x), (y)), lt((x), (z)), lt((y), (z)));
        //e1.setDecomposeExp(true);
		m.addConstraint(e1);
		s.read(m);
		System.out.println(s.pretty());

		s.solve();
		do {
			System.out.println("x = " + s.getVar(x).getVal());
			System.out.println("y = " + s.getVar(y).getVal());
			System.out.println("z = " + s.getVar(z).getVal());

		} while (s.nextSolution());

		assertEquals(4, s.getNbSolutions());
	}


	@Test
	public void testSylvainBug1() {
		CPModel m = new CPModel();
		CPSolver s = new CPSolver();
		IntegerVariable[][] v = makeIntVarArray("v", 3, 2, 0, 1);
		Constraint[] tab = {(eq(sum(v[0]), 2)),
				(eq(sum(v[1]), 2)),
				(eq(sum(v[2]), 2))};
		Constraint c = or(tab);
		m.addConstraint(c);
		s.read(m);
		System.out.println(s.pretty());
		if (s.solve()) {
			System.out.print("v[0] = [" + s.getVar(v[0][0]).getVal() + ", " + s.getVar(v[0][1]).getVal() + "] / ");
			System.out.print("v[1] = [" + s.getVar(v[1][0]).getVal() + ", " + s.getVar(v[1][1]).getVal() + "] / ");
			System.out.println("v[2] = [" + s.getVar(v[2][0]).getVal() + ", " + s.getVar(v[2][1]).getVal() + "] - > c");
			//System.out.println(s.getCstr(c).isSatisfied() ? " satisfied." : " not satisfied!!!");

		}

		while (s.nextSolution()) {
			System.out.print("v[0] = [" + s.getVar(v[0][0]).getVal() + ", " + s.getVar(v[0][1]).getVal() + "] / ");
			System.out.print("v[1] = [" + s.getVar(v[1][0]).getVal() + ", " + s.getVar(v[1][1]).getVal() + "] / ");
			System.out.println("v[2] = [" + s.getVar(v[2][0]).getVal() + ", " + s.getVar(v[2][1]).getVal() + "] - > c");
			//System.out.println(s.getCstr(c).isSatisfied() ? " satisfied." : "not satisfied!!!");
		}
		assertEquals(37, s.getNbSolutions());

	}

	@Test
	public void testSylvainBug2() {
		CPModel m = new CPModel();
		CPSolver s = new CPSolver();
		IntegerVariable[][] v = makeIntVarArray("v", 2, 2, 0, 1);
		Constraint[] tab = {(eq(sum(v[0]), 2)),
				(eq(sum(v[1]), 2))};
		Constraint c = or(tab[0], tab[1]);
		m.addConstraint(c);
		s.read(m);
		System.out.println(s.pretty());
		//System.out.println(" " + s.getCstr(c).isSatisfied());
		if (s.solve()) {
			System.out.print("v[0] = [" + s.getVar(v[0][0]).getVal() + ", " + s.getVar(v[0][1]).getVal() + "] / ");
			System.out.println("v[1] = [" + s.getVar(v[1][0]).getVal() + ", " + s.getVar(v[1][1]).getVal() + "] / ");
			//System.out.println(s.getCstr(c).isSatisfied() ? " satisfied." : " not satisfied!!!");

		}

		while (s.nextSolution()) {
			System.out.print("v[0] = [" + s.getVar(v[0][0]).getVal() + ", " + s.getVar(v[0][1]).getVal() + "] / ");
			System.out.println("v[1] = [" + s.getVar(v[1][0]).getVal() + ", " + s.getVar(v[1][1]).getVal() + "] / ");
			//System.out.println(s.getCstr(c).isSatisfied() ? " satisfied." : "not satisfied!!!");
		}
		assertEquals(7, s.getNbSolutions());

	}

	@Test
	public void testSylvainBug2Decomp() {
		CPModel m = new CPModel();
		CPSolver s = new CPSolver();
		IntegerVariable[][] v = makeIntVarArray("v", 2, 2, 0, 1);
		Constraint[] tab = {(eq(sum(v[0]), 2)),
				(eq(sum(v[1]), 2))};
		Constraint c = or(tab[0], tab[1]);
		m.addConstraint(c);
		m.setDefaultExpressionDecomposition(true);
		s.read(m);
		System.out.println(s.pretty());
		//System.out.println(" " + s.getCstr(c).isSatisfied());
		if (s.solve()) {
			System.out.print("v[0] = [" + s.getVar(v[0][0]).getVal() + ", " + s.getVar(v[0][1]).getVal() + "] / ");
			System.out.println("v[1] = [" + s.getVar(v[1][0]).getVal() + ", " + s.getVar(v[1][1]).getVal() + "] / ");
			//System.out.println(s.getCstr(c).isSatisfied() ? " satisfied." : " not satisfied!!!");

		}

		while (s.nextSolution()) {
			System.out.print("v[0] = [" + s.getVar(v[0][0]).getVal() + ", " + s.getVar(v[0][1]).getVal() + "] / ");
			System.out.println("v[1] = [" + s.getVar(v[1][0]).getVal() + ", " + s.getVar(v[1][1]).getVal() + "] / ");
			//System.out.println(s.getCstr(c).isSatisfied() ? " satisfied." : "not satisfied!!!");
		}
		assertEquals(7, s.getNbSolutions());

	}


	@Test
	public void testEoin1() {
		int nbexpectedsol = 1; //initialize to -1 if number of solutions to hard to compute by hand

		for (int seed = 0; seed < 10; seed++) {
			m = new CPModel();
			s = new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 2);
			IntegerVariable y = makeIntVar("y", 5, 5);
			IntegerVariable z = makeIntVar("z", 1, 2);
            m.addVariables("cp:bound",x ,y, z);

            m.addConstraint(and(
					and(leq((x), (y)), leq((x), (z))),
					not(eq((x), (z)))));

			s.read(m);
			System.out.println(s.pretty());
			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));

			s.solveAll();

			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();
			assertEquals(nbexpectedsol, s.getNbSolutions());
		}
	}

	@Test
	public void testEoin1Decomp() {
		int nbexpectedsol = 1; //initialize to -1 if number of solutions to hard to compute by hand

		for (int seed = 0; seed < 10; seed++) {
			m = new CPModel();
			s = new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 2);
			IntegerVariable y = makeIntVar("y", 5, 5);
			IntegerVariable z = makeIntVar("z", 1, 2);
            m.addVariables("cp:bound",x ,y, z);

            Constraint e1 = and(
					and(leq((x), (y)), leq((x), (z))),
					not(eq((x), (z))));

            //e1.setDecomposeExp(true);

			m.addConstraint(e1);

			s.read(m);
			System.out.println(s.pretty());

			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));

			s.solveAll();

			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();
			assertEquals(nbexpectedsol, s.getNbSolutions());
		}
	}

	@Test
	public void testEoin2() {
		int nbexpectedsol = 2; //initialize to -1 if number of solutions to hard to compute by hand

		for (int seed = 0; seed < 10; seed++) {
			m = new CPModel();
			s = new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 1);
			IntegerVariable y = makeIntVar("y", 1, 1);
			IntegerVariable z = makeIntVar("z", 1, 2);
            m.addVariables("cp:bound",x ,y, z);

            m.addConstraint(and(
					leq((y), (z)),
					implies(leq((x), (y)), leq((x), (z)))));
			s.read(m);
			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));
			System.out.println(s.pretty());
			s.solveAll();

			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();
			assertEquals(nbexpectedsol, s.getNbSolutions());
		}
	}

	@Test
	public void testEoin2Decomp() {
		int nbexpectedsol = 2; //initialize to -1 if number of solutions to hard to compute by hand

		for (int seed = 0; seed < 10; seed++) {
			m = new CPModel();
			s = new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 1);
			IntegerVariable y = makeIntVar("y", 1, 1);
			IntegerVariable z = makeIntVar("z", 1, 2);
            m.addVariables("cp:bound",x ,y, z);

            Constraint e1 = and(
					leq((y), (z)),
					implies(leq((x), (y)), leq((x), (z))));
            //e1.setDecomposeExp(true);

			m.addConstraint(e1);
			s.read(m);
			System.out.println(s.pretty());
			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));

			s.solveAll();

			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();
			assertEquals(nbexpectedsol, s.getNbSolutions());
		}
	}

	//================================================
	@Test
	public void testEoin3() {
		int nbexpectedsol = 2; //initialize to -1 if number of solutions to hard to compute by hand

		for (int seed = 0; seed < 10; seed++) {
			m = new CPModel();
			s = new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 3);
			IntegerVariable y = makeIntVar("y", 1, 3);
			IntegerVariable z = makeIntVar("z", 1, 3);
            m.addVariables("cp:bound",x ,y, z);

            IntegerVariable[] vars = new IntegerVariable[]{x, y, z};

			// negating an iff turns it into an XOR gate

			m.addConstraint(and(
					not(
							ifOnlyIf(
									and(
											leq((x), (y)),
											leq((y), (z))
									),
									and(
											leq((z), (y)),
											leq((y), (x))
									)
							)),
					(allDifferent(vars))
			));
			s.read(m);
			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));
			System.out.println(s.pretty());
			s.solveAll();

			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();
			assertEquals(nbexpectedsol, s.getNbSolutions());
		}
	}

	@Test
	public void testEoin4() {
		int nbexpectedsol = 0; //initialize to -1 if number of solutions to hard to compute by hand

		for (int seed = 0; seed < 10; seed++) {
			m = new CPModel();
			s = new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 3);
			IntegerVariable y = makeIntVar("y", 1, 3);
			IntegerVariable z = makeIntVar("z", 1, 3);
            m.addVariables("cp:bound",x ,y, z);

            m.addConstraint(and(
					and(
							not(eq((x), (y))),
							not(eq((x), (z)))),
					and(
							not(eq((y), (z))),
							not(not(eq((x), (z)))))
			));
			s.read(m);
			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));
			System.out.println(s.pretty());
			s.solveAll();

			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();
			assertEquals(nbexpectedsol, s.getNbSolutions());
		}
	}

	@Test
	public void testEoin4Decomp() {
		int nbexpectedsol = 0; //initialize to -1 if number of solutions to hard to compute by hand

		for (int seed = 0; seed < 10; seed++) {
			m = new CPModel();
			s = new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 3);
			IntegerVariable y = makeIntVar("y", 1, 3);
			IntegerVariable z = makeIntVar("z", 1, 3);
            m.addVariables("cp:bound",x ,y, z);

            Constraint e1 = and(
					and(
							not(eq((x), (y))),
							not(eq((x), (z)))),
					and(
							not(eq((y), (z))),
							not(not(eq((x), (z))))
			));

            //e1.setDecomposeExp(true);
			m.addConstraint(e1);
			s.read(m);
			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));
			System.out.println(s.pretty());
			s.solveAll();

			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();
			assertEquals(nbexpectedsol, s.getNbSolutions());
		}
	}

	@Test
	public void testEoin5() {
		int nbexpectedsol = 2; //initialize to -1 if number of solutions to hard to compute by hand

		for (int seed = 0; seed < 10; seed++) {
			m = new CPModel();
			s = new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 3);
			IntegerVariable y = makeIntVar("y", 1, 3);
			IntegerVariable z = makeIntVar("z", 1, 3);
            m.addVariables("cp:bound",x ,y, z);

            IntegerVariable[] vars = new IntegerVariable[]{x, y, z};

			// negating an iff turns it into an XOR gate

			m.addConstraint(and(
					not(
							ifOnlyIf(
									and(
											leq((x), (y)),
											leq((y), (z))
									),
									and(
											leq((z), (y)),
											leq((y), (x))
									)
							)),
					(allDifferent(vars))
			));
			s.read(m);
			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));
			System.out.println(s.pretty());
			s.solveAll();

			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();
			assertEquals(nbexpectedsol, s.getNbSolutions());
		}
	}

	@Test
	public void testDeepak() {
		int nbexpectedsol = -1; //initialize to -1 if number of solutions to hard to compute by hand

		for (int seed = 0; seed < 10; seed++) {
			System.out.println("seed " + seed);
			m = new CPModel();
			s = new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 3);
			IntegerVariable y = makeIntVar("y", 1, 3);
			IntegerVariable z = makeIntVar("z", 0, 1);
			IntegerVariable w = makeIntVar("w", 0, 1);
            m.addVariables("cp:bound",x ,y, z);

            Constraint c = implies(and(eq((z), (1)), eq((w), (1))), leq((x), (y)));
			m.addConstraint(c);
			s.read(m);
			ExpressionSConstraint p = (ExpressionSConstraint) s.getCstr(c);
			p.setScope(s);
			IntDomainVar[] vs = p.getVars();
			int[] max = new int[4];
			for (int i = 0; i < vs.length; i++) {
				max[i] = vs[i].getSup() + i;
			}
			LargeRelation lrela = s.makeLargeRelation(new int[4], max, p.getTuples(s), true);
			//relationTupleAC(new IntegerVariable[]{x, y}, lrela);
			s.post(s.relationTupleAC(p.getVars(), lrela));
			System.out.println(s.pretty());
			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));

			//s.solve();
			//System.out.println(z + " " + w + " " + y + " " + x);
			//System.out.println(isFeasible() + " " + c.pretty());
			s.solveAll();
			System.out.println("" + s.getNbSolutions());
			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();
			assertEquals(nbexpectedsol, s.getNbSolutions());
		}

	}

	@Test
	public void testDeepakDecomp() {
		int nbexpectedsol = -1; //initialize to -1 if number of solutions to hard to compute by hand

		for (int seed = 0; seed < 10; seed++) {
			System.out.println("seed " + seed);
			m = new CPModel();
			s = new CPSolver();
			IntegerVariable x = makeIntVar("x", 1, 3);
			IntegerVariable y = makeIntVar("y", 1, 3);
			IntegerVariable z = makeIntVar("z", 0, 1);
			IntegerVariable w = makeIntVar("w", 0, 1);
            m.addVariables("cp:bound", w, x ,y, z);

            Constraint e1 = implies(and(eq((z), (1)), eq((w), (1))), leq((x), (y)));
            //e1.setDecomposeExp(true);
			m.addConstraint(e1);
			s.read(m);
			System.out.println(s.pretty());
			ExpressionSConstraint p = (ExpressionSConstraint) s.getCstr(e1);
			p.setScope(s);
			IntDomainVar[] vs = p.getVars();
			int[] max = new int[4];
			for (int i = 0; i < vs.length; i++) {
				max[i] = vs[i].getSup() + i;
			}
			LargeRelation lrela = s.makeLargeRelation(new int[4], max, p.getTuples(s), true);
			relationTupleAC(new IntegerVariable[]{x, y}, lrela);
			s.post(s.relationTupleAC(p.getVars(), lrela));

			s.setVarIntSelector(new RandomIntVarSelector(s, seed));
			s.setValIntSelector(new RandomIntValSelector(seed + 1));

			//s.solve();
			//System.out.println(z + " " + w + " " + y + " " + x);
			//System.out.println(isFeasible() + " " + c.pretty());
			s.solveAll();
			System.out.println("" + s.getNbSolutions());
			if (nbexpectedsol == -1)
				nbexpectedsol = s.getNbSolutions();
			assertEquals(nbexpectedsol, s.getNbSolutions());
		}

	}

	@Test
	public void testExp1() {
		CPModel m = new CPModel();
		IntegerVariable z = makeIntVar("z", -10, 10);
		IntegerVariable w = makeIntVar("w", -10, 10);
		m.addVariables(z, w);

		CPSolver s = new CPSolver();
		// z = 10 * |w| OU z >= 9
		Constraint exp =
				or(
						eq((z), mult((10), abs((w)))),
						geq((z), (9))
				);

		m.addConstraint(exp);

		s.read(m);
		System.out.println(s.pretty());
		try {
			s.propagate();
			System.out.println(s.getVar(z).pretty() + " " + s.getVar(w).pretty());
		} catch (
				ContradictionException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		s.solve();

		System.out.println("" + s.isFeasible());
		System.out.println(s.getVar(z).getVal() + " " + s.getVar(w).getVal());
	}

	@Test
	public void testExp1Decomp() {
		CPModel m = new CPModel();
		IntegerVariable z = makeIntVar("z", -10, 10);
		IntegerVariable w = makeIntVar("w", -10, 10);
		m.addVariables(z, w);

		CPSolver s = new CPSolver();
		// z = 10 * |w| OU z >= 9
		Constraint exp =
				or(
						eq((z), mult((10), abs((w)))),
						geq((z), (9))
				);

		m.setDefaultExpressionDecomposition(true);
		m.addConstraint(exp);

		s.read(m);
		System.out.println(s.pretty());
		try {
			s.propagate();
			System.out.println(s.getVar(z).pretty() + " " + s.getVar(w).pretty());
		} catch (
				ContradictionException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		s.solve();

		System.out.println("" + s.isFeasible());
		System.out.println(s.getVar(z).getVal() + " " + s.getVar(w).getVal());
	}

    @Test
	public void testNotReifiedExpr() {
		CPModel m = new CPModel();
        IntegerVariable x = makeIntVar("x", -10, 10);
        IntegerVariable z = makeIntVar("z", -10, 10);
		IntegerVariable w = makeIntVar("w", -10, 10);
		m.addVariables(x,z, w);

		CPSolver s = new CPSolver();
		// z = 10 * |w| OU z >= 9
		Constraint exp = geq(x, mult(z,w));

		m.setDefaultExpressionDecomposition(true);
		m.addConstraint(exp);

		s.read(m);
		s.solveAll();

		assertEquals(s.getNbSolutions(),4705);

	}

        @Test
     public void testIfThenElse3() {
            for (int seed = 0; seed < 100; seed++) {
                CPModel m = new CPModel();
                m.setDefaultExpressionDecomposition(true);
                IntegerVariable x = makeIntVar("x", 0, 2, "cp:bound");
                IntegerVariable y = makeIntVar("y", 0, 2, "cp:bound");
                m.addConstraint(ifThenElse(gt(x, 0), eq(y, 0), Choco.TRUE));
                CPSolver s = new CPSolver();
                s.read(m);
                s.setVarIntSelector(new RandomIntVarSelector(s, seed));
                s.setValIntSelector(new RandomIntValSelector(seed + 1));
                s.solveAll();
                System.out.println("" + s.getNbSolutions());
                assertEquals(s.getNbSolutions(), 5);

            }
        }
}