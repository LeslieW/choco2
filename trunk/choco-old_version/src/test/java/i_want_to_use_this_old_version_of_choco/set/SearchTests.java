package i_want_to_use_this_old_version_of_choco.set;

import i_want_to_use_this_old_version_of_choco.*;
import i_want_to_use_this_old_version_of_choco.set.search.*;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class SearchTests extends TestCase {

	private Logger logger = Logger.getLogger("choco.currentElement");
	private Problem pb;
	private SetVar x;
	private SetVar y;
	private SetVar z;
	private Constraint c1;
	private Constraint c2;
	private Constraint c3;

	protected void setUp() {
		logger.fine("EqualXC Testing...");
		pb = new Problem();
	}

	protected void tearDown() {
		c1 = null;
		c2 = null;
		c3 = null;
		x = null;
		y = null;
		z = null;
		pb = null;
	}

	/**
	 * A ternary Steiner system of order n is a set of triplets of distinct elements
	 * taking their values between 1 and n, such that all the pairs included in two different triplets are different.
	 * une solution pour n = 7 : [{1, 2, 3}, {2, 4, 5}, {3, 4, 6}, {1, 4, 7}, {1, 5, 6}, {2,6, 7}, {3, 5, 7}]
	 * Il faut que n % 6 = 1 ou n % 6 = 3 pour n soit une valeur valide pour le pb
	 */
	public void steinerSystem(int m) {
		pb = new Problem();
		//Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search").setLevel(Level.FINEST);
		int n = m * (m - 1) / 6;
		SetVar[] vars = new SetVar[n];
		SetVar[] intersect = new SetVar[n * n];
		for (int i = 0; i < n; i++)
			vars[i] = pb.makeSetVar("set " + i, 1, n);
		for (int i = 0; i < n; i++)
			for (int j = i + 1; j < n; j++)
				intersect[i * n + j] = pb.makeSetVar("interSet " + i + " " + j, 1, n);

		for (int i = 0; i < n; i++)
			pb.post(pb.eqCard(vars[i], 3));
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				pb.post(pb.setInter(vars[i], vars[j], intersect[i * n + j]));
				pb.post(pb.leqCard(intersect[i * n + j], 1));
			}
		}

		pb.getSolver().setFirstSolution(true);
		pb.getSolver().generateSearchSolver(pb);
		pb.getSolver().attachGoal(new AssignSetVar(new MinDomSet(pb, vars), new MinEnv(pb)));
		pb.getSolver().launch();
		//pb.solve();
		System.out.println("NbSolution " + pb.getSolver().getNbSolutions());
		Solution sol = (Solution) pb.getSolver().getSearchSolver().solutions.get(0);
		sol.restore();
		for (int i = 0; i < n; i++) {
			System.out.println("set[" + i + "]:" + vars[i].pretty());
		}
		System.out.println("Nb node " + pb.getSolver().getSearchSolver().getNodeCount());
		assertTrue(pb.isFeasible());
	}

	public void test2() {
		steinerSystem(7);
	}

	
	public void test1() {
		pb = new Problem();
		x = pb.makeSetVar("X", 1, 5);
		y = pb.makeSetVar("Y", 1, 2);
		c1 = pb.member(x, 3);
		c2 = pb.member(x, 5);
		c3 = pb.notMember(x, 2);
		logger.finer("test1");
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
		pb.getSolver().setFirstSolution(false);
		pb.getSolver().generateSearchSolver(pb);
		pb.getSolver().addGoal(new AssignSetVar(new MinDomSet(pb), new MinEnv(pb)));
		pb.getSolver().launch();

		assertEquals(16, pb.getSolver().getNbSolutions());
	}

	public void test3() {
		for (int i = 0; i < 10; i++) {
			Problem pb = new Problem();
			SetVar s1 = pb.makeSetVar("s1", 1, 3);
			SetVar x = pb.makeSetVar("x", 1, 3);
			SetVar y = pb.makeSetVar("y", 1, 3);
			pb.post(pb.setUnion(x, y, s1));
			pb.post(pb.setDisjoint(x, y));
			pb.post(pb.geqCard(x, 1));

			pb.getSolver().setFirstSolution(false);
			pb.getSolver().generateSearchSolver(pb);
			pb.getSolver().addGoal(new AssignSetVar(new RandomSetVarSelector(pb, i), new RandomSetValSelector(i + 1)));
			pb.getSolver().launch();

			System.out.println("Nb solution: " + pb.getSolver().getNbSolutions());

//			System.out.println("" + s1);
//			System.out.println("" + x);
//			System.out.println("" + y);

			assertTrue(19 == pb.getSolver().getNbSolutions());
			//pb.getSolver().setVarSetSelector(new StaticSetVarOrder(new SetVar[]{x, y , s1}));
			//pb.solve();
			//pb.getSolver().setVarSetSelector(new StaticSetVarOrder(new SetVar[]{x, y , s1}));
		}
	}

	public void test4() {

		Problem pb = new Problem();
		SetVar s1 = pb.makeSetVar("s1", 1, 10);
		pb.post(pb.geqCard(s1, 1));
		pb.post(pb.eqCard(s1, 0));
		pb.getSolver().setFirstSolution(false);
		pb.getSolver().generateSearchSolver(pb);
		pb.getSolver().addGoal(new AssignSetVar(new RandomSetVarSelector(pb), new RandomSetValSelector()));
		boolean contr = false;
		try {
			pb.propagate();
		} catch (ContradictionException e) {
			contr = true;
		}
		//pb.getSolver().launch();
		assertTrue(contr);

	}

	public void test5() {
		Problem pb = new Problem();
		SetVar s1 = pb.makeSetVar("s1", 1, 10);
		SetVar s2 = pb.makeSetVar("s2", 1, 10);
		SetVar s3 = pb.makeSetVar("s3", 1, 10);

		pb.post(pb.setInter(s1, s2, s3));
		pb.post(pb.eqCard(s1, 0));
		pb.post(pb.geqCard(s3, 1));
		try {
			pb.propagate();
		} catch (ContradictionException e) {
			System.out.println("contradiction");
		}

		Iterator it = pb.getIntConstraintIterator();
		while(it.hasNext()) {
			Propagator prop = (Propagator) it.next();
			prop.constAwake(true);
		}
		pb.solve();
		assertFalse(pb.isFeasible());
	}
}

