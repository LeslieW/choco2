package i_want_to_use_this_old_version_of_choco.set;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.set.constraint.*;
import i_want_to_use_this_old_version_of_choco.set.search.*;
import junit.framework.TestCase;

import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class BasicConstraintsTests extends TestCase {
	private Logger logger = Logger.getLogger("choco.currentElement");
	private Problem pb;
	private SetVar x;
	private SetVar y;
	private SetVar z;
	private IntDomainVar iv;
	private Constraint c1;
	private Constraint c2;
	private Constraint c3;
	private Constraint c4;

	protected void setUp() {
		logger.fine("EqualXC Testing...");

	}

	protected void tearDown() {
		c1 = null;
		c2 = null;
		c3 = null;
		c4 = null;
		x = null;
		y = null;
		z = null;
		iv = null;
		pb = null;
	}

	/**
	 * Test MemberX - NotMemberX
	 */
	public void test1() {
		logger.finer("test1");
		pb = new Problem();
		x = pb.makeSetVar("X", 1, 5);
		y = pb.makeSetVar("Y", 1, 5);
		c1 = new MemberX(x, 3);
		c2 = new MemberX(x, 5);
		c3 = new NotMemberX(x, 2);
		try {
			pb.post(c1);
			pb.post(c2);
			pb.post(c3);
			pb.propagate();
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		assertTrue(x.isInDomainKernel(3));
		assertTrue(x.isInDomainKernel(5));
		assertTrue(!x.isInDomainKernel(2));
		System.out.println("[BasicConstraintTests,test1] x : " + x.pretty());
		logger.finest("domains OK after first propagate");
	}

	/**
	 * Test MemberXY
	 */
	public void test2() {
		logger.finer("test2");
		pb = new Problem();
		x = pb.makeSetVar("X", 1, 5);
		iv = pb.makeEnumIntVar("iv", 1, 5);
		c1 = new MemberX(x, 3);
		c2 = new MemberX(x, 5);
		c3 = new NotMemberX(x, 2);
		c4 = new MemberXY(x, iv);
		try {
			pb.post(c4);
			pb.post(c2);
			pb.post(c3);
			pb.post(c1);
			pb.propagate();
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		assertTrue(!iv.canBeInstantiatedTo(2));
		assertTrue(x.isInDomainKernel(3));
		assertTrue(x.isInDomainKernel(5));
		assertTrue(!x.isInDomainKernel(2));
		System.out.println("[BasicConstraintTests,test2] x : " + x.pretty());
		System.out.println("[BasicConstraintTests,test2] iv : " + iv.pretty());
		logger.finest("domains OK after first propagate");
		pb.getSolver().setFirstSolution(false);
		pb.getSolver().generateSearchSolver(pb);
		pb.getSolver().addGoal(new AssignSetVar(new MinDomSet(pb), new MinEnv(pb)));
		pb.getSolver().launch();

		assertEquals(12, pb.getSolver().getNbSolutions());
	}

	/**
	 * Test NotMemberXY
	 */
	public void test3() {
		logger.finer("test3");
		pb = new Problem();
		x = pb.makeSetVar("X", 1, 5);
		iv = pb.makeEnumIntVar("iv", 1, 5);
		c1 = new MemberX(x, 3);
		c2 = new MemberX(x, 5);
		c3 = new NotMemberX(x, 2);
		c4 = new NotMemberXY(x, iv);
		try {
			pb.post(c2);
			pb.post(c1);
			pb.post(c3);
			pb.post(c4);
			pb.propagate();
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		System.out.println("[BasicConstraintTests,test1] x : " + x.pretty());
		System.out.println("[BasicConstraintTests,test1] iv : " + iv.pretty());
		assertTrue(!iv.canBeInstantiatedTo(3));
		assertTrue(!iv.canBeInstantiatedTo(5));
		assertTrue(x.isInDomainKernel(3));
		assertTrue(x.isInDomainKernel(5));
		assertTrue(!x.isInDomainKernel(2));
		System.out.println("[BasicConstraintTests,test3] x : " + x.pretty());
		System.out.println("[BasicConstraintTests,test3] iv : " + iv.pretty());
		logger.finest("domains OK after first propagate");
		pb.getSolver().setFirstSolution(false);
		pb.getSolver().generateSearchSolver(pb);
		pb.getSolver().addGoal(new AssignSetVar(new MinDomSet(pb), new MinEnv(pb)));
		pb.getSolver().launch();

		assertEquals(8, pb.getSolver().getNbSolutions());
	}

	/**
	 * Test TestCardinality ==
	 */
	public void test4() {
		for (int i = 0; i < 20; i++) {
			logger.finer("test4");
			pb = new Problem();
			x = pb.makeSetVar("X", 1, 5);
			iv = pb.makeEnumIntVar("iv", 2, 3);
			c1 = new MemberX(x, 3);
			c2 = new SetCard(x, iv, true, true);   // on teste l'�galit�
			try {
				pb.post(c1);
				pb.post(c2);
				pb.propagate();
			} catch (ContradictionException e) {
				assertTrue(false);
			}
			pb.getSolver().setFirstSolution(false);
			pb.getSolver().generateSearchSolver(pb);
			pb.getSolver().addGoal(new AssignSetVar(new RandomSetVarSelector(pb, i), new RandomSetValSelector(i + 1)));
			//pb.getSolver().addGoal(new AssignSetVar(new MinDomSet(pb), new MinEnv(pb)));
			pb.getSolver().launch();
			System.out.println("Nb solution: " + pb.getSolver().getNbSolutions());

			assertEquals(10, pb.getSolver().getNbSolutions());
		}
	}

	/**
	 * Test TestCardinality <=
	 */
	public void test5() {
		for (int i = 0; i < 20; i++) {
			logger.finer("test5");
			pb = new Problem();
			x = pb.makeSetVar("X", 1, 3);
			iv = pb.makeEnumIntVar("iv", 2, 2);
			c1 = new MemberX(x, 3);
			c2 = new SetCard(x, iv, true, false);   // on teste <=
			try {
				pb.post(c1);
				pb.post(c2);
				pb.propagate();
			} catch (ContradictionException e) {
				assertTrue(false);
			}
			pb.getSolver().setFirstSolution(false);
			pb.getSolver().generateSearchSolver(pb);
			//pb.getSolver().addGoal(new AssignSetVar(new MinDomSet(pb), new MinEnv(pb)));
			pb.getSolver().addGoal(new AssignSetVar(new RandomSetVarSelector(pb, i), new RandomSetValSelector(i + 1)));
			pb.getSolver().launch();
			System.out.println("Nb solution: " + pb.getSolver().getNbSolutions());
			assertEquals(3, pb.getSolver().getNbSolutions());
		}
	}

	/**
	 * Test TestCardinality >=
	 */
	public void test6() {
		for (int i = 0; i < 20; i++) {

			logger.finer("test6");
			pb = new Problem();
			x = pb.makeSetVar("X", 1, 3);
			iv = pb.makeEnumIntVar("iv", 1, 2);
			c1 = new MemberX(x, 3);
			c2 = new SetCard(x, iv, false, true);   // on teste =>
			try {
				pb.post(c1);
				pb.post(c2);
				pb.propagate();
			} catch (ContradictionException e) {
				assertTrue(false);
			}
			pb.getSolver().setFirstSolution(false);
			pb.getSolver().generateSearchSolver(pb);
			//pb.getSolver().addGoal(new AssignSetVar(new MinDomSet(pb), new MinEnv(pb)));
			pb.getSolver().addGoal(new AssignSetVar(new RandomSetVarSelector(pb, i), new RandomSetValSelector(i + 1)));

			pb.getSolver().launch();
			System.out.println("Nb solution: " + pb.getSolver().getNbSolutions());
			assertEquals(7, pb.getSolver().getNbSolutions());
		}
	}

	/**
	 * Test TestDisjoint
	 * The number of disjoint pair of set taken in a set of initial size n is :
	 * sigma_{k = 0 -> k = n} (C_n^k * 2^(n - k))
	 */
	public void test7() {
		for (int i = 0; i < 20; i++) {
			logger.finer("test7");
			pb = new Problem();
			x = pb.makeSetVar("X", 1, 3);
			y = pb.makeSetVar("Y", 1, 3);
			c1 = new Disjoint(x, y);
			try {
				pb.post(c1);
				pb.propagate();
			} catch (ContradictionException e) {
				assertTrue(false);
			}
			pb.getSolver().setFirstSolution(false);
			pb.getSolver().generateSearchSolver(pb);
			//pb.getSolver().addGoal(new AssignSetVar(new MinDomSet(pb), new MinEnv(pb)));
			pb.getSolver().addGoal(new AssignSetVar(new RandomSetVarSelector(pb, i), new RandomSetValSelector(i + 1)));
			pb.getSolver().launch();
			System.out.println("nbSol " + pb.getSolver().getNbSolutions());
			assertEquals(27, pb.getSolver().getNbSolutions());
		}
	}

	/**
	 * Test Intersection
	 */
	public void test8() {
		for (int i = 0; i < 20; i++) {
			logger.finer("test8");
			pb = new Problem();
			x = pb.makeSetVar("X", 1, 3);
			y = pb.makeSetVar("Y", 1, 3);
			z = pb.makeSetVar("Z", 2, 3);
			c1 = new SetIntersection(x, y, z);
			//c2 = new NotMemberX(z,2);
			try {
				//pb.post(c2);
				pb.post(c1);
				pb.propagate();
			} catch (ContradictionException e) {
				assertTrue(false);
			}
			pb.getSolver().setFirstSolution(false);
			pb.getSolver().generateSearchSolver(pb);
			pb.getSolver().addGoal(new AssignSetVar(new RandomSetVarSelector(pb, i), new RandomSetValSelector(i + 1)));
			//pb.getSolver().addGoal(new AssignSetVar(new MinDomSet(pb), new MinEnv(pb)));
			pb.getSolver().launch();
			System.out.println("nbSol " + pb.getSolver().getNbSolutions());
			assertEquals(48, pb.getSolver().getNbSolutions());

		}
	}

	/**
	 * Test cardinality reasonnings
	 */
	public void test9() {
		logger.finer("test9");
		pb = new Problem();
		x = pb.makeSetVar("X", 1, 3);
		pb.post(pb.geqCard(x,2));
		pb.post(pb.eqCard(x,1));
		boolean contr = false;
		try {
			pb.propagate();
		} catch (ContradictionException e) {
			contr = true;
		}
		assertTrue(contr);
	}

	public void test10() {
		cardtest10(true);
	}

	public void test10_2() {
		cardtest10(false);
	}

	public void cardtest10(boolean cardr) {
		logger.finer("test10");
		pb = new Problem();
		pb.setCardReasoning(cardr);
		x = pb.makeSetVar("X", 0, 5);
		y = pb.makeSetVar("Y", 0, 5);
		z = pb.makeSetVar("Z", 0, 5);
		pb.post(pb.setUnion(x,y,z));
		pb.post(pb.leqCard(x,2));
		pb.post(pb.leqCard(y,2));
		pb.post(pb.geqCard(z,5));
		boolean contr = false;
		try {
			pb.propagate();
		} catch (ContradictionException e) {
			System.out.println("The contradiction is seen only if cardr is set to true");
			contr = true;
		}
		assertTrue(cardr == contr);
	}
}
