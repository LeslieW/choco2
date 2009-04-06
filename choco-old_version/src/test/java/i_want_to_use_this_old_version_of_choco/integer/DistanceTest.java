package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import i_want_to_use_this_old_version_of_choco.search.NodeLimit;
import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 23 f�vr. 2007
 * Time: 16:00:01
 * To change this template use File | Settings | File Templates.
 */
public class DistanceTest extends TestCase {

	public void test1Solve() {
		for (int seed = 0; seed < 10; seed++) {
			Problem pb = new Problem();
			int k = 9, k1 = 7, k2 = 6;
			IntDomainVar v0 = pb.makeEnumIntVar("v0", 0, 10);
			IntDomainVar v1 = pb.makeEnumIntVar("v1", 0, 10);
			IntDomainVar v2 = pb.makeEnumIntVar("v2", 0, 10);
			IntDomainVar v3 = pb.makeEnumIntVar("v3", 0, 10);
			pb.post(pb.distanceEQ(v0, v1, k));
			pb.post(pb.distanceEQ(v1, v2, k1));
			pb.post(pb.distanceEQ(v2, v3, k2));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, seed + 32));
			pb.getSolver().setValSelector(new RandomIntValSelector(seed));
			try {
				pb.propagate();
			} catch (ContradictionException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
			pb.solve();
			do {
				//System.out.println("|" + v0.getVal() + " - " + v1.getVal() + "| = " + k);
				//System.out.println("|" + v1.getVal() + " - " + v2.getVal() + "| = " + k1);
				//System.out.println("|" + v2.getVal() + " - " + v3.getVal() + "| = " + k2);
				//System.out.println("----------------");
			} while (pb.nextSolution() == Boolean.TRUE);
			int nbNode = ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot();
			System.out.println("solutions : " + pb.getSolver().getNbSolutions() + " nbNode : " + nbNode);
			assertEquals(pb.getSolver().getNbSolutions(), 4);
		}
	}


	//@Test
	public void test2Neq() {
		for (int seed = 0; seed < 10; seed++) {
			Problem pb = new Problem();
			int k = 9, k1 = 7, k2 = 6;
			IntDomainVar v0 = pb.makeEnumIntVar("v0", 0, 10);
			IntDomainVar v1 = pb.makeEnumIntVar("v1", 0, 10);
            IntDomainVar v2 = pb.makeEnumIntVar("v2", 0, 10);
            IntDomainVar v3 = pb.makeEnumIntVar("v3", 0, 10);

			IntDomainVar w0 = pb.makeEnumIntVar("w0", -100, 100);
	        IntDomainVar w1 = pb.makeEnumIntVar("w1", -100, 100);
	        IntDomainVar w2 = pb.makeEnumIntVar("w2", -100, 100);

			IntDomainVar absw0 = pb.makeEnumIntVar("absw0", -100, 100);
	        IntDomainVar absw1 = pb.makeEnumIntVar("absw1", -100, 100);
	        IntDomainVar absw2 = pb.makeEnumIntVar("absw2", -100, 100);

			pb.post(pb.eq(pb.minus(v0,v1),w0));
	        pb.post(pb.eq(pb.minus(v1,v2),w1));
	        pb.post(pb.eq(pb.minus(v2,v3),w2));

			pb.post(pb.abs(absw0,w0));
	        pb.post(pb.abs(absw1,w1));
	        pb.post(pb.abs(absw2,w2));
//
			pb.post(pb.neq(absw0,k ));
	        pb.post(pb.neq(absw1,k1));
	        pb.post(pb.neq(absw2,k2));

			Solver s = pb.getSolver();
			s.setVarIntSelector(new RandomIntVarSelector(pb, seed + 32));
			s.setValIntSelector(new RandomIntValSelector(seed));
			pb.solveAll();
			System.out.println("solutions : " + pb.getSolver().getNbSolutions());
			assertEquals(s.getNbSolutions(), 12147);
		}
	}


	public void test2SolveNegDoms() {
		for (int seed = 0; seed < 10; seed++) {

			Problem pb = new Problem();
			int k = 9, k1 = 7, k2 = 6;
			IntDomainVar v0 = pb.makeEnumIntVar("v0", -5, 5);
			IntDomainVar v1 = pb.makeEnumIntVar("v1", -5, 5);
			IntDomainVar v2 = pb.makeEnumIntVar("v2", -5, 5);
			IntDomainVar v3 = pb.makeEnumIntVar("v3", -5, 5);
			pb.post(pb.distanceEQ(v0, v1, k));
			pb.post(pb.distanceEQ(v1, v2, k1));
			pb.post(pb.distanceEQ(v2, v3, k2));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, seed + 32));
			pb.getSolver().setValSelector(new RandomIntValSelector(seed));
			try {
				pb.propagate();
			} catch (ContradictionException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
			pb.solve();
			do {
				//System.out.println("|" + v0.getVal() + " - " + v1.getVal() + "| = " + k);
				//System.out.println("|" + v1.getVal() + " - " + v2.getVal() + "| = " + k1);
				//System.out.println("|" + v2.getVal() + " - " + v3.getVal() + "| = " + k2);
				//System.out.println("----------------");
			} while (pb.nextSolution() == Boolean.TRUE);
			int nbNode = ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot();
			System.out.println("solutions : " + pb.getSolver().getNbSolutions() + " nbNode : " + nbNode);
			assertEquals(pb.getSolver().getNbSolutions(), 4);
		}
	}

	public void test3BoundsSolve() {
		for (int seed = 0; seed < 10; seed++) {

			Problem pb = new Problem();
			int k = 9, k1 = 7, k2 = 6;
			IntDomainVar v0 = pb.makeBoundIntVar("v0", 0, 10);
			IntDomainVar v1 = pb.makeBoundIntVar("v1", 0, 10);
			IntDomainVar v2 = pb.makeBoundIntVar("v2", 0, 10);
			IntDomainVar v3 = pb.makeBoundIntVar("v3", 0, 10);
			pb.post(pb.distanceEQ(v0, v1, k));
			pb.post(pb.distanceEQ(v1, v2, k1));
			pb.post(pb.distanceEQ(v2, v3, k2));
			pb.getSolver().setVarIntSelector(new RandomIntVarSelector(pb, seed + 32));
			pb.getSolver().setValIntSelector(new RandomIntValSelector(seed));
			try {
				pb.propagate();
			} catch (ContradictionException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
			pb.solve();
			do {
				//System.out.println("|" + v0.getVal() + " - " + v1.getVal() + "| = " + k);
				//System.out.println("|" + v1.getVal() + " - " + v2.getVal() + "| = " + k1);
				//System.out.println("|" + v2.getVal() + " - " + v3.getVal() + "| = " + k2);
				//System.out.println("----------------");
			} while (pb.nextSolution() == Boolean.TRUE);
			int nbNode = ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot();
			System.out.println("solutions : " + pb.getSolver().getNbSolutions() + " nbNode : " + nbNode);
			assertEquals(pb.getSolver().getNbSolutions(), 4);
		}
	}

	public void test3GTEnumSolve() {
		for (int seed = 0; seed < 10; seed++) {

			Problem pb = new Problem();
			int k = 8;
			IntDomainVar v0 = pb.makeEnumIntVar("v0", 0, 10);
			IntDomainVar v1 = pb.makeEnumIntVar("v1", 0, 10);
			pb.post(pb.distanceGT(v0, v1, k));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, seed + 32));
			pb.getSolver().setValSelector(new RandomIntValSelector(seed));
			try {
				pb.propagate();
			} catch (ContradictionException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
			pb.solve();
			do {
				//System.out.println("|" + v0.getVal() + " - " + v1.getVal() + "| > " + k);
			} while (pb.nextSolution() == Boolean.TRUE);
			int nbNode = ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot();
			System.out.println("solutions : " + pb.getSolver().getNbSolutions() + " nbNode : " + nbNode);
			assertEquals(pb.getSolver().getNbSolutions(), 6);
		}
	}

	public void test3GTBoundSolve() {
		for (int seed = 0; seed < 10; seed++) {

			Problem pb = new Problem();
			int k = 8;
			IntDomainVar v0 = pb.makeBoundIntVar("v0", 0, 10);
			IntDomainVar v1 = pb.makeBoundIntVar("v1", 0, 10);
			pb.post(pb.distanceGT(v0, v1, k));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, seed + 32));
			pb.getSolver().setValSelector(new RandomIntValSelector(seed));
			try {
				pb.propagate();
			} catch (ContradictionException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
			pb.solve();
			do {
				//System.out.println("|" + v0.getVal() + " - " + v1.getVal() + "| > " + k);
			} while (pb.nextSolution() == Boolean.TRUE);
			int nbNode = ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot();
			System.out.println("solutions : " + pb.getSolver().getNbSolutions() + " nbNode : " + nbNode);
			assertEquals(pb.getSolver().getNbSolutions(), 6);
		}
	}

	public void test3LTSolve() {
		for (int seed = 0; seed < 10; seed++) {

			Problem pb = new Problem();
			int k = 2;
			IntDomainVar v0 = pb.makeEnumIntVar("v0", 0, 10);
			IntDomainVar v1 = pb.makeEnumIntVar("v1", 0, 10);
			pb.post(pb.distanceLT(v0, v1, k));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, seed + 32));
			pb.getSolver().setValSelector(new RandomIntValSelector(seed));
			try {
				pb.propagate();
			} catch (ContradictionException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
			pb.solve();
			do {
				//System.out.println("|" + v0.getVal() + " - " + v1.getVal() + "| < " + k);
			} while (pb.nextSolution() == Boolean.TRUE);
			int nbNode = ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot();
			System.out.println("solutions : " + pb.getSolver().getNbSolutions() + " nbNode : " + nbNode);
			assertEquals(pb.getSolver().getNbSolutions(), 31);
			assertEquals(nbNodeFromRegulatModel(seed), nbNode);
		}
	}

	public int nbNodeFromRegulatModel(int seed) {
		Problem pb = new Problem();
		int k = 2;
		IntDomainVar v0 = pb.makeEnumIntVar("v0", 0, 10);
		IntDomainVar v1 = pb.makeEnumIntVar("v1", 0, 10);
		List<int[]> ltuple = new LinkedList<int[]>();
		for (int i = 0; i <= 10; i++) {
			for (int j = 0; j <= 10; j++) {
				if (Math.abs(i - j) < k)
					ltuple.add(new int[]{i, j});
			}
		}
		pb.post(pb.regular(new IntVar[]{v0, v1}, ltuple));
		pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, seed + 32));
		pb.getSolver().setValSelector(new RandomIntValSelector(seed));
		try {
			pb.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		pb.solve();
		do {
			//System.out.println("|" + v0.getVal() + " - " + v1.getVal() + "| < " + k);
		} while (pb.nextSolution() == Boolean.TRUE);
		//System.out.println("solutions regular : " + pb.getSolver().getNbSolutions());
		return ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot();
	}

	//********************************************************************//
	//****************************** Test on DistanceXYZ *****************//
	//********************************************************************//

	public void testDXYZProp1() {
		Problem pb = new Problem();
		IntDomainVar v0 = pb.makeBoundIntVar("v0", 1, 4);
		IntDomainVar v1 = pb.makeBoundIntVar("v1", 5, 7);
		IntDomainVar v2 = pb.makeBoundIntVar("v2", -100, 100);
		pb.post(pb.distanceEQ(v0, v1, v2, 0));
		try {
			pb.propagate();
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		assertEquals(1, v2.getInf());
		assertEquals(6, v2.getSup());
	}

	public void testDXYZProp1bis() {
		Problem pb = new Problem();
		IntDomainVar v0 = pb.makeBoundIntVar("v0", 1, 4);
		IntDomainVar v1 = pb.makeBoundIntVar("v1", 5, 7);
		IntDomainVar v2 = pb.makeBoundIntVar("v2", -100, 100);
		pb.post(pb.distanceEQ(v0, v1, v2, 2));
		try {
			pb.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		assertEquals(-1, v2.getInf());
		assertEquals(4, v2.getSup());
	}


	public void testDXYZProp2() {
		Problem pb = new Problem();
		IntDomainVar v0 = pb.makeBoundIntVar("v0", 1, 5);
		IntDomainVar v1 = pb.makeBoundIntVar("v1", 5, 10);
		IntDomainVar v2 = pb.makeBoundIntVar("v2", 1, 2);
		pb.post(pb.distanceEQ(v0, v1, v2, 0));
		try {
			pb.propagate();
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		assertEquals(3, v0.getInf());
		assertEquals(7, v1.getSup());
	}

	public void testDXYZProp2bis() {
		Problem pb = new Problem();
		IntDomainVar v0 = pb.makeBoundIntVar("v0", 1, 5);
		IntDomainVar v1 = pb.makeBoundIntVar("v1", 5, 10);
		IntDomainVar v2 = pb.makeBoundIntVar("v2", 1, 2);
		pb.post(pb.distanceEQ(v0, v1, v2, -1));
		try {
			pb.propagate();
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		assertEquals(4, v0.getInf());
		assertEquals(6, v1.getSup());
		pb.solve();
		System.out.println("" + pb.pretty());
	}


	public void testDXYZProp3() {
		Problem pb = new Problem();
		IntDomainVar v0 = pb.makeBoundIntVar("v0", 1, 5);
		IntDomainVar v1 = pb.makeBoundIntVar("v1", 5, 6);
		IntDomainVar v2 = pb.makeBoundIntVar("v2", 3, 10);
		pb.post(pb.distanceEQ(v0, v1, v2, 0));
		try {
			pb.propagate();
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		assertEquals(3, v0.getSup());
		assertEquals(5, v2.getSup());
	}

	public void testDXYZProp4() {
		Problem pb = new Problem();
		IntDomainVar v0 = pb.makeEnumIntVar("v0", -1, 5);
		IntDomainVar v1 = pb.makeEnumIntVar("v1", -5, 6);
		IntDomainVar v2 = pb.makeEnumIntVar("v2", -2, 2);
		pb.post(pb.distanceEQ(v0, v1, v2, 0));
		try {
			pb.propagate();
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		assertEquals(-3, v1.getInf());
		assertEquals(0, v2.getInf());
	}

	public void testDXYZProp5() {
		Problem pb = new Problem();
		IntDomainVar v0 = pb.makeEnumIntVar("v0", -1, 1);
		IntDomainVar v1 = pb.makeEnumIntVar("v1", -5, 6);
		IntDomainVar v2 = pb.makeEnumIntVar("v2", 3, 10);

		// IntDomainVar z = pb.makeEnumIntVar("z", -100, 100);
		// pb.post(pb.eq(pb.minus(v0,v1),z));
		// pb.post(pb.abs(v2,z));

		pb.post(pb.distanceEQ(v0, v1, v2, 0));
		try {
			pb.propagate();
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		System.out.println("" + v1.pretty());
		assertTrue(!v1.canBeInstantiatedTo(0));
		assertTrue(!v1.canBeInstantiatedTo(1));
		assertTrue(!v1.canBeInstantiatedTo(-1));
		assertEquals(7, v2.getSup());
	}

	public void testDXYZSolve1() {
		for (int seed = 0; seed < 10; seed++) {
			Problem pb = new Problem();
			IntDomainVar v0 = pb.makeEnumIntVar("v0", 0, 5);
			IntDomainVar v1 = pb.makeEnumIntVar("v1", 0, 5);
			IntDomainVar v2 = pb.makeEnumIntVar("v2", 0, 5);
			pb.post(pb.distanceEQ(v0, v1, v2, 0));

			pb.getSolver().setVarIntSelector(new RandomIntVarSelector(pb, seed + 32));
			pb.getSolver().setValIntSelector(new RandomIntValSelector(seed));

		    pb.solveAll();
			assertEquals(36, pb.getSolver().getNbSolutions());
		}
	}


	public void testDXYZSolve2() {
		for (int seed = 0; seed < 10; seed++) {
			Problem pb = new Problem();
			IntDomainVar v0 = pb.makeEnumIntVar("v0", 3, 6);
			IntDomainVar v1 = pb.makeEnumIntVar("v1", -3, 4);
			IntDomainVar v2 = pb.makeEnumIntVar("v2", 0, 5);
			IntDomainVar v3 = pb.makeEnumIntVar("v3", 2, 5);

			pb.post(pb.distanceEQ(v0, v1, v2, 0));
			pb.post(pb.distanceEQ(v0, v2, v3, 0));

			pb.getSolver().setVarIntSelector(new RandomIntVarSelector(pb, seed + 32));
			pb.getSolver().setValIntSelector(new RandomIntValSelector(seed));

		    pb.solveAll();
			System.out.println("nbsol " + pb.getSolver().getNbSolutions());
			assertEquals(getNbSolByDecomp(0), pb.getSolver().getNbSolutions());
		}
	}

	public void testDXYZSolve3() {
		for (int seed = 0; seed < 10; seed++) {
			Problem pb = new Problem();
			IntDomainVar v0 = pb.makeEnumIntVar("v0", 3, 6);
			IntDomainVar v1 = pb.makeEnumIntVar("v1", -3, 4);
			IntDomainVar v2 = pb.makeEnumIntVar("v2", 0, 5);
			IntDomainVar v3 = pb.makeEnumIntVar("v3", 2, 5);

			pb.post(pb.distanceLT(v0, v1, v2, 0));
			pb.post(pb.distanceLT(v0, v2, v3, 0));

			pb.getSolver().setVarIntSelector(new RandomIntVarSelector(pb, seed + 32));
			pb.getSolver().setValIntSelector(new RandomIntValSelector(seed));

		    pb.solveAll();
			System.out.println("nbsol " + pb.getSolver().getNbSolutions());
			assertEquals(getNbSolByDecomp(1), pb.getSolver().getNbSolutions());
		}
	}

	public void testDXYZSolve4() {
		for (int seed = 0; seed < 10; seed++) {
			Problem pb = new Problem();
			IntDomainVar v0 = pb.makeEnumIntVar("v0", 3, 6);
			IntDomainVar v1 = pb.makeEnumIntVar("v1", -3, 4);
			IntDomainVar v2 = pb.makeEnumIntVar("v2", 0, 5);
			IntDomainVar v3 = pb.makeEnumIntVar("v3", 2, 5);

			pb.post(pb.distanceGT(v0, v1, v2, 0));
			pb.post(pb.distanceGT(v0, v2, v3, 0));

			pb.getSolver().setVarIntSelector(new RandomIntVarSelector(pb, seed + 32));
			pb.getSolver().setValIntSelector(new RandomIntValSelector(seed));

		    pb.solveAll();
			System.out.println("nbsol " + pb.getSolver().getNbSolutions());
			assertEquals(getNbSolByDecomp(2), pb.getSolver().getNbSolutions());
		}
	}

	public int getNbSolByDecomp(int op) {
		Problem pb = new Problem();
			IntDomainVar v0 = pb.makeEnumIntVar("v0", 3, 6);
			IntDomainVar v1 = pb.makeEnumIntVar("v1", -3, 4);
			IntDomainVar interV0V1 = pb.makeEnumIntVar("v01", -100, 100);
			IntDomainVar v2 = pb.makeEnumIntVar("v2", 0, 5);
			IntDomainVar interV0V2 = pb.makeEnumIntVar("v02", -100, 100);
		    IntDomainVar v3 = pb.makeEnumIntVar("v3", 2, 5);

			pb.post(pb.eq(pb.minus(v0,v1),interV0V1));
			pb.post(pb.eq(pb.minus(v0,v2),interV0V2));

			if (op == 0) {
				pb.post(pb.abs(v2,interV0V1));
				pb.post(pb.abs(v3,interV0V2));
			} else if (op == 1) {
				IntDomainVar interV0V1bis = pb.makeEnumIntVar("v01b", -100, 100);
				IntDomainVar interV0V2bis = pb.makeEnumIntVar("v02b", -100, 100);
				pb.post(pb.abs(interV0V1bis,interV0V1));
				pb.post(pb.abs(interV0V2bis,interV0V2));
				pb.post(pb.lt(interV0V1bis,v2));
				pb.post(pb.lt(interV0V2bis,v3));
			} else {
				IntDomainVar interV0V1bis = pb.makeEnumIntVar("v01b", -100, 100);
				IntDomainVar interV0V2bis = pb.makeEnumIntVar("v02b", -100, 100);
				pb.post(pb.abs(interV0V1bis,interV0V1));
				pb.post(pb.abs(interV0V2bis,interV0V2));
				pb.post(pb.gt(interV0V1bis,v2));
				pb.post(pb.gt(interV0V2bis,v3));
			}


		    pb.solveAll();
			return pb.getSolver().getNbSolutions();
	}
}

