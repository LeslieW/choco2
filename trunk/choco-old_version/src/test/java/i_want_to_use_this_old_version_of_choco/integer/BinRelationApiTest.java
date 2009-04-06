package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.constraints.extension.BinRelation;
import i_want_to_use_this_old_version_of_choco.integer.constraints.extension.ConsistencyRelation;
import i_want_to_use_this_old_version_of_choco.integer.constraints.extension.CouplesTest;
import i_want_to_use_this_old_version_of_choco.integer.constraints.extension.CspBinConstraint;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class BinRelationApiTest extends TestCase {

	private Logger logger = Logger.getLogger("choco.currentElement");
	private Problem pb;
	private IntDomainVar v1, v2, v3, v4;
	boolean[][] matrice1, matrice2;
	ArrayList couples1, couples2;

	protected void setUp() {
		logger.fine("BitSetIntDomain Testing...");
		pb = new Problem();
		//v4 = pb.makeEnumIntVar("v4", 3, 8);
		matrice1 = new boolean[][]{
				{false, true, true, true},
				{true, false, true, false},
				{false, false, false, false},
				{true, true, true, false}};
		couples1 = new ArrayList();
		couples1 = new ArrayList();
		couples1.add(new int[]{1, 2});
		couples1.add(new int[]{1, 3});
		couples1.add(new int[]{1, 4});
		couples1.add(new int[]{2, 1});
		couples1.add(new int[]{2, 3});
		couples1.add(new int[]{3, 1});
		couples1.add(new int[]{3, 2});
		couples1.add(new int[]{3, 3});
		matrice2 = new boolean[][]{
				{false, true, true, false},
				{true, false, false, false},
				{false, false, true, false},
				{false, true, false, false}};
		couples2 = new ArrayList();
		couples2.add(new int[]{1, 2});
		couples2.add(new int[]{1, 3});
		couples2.add(new int[]{2, 1});
		couples2.add(new int[]{3, 1});
		couples2.add(new int[]{4, 1});

	}

	protected void tearDown() {
		v1 = null;
		v2 = null;
		v3 = null;
		v4 = null;
		pb = null;
		matrice1 = null;
		matrice2 = null;
		couples1 = null;
		couples2 = null;
	}

	public void test1FeasAc4() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		pb.post(pb.feasPairAC(v1, v2, matrice2, 4));
		pb.solveAll();
		assertEquals(5, pb.getSolver().getNbSolutions());
	}

	public void test23FeasAc32() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		pb.post(pb.feasPairAC(v1, v2, couples2, 4));
		pb.solveAll();
		assertEquals(5, pb.getSolver().getNbSolutions());
	}

	public void test2FeasAc4() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		pb.post(pb.feasPairAC(v1, v2, couples2, 4));
		pb.solveAll();
		assertEquals(5, pb.getSolver().getNbSolutions());
	}


	public void test2FeasAc4bis() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		Constraint c = pb.feasPairAC(v1, v2, couples2, 4);
		ConsistencyRelation r = (ConsistencyRelation) ((CspBinConstraint) c).getRelation();
		pb.post(pb.relationPairAC(v1, v2, (BinRelation) (r).getOpposite()));
		pb.solveAll();
		assertEquals((16 - 5), pb.getSolver().getNbSolutions());
	}

	public void test1InFeasAc4() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		pb.post(pb.infeasPairAC(v1, v2, matrice2, 4));
		pb.solveAll();
		assertEquals((16 - 5), pb.getSolver().getNbSolutions());
	}

	public void test2InFeasAc4() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		pb.post(pb.infeasPairAC(v1, v2, couples2, 4));
		pb.solveAll();
		assertEquals((16 - 5), pb.getSolver().getNbSolutions());
	}

	public void test1FeasAc3() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		pb.post(pb.feasPairAC(v1, v2, matrice2, 3));
		pb.solveAll();
		assertEquals(5, pb.getSolver().getNbSolutions());
	}

	public void test1FeasAc2001() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		pb.post(pb.feasPairAC(v1, v2, matrice2, 2001));
		pb.solve();
		System.out.println("v1 : " + v1.getVal() + " v2: " + v2.getVal());
		while (pb.nextSolution() == Boolean.TRUE) {
			System.out.println("v1 : " + v1.getVal() + " v2: " + v2.getVal());
		}
		assertEquals(5, pb.getSolver().getNbSolutions());
	}

	public void test1FeasAc32() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		pb.post(pb.feasPairAC(v1, v2, matrice2, 32));
		pb.solve();
		System.out.println("v1 : " + v1.getVal() + " v2: " + v2.getVal());
		while (pb.nextSolution() == Boolean.TRUE) {
			System.out.println("v1 : " + v1.getVal() + " v2: " + v2.getVal());
		}
		assertEquals(5, pb.getSolver().getNbSolutions());
	}


	public void test3FeasAc2001() {
		v1 = pb.makeEnumIntVar("v1", 0, 4);
		v2 = pb.makeEnumIntVar("v2", 0, 2);
		ArrayList feasTuple = new ArrayList();
		feasTuple.add(new int[]{1, 1}); // x*y = 1
		feasTuple.add(new int[]{4, 2}); // x*y = 1
		Constraint c = pb.feasPairAC(v1, v2, feasTuple, 2001);
		System.out.println("c = " + c.pretty());
		pb.post(c);
		try {
			pb.propagate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		pb.solve();
		do {
			System.out.println("v1 : " + v1.getVal() + " v2: " + v2.getVal());
		} while (pb.nextSolution() == Boolean.TRUE);
		assertEquals(2, pb.getSolver().getNbSolutions());
	}

	public void test1InFeasAc3() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		pb.post(pb.infeasPairAC(v1, v2, matrice2, 3));
		pb.solveAll();
		assertEquals((16 - 5), pb.getSolver().getNbSolutions());
	}

	public void test3FeasAc4() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		v3 = pb.makeEnumIntVar("v3", 3, 6);
		pb.post(pb.feasPairAC(v1, v2, matrice1, 4));
		pb.post(pb.feasPairAC(v2, v3, matrice2, 4));
		pb.solveAll();
		assertEquals(10, pb.getSolver().getNbSolutions());
	}

	public void test2FeasAc2001() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		v3 = pb.makeEnumIntVar("v3", 3, 6);
		pb.post(pb.feasPairAC(v1, v2, matrice1, 2001));
		pb.post(pb.feasPairAC(v2, v3, matrice2, 2001));
		pb.solveAll();
		assertEquals(10, pb.getSolver().getNbSolutions());
	}

	public void test2FeasAc32() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		v3 = pb.makeEnumIntVar("v3", 3, 6);
		pb.post(pb.feasPairAC(v1, v2, matrice1, 32));
		pb.post(pb.feasPairAC(v2, v3, matrice2, 32));
		pb.solveAll();
		assertEquals(10, pb.getSolver().getNbSolutions());
	}


	public void test4FeasAc4() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		v3 = pb.makeEnumIntVar("v3", 3, 6);
		pb.post(pb.relationPairAC(v1, v2, new MyEquality(), 4));
		pb.post(pb.relationPairAC(v2, v3, new MyEquality(), 4));
		pb.solveAll();
		assertEquals(2, pb.getSolver().getNbSolutions());
	}

	public void test5FeasAc4() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		v3 = pb.makeEnumIntVar("v3", 5, 8);
		pb.post(pb.relationPairAC(v1, v2, new MyInequality(), 4));
		pb.post(pb.relationPairAC(v2, v3, new MyInequality(), 4));
		pb.solveAll();
		assertEquals(48, pb.getSolver().getNbSolutions());
	}

	public void test6FeasAc4() {
		v1 = pb.makeEnumIntVar("v1", 1, 4);
		v2 = pb.makeEnumIntVar("v2", 1, 4);
		v3 = pb.makeEnumIntVar("v3", 5, 8);
		pb.post(pb.relationPairAC(v1, v2, (BinRelation) (new MyEquality()).getOpposite(), 4));
		pb.post(pb.relationPairAC(v2, v3, (BinRelation) (new MyEquality()).getOpposite(), 4));
		pb.solveAll();
		assertEquals(48, pb.getSolver().getNbSolutions());
	}


	private class MyEquality extends CouplesTest {

		public boolean checkCouple(int x, int y) {
			return x == y;
		}

	}

	private class MyInequality extends CouplesTest {

		public boolean checkCouple(int x, int y) {
			return x != y;
		}
	}

}
