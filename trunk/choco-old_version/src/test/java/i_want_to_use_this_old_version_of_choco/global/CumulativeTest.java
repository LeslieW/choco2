package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.*;
import i_want_to_use_this_old_version_of_choco.global.scheduling.SchedulingSettings;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import i_want_to_use_this_old_version_of_choco.search.NodeLimit;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 27 mars 2006
 * Time: 10:03:39
 * To change this template use File | Settings | File Templates.
 */
public class CumulativeTest extends TestCase {

	//***********************************************************//
	//******************* Test Cumulative *********************//
	//***********************************************************//

	public void test1() {
		SchedulingSettings.noCumulativeTaskInterval();
		_test1();
	}

	public void test2() {
		SchedulingSettings.noCumulativeTaskInterval();
		_test2();
	}

	public void test3() {
		SchedulingSettings.noCumulativeTaskInterval();
		_test3();
	}

	public void test4() {
		SchedulingSettings.noCumulativeTaskInterval();
		_test4();
	}

	public void test5() {
		SchedulingSettings.noCumulativeTaskInterval();
		_test5();
	}

	public void test6() {
		SchedulingSettings.noCumulativeTaskInterval();
		_test6();
	}

	//***********************************************************//
	//******************* Test Edge finding *********************//
	//***********************************************************//

	public void test1_ef() {
		SchedulingSettings.setCumulativeEdgeFinding();
		_test1();
	}

	public void test2_ef() {
		SchedulingSettings.setCumulativeEdgeFinding();
		_test2();
	}

	public void test3_ef() {
		SchedulingSettings.setCumulativeTaskInterval();
		SchedulingSettings.noCumulativeEdgeFinding();
		_test3();
	}

	public void test4_ef() {
		SchedulingSettings.setCumulativeEdgeFinding();
		_test4();
	}

	public void test5_ef() {
		SchedulingSettings.setCumulativeEdgeFinding();
		_test5();
	}

	public void test52_ef() {
		SchedulingSettings.setCumulativeTaskInterval();
		SchedulingSettings.noCumulativeEdgeFinding();
		_test5();
	}

	public void test6_ef() {
		SchedulingSettings.setCumulativeEdgeFinding();
		_test6();
	}

	public static IntDomainVar createTest(boolean start, int[] p, int[] d, int[] r, int[] h, int C) {
		int n = p.length;
		Problem pb = new Problem();
		IntDomainVar[] durations = new IntDomainVar[n];
		IntDomainVar[] starts = new IntDomainVar[n];
		IntDomainVar[] ends = new IntDomainVar[n];
		for (int i = 0; i < n; i++) {
			durations[i] = (IntDomainVar) pb.makeConstantIntVar("t" + i, p[i]);
			starts[i] = pb.makeBoundIntVar("t" + i, r[i], 100);
			ends[i] = pb.makeBoundIntVar("t" + i, 0, d[i]);
		}

		SchedulingSettings.setCumulativeEdgeFinding();
		pb.post(pb.cumulative(starts, ends, durations, h, C));
		if (start) return starts[0];
		else return ends[0];
	}


	/**
	 * Example found page 59 of the book : Constraint Based Scheduling
	 */
	public static void testPropagEdgeFinding() {
		int[] p = new int[]{11, 6, 5, 5};
		int[] d = new int[]{19, 10, 10, 10};
		int[] r = new int[]{0, 0, 0, 0};
		int[] h = new int[]{1, 1, 1, 1};
		IntDomainVar v = createTest(true, p, d, r, h, 2);
		try {
			System.out.println(v.pretty());
			v.getProblem().propagate();
			System.out.println(v.pretty());
			assertEquals(v.getInf(), 6);
		} catch (ContradictionException e) {
			assertTrue(false);
			e.printStackTrace();
		}
	}

	/**
	 * Ending date version of Example found page 59 of the book : Constraint Based Scheduling
	 */
	public static void testPropagEdgeFindingEnding() {
		int[] p = new int[]{11, 6, 5, 5};
		int[] d = new int[]{19, 19, 19, 19};
		int[] r = new int[]{0, 9, 9, 9};
		int[] h = new int[]{1, 1, 1, 1};
		IntDomainVar v = createTest(false, p, d, r, h, 2);
		try {
			System.out.println(v.pretty());
			v.getProblem().propagate();
			System.out.println(v.pretty());
			assertEquals(13, v.getSup());
		} catch (ContradictionException e) {
			assertTrue(false);
			e.printStackTrace();
		}
	}

	/**
	 * Another small example
	 */
	public static void testPropagEdgeFinding2() {
		int[] p = new int[]{11, 6, 5, 5, 8};
		int[] d = new int[]{50, 10, 10, 10, 22};
		int[] r = new int[]{0, 0, 0, 0, 12};
		int[] h = new int[]{1, 1, 1, 1, 2};
		IntDomainVar v = createTest(true, p, d, r, h, 2);
		try {
			System.out.println(v.pretty());
			v.getProblem().propagate();
			System.out.println(v.pretty());
			assertEquals(v.getInf(), 20);
		} catch (ContradictionException e) {
			assertTrue(false);
			e.printStackTrace();
		}
	}

	/**
	 * Pascal example to show that nuijten is incomplete
	 * NOTE : edge finding is not needed to do the deduction that Nuitjen is missing !!!
	 */
	public static void testEdgeFinding() {
		int[] d = new int[]{69, 2, 3, 3, 3};
		int[] p = new int[]{4, 1, 1, 1, 1};
		int[] r = new int[]{0, 1, 0, 0, 2};
		int[] h = new int[]{1, 4, 2, 2, 1};
		IntDomainVar v = createTest(true, p, d, r, h, 4);
		try {
			System.out.println(v.pretty());
			v.getProblem().propagate();
			System.out.println(v.pretty());
			assertEquals(v.getInf(), 2);
		} catch (ContradictionException e) {
			assertTrue(false);
			e.printStackTrace();
		}
	}

	public static void parse(int[] r, int[] d, int[] p, int[] h) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("nluctest.txt")));
			String s = br.readLine();
			s = br.readLine();
			int i = 0;
			while (s != null) {
				StringTokenizer st = new StringTokenizer(s, "\t");
				st.nextToken();
				r[i] = Integer.parseInt(st.nextToken());
				d[i] = Integer.parseInt(st.nextToken());
				p[i] = Integer.parseInt(st.nextToken());
				h[i] = Integer.parseInt(st.nextToken());
				i++;
				s = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	//todo: need a directory to files for tests
	public static void _complexeEdgeFindingExample() {
		int n = 10000;
		int[] d = new int[n];
		int[] p = new int[n];
		int[] r = new int[n];
		int[] h = new int[n];
		parse(r, d, p, h);
		Problem pb = new Problem();
		IntDomainVar[] durations = new IntDomainVar[n];
		IntDomainVar[] starts = new IntDomainVar[n];
		IntDomainVar[] ends = new IntDomainVar[n];
		for (int i = 0; i < n; i++) {
			durations[i] = (IntDomainVar) pb.makeConstantIntVar("t" + i, p[i]);
			starts[i] = pb.makeBoundIntVar("t" + i, r[i], 120000);
			ends[i] = pb.makeBoundIntVar("t" + i, 0, d[i]);
		}

		SchedulingSettings.setCumulativeTaskInterval();
		SchedulingSettings.noCumulativeEdgeFinding();

		pb.post(pb.cumulative(starts, ends, durations, h, 2));
		try {
			System.out.println(" " + starts[0].pretty());
			long tps = System.currentTimeMillis();
			pb.propagate();
			System.out.println("tps : " + (System.currentTimeMillis() - tps));
			for (int i = 0; i < n; i++) {
				System.out.println("A" + i + ":" + " r=" + starts[i].getInf() + " d=" + ends[i].getSup() + " p=" + p[i] + " d=" + h[i]);
			}
			//assertEquals(starts[0].getInf(), 2);
		} catch (ContradictionException e) {
			assertTrue(false);
			e.printStackTrace();
		}
	}

	public static void testEnd1() {
		AbstractProblem pb = new Problem();
		IntDomainVar d1 = pb.makeEnumIntVar("d1", 2, 2);
		IntDomainVar d2 = pb.makeEnumIntVar("d2", 2, 2);
		IntDomainVar d3 = pb.makeEnumIntVar("d3", 1, 1);
		IntDomainVar[] durations = new IntDomainVar[]{d1, d2, d3};
		IntDomainVar[] starts = new IntDomainVar[3];
		IntDomainVar[] ends = new IntDomainVar[3];
		for (int i = 0; i < 3; i++) {
			starts[i] = pb.makeEnumIntVar("s" + i, 0, 3);
			ends[i] = pb.makeEnumIntVar("e" + i, 0, 3);
		}

		Solver.setVerbosity(Solver.PROPAGATION);
        Constraint c = pb.cumulative(starts, ends, durations, new int[]{2, 1, 3}, 3);
        System.out.println("c = " + c.pretty());
        pb.post(c);
		try {
			starts[0].setVal(1);
			starts[2].remVal(1);
			ends[2].remVal(2);
			pb.propagate();
		} catch (ContradictionException e) {

			Solver.flushLogs();
			assertTrue(false);
		}

		Solver.flushLogs();
		//System.out.println(seed + " nbSol " + pb.getSolver().getNbSolutions());
	}

	/**
	 * Trivial exemple with 2 solutions
	 */
	public static void _test1() {
		for (int seed = 0; seed < 10; seed++) {
			AbstractProblem pb = new Problem();
			IntDomainVar d1 = pb.makeEnumIntVar("d1", 2, 2);
			IntDomainVar d2 = pb.makeEnumIntVar("d2", 2, 2);
			IntDomainVar d3 = pb.makeEnumIntVar("d3", 1, 1);
			IntDomainVar[] durations = new IntDomainVar[]{d1, d2, d3};
			IntDomainVar[] starts = new IntDomainVar[3];
			IntDomainVar[] ends = new IntDomainVar[3];
			for (int i = 0; i < 3; i++) {
				starts[i] = pb.makeEnumIntVar("s" + i, 0, 3);
				ends[i] = pb.makeEnumIntVar("e" + i, 0, 3);
			}

			//Solver.setVerbosity(Solver.PROPAGATION);
			pb.post(pb.cumulative(starts, ends, durations, new int[]{2, 1, 3}, 3));

			pb.getSolver().setValSelector(new RandomIntValSelector((long) seed));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, (long) seed + 1002));//, t2, t3}));
			pb.solveAll();
			//Solver.flushLogs();
			//System.out.println(seed + " nbSol " + pb.getSolver().getNbSolutions());
			assertEquals(2, pb.getSolver().getNbSolutions());
		}
	}

	public static void _test2() { // nb solutions 208
		for (int seed = 0; seed < 20; seed++) {
			AbstractProblem pb = new Problem();
			IntDomainVar d1 = pb.makeBoundIntVar("d1", 4, 10);
			IntDomainVar d2 = pb.makeBoundIntVar("d2", 3, 3);
			IntDomainVar d3 = pb.makeBoundIntVar("d3", 1, 1);
			IntDomainVar d4 = pb.makeBoundIntVar("d4", 8, 12);
			IntDomainVar d5 = pb.makeBoundIntVar("d5", 2, 2);
			IntDomainVar[] durations = new IntDomainVar[]{d1, d2, d3, d4, d5};
			IntDomainVar[] starts = new IntDomainVar[5];
			IntDomainVar[] ends = new IntDomainVar[5];
			for (int i = 0; i < 5; i++) {
				starts[i] = pb.makeBoundIntVar("s" + i, 0, 11);
				ends[i] = pb.makeBoundIntVar("e" + i, 0, 11);
			}

			pb.post(pb.cumulative(starts, ends, durations, new int[]{2, 2, 3, 1, 4}, 4));

			pb.getSolver().setValSelector(new RandomIntValSelector((long) seed));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, (long) seed + 1002));//, t2, t3}));
			pb.solveAll();
			assertEquals(208, pb.getSolver().getNbSolutions());
			System.out.println(seed + " nbSol " + pb.getSolver().getNbSolutions() + " Node " + ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot());
		}
	}

	/**
	 * Trivial exemple for edge finding
	 */
	public static void _test3() { // nb solutions : 2
		for (int seed = 0; seed < 20; seed++) {
			AbstractProblem pb = new Problem();
			int nbtask = 3;
			IntDomainVar d1 = pb.makeBoundIntVar("d0", 5, 5);
			IntDomainVar d2 = pb.makeBoundIntVar("d1", 3, 3);
			IntDomainVar d3 = pb.makeBoundIntVar("d2", 2, 2);
			IntDomainVar[] durations = new IntDomainVar[]{d1, d2, d3};
			IntDomainVar[] starts = new IntDomainVar[nbtask];
			IntDomainVar[] ends = new IntDomainVar[nbtask];
			for (int i = 0; i < nbtask; i++) {
				starts[i] = pb.makeBoundIntVar("s" + i, 0, 3);
				ends[i] = pb.makeBoundIntVar("e" + i, 0, 7);
			}
			Solver.setVerbosity(Solver.SEARCH);
			pb.post(pb.cumulative(starts, ends, durations, new int[]{2, 2, 3}, 4));

			pb.getSolver().setValSelector(new RandomIntValSelector((long) seed));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, (long) seed + 1002));//, t2, t3}));
			pb.solveAll();
			Solver.flushLogs();
			assertEquals(2, pb.getSolver().getNbSolutions());
			System.out.println(seed + " nbSol " + pb.getSolver().getNbSolutions() + " Node " + ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot());
		}
	}

	/**
	 * Trivial example where edgefinding shouldn't fail !
	 * example send to Luc to highlhight the problem in algorithm CalcR !
	 */
	public static void _test4() {
		AbstractProblem pb = new Problem();
		int nbtask = 2;
		IntDomainVar d1 = pb.makeBoundIntVar("d0", 3, 3);
		IntDomainVar d2 = pb.makeBoundIntVar("d1", 2, 2);
		IntDomainVar[] durations = new IntDomainVar[]{d1, d2};
		IntDomainVar[] starts = new IntDomainVar[nbtask];
		IntDomainVar[] ends = new IntDomainVar[nbtask];
		starts[0] = pb.makeBoundIntVar("s0", 3, 3);
		ends[0] = pb.makeBoundIntVar("e0", 6, 6);
		starts[1] = pb.makeBoundIntVar("s1", 0, 0);
		ends[1] = pb.makeBoundIntVar("e1", 2, 2);

		pb.post(pb.cumulative(starts, ends, durations, new int[]{2, 3}, 4));
		try {
			pb.propagate();
		} catch (ContradictionException e) {
			assertTrue(false);
		}
	}

	// nb solutions = 510
	public static void _test5() {
		int totnbnode = 0;
		int echt = 20;
		long tps = System.currentTimeMillis();
		for (int seed = 0; seed < echt; seed++) {
			AbstractProblem pb = new Problem();
			IntDomainVar[] durations = new IntDomainVar[5];
			IntDomainVar[] starts = new IntDomainVar[5];
			IntDomainVar[] ends = new IntDomainVar[5];
			IntDomainVar[] heigths = new IntDomainVar[5];
			for (int i = 0; i < 5; i++) {
				durations[i] = pb.makeBoundIntVar("d", 2, 2);
				heigths[i] = pb.makeBoundIntVar("h", 2, 4);
				starts[i] = pb.makeBoundIntVar("s" + i, 0, 6);
				ends[i] = pb.makeBoundIntVar("e" + i, 0, 6);
			}

			pb.post(pb.cumulative(starts, ends, durations, heigths, 4));

			pb.getSolver().setValSelector(new RandomIntValSelector((long) seed));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, (long) seed + 1002));//, t2, t3}));
			pb.solveAll();
			System.out.println(seed + " nbSol " + pb.getSolver().getNbSolutions() + " " + pb.getSolver().getSearchSolver().getNodeCount());
			totnbnode += pb.getSolver().getSearchSolver().getNodeCount();
			assertEquals(510, pb.getSolver().getNbSolutions());
			//System.out.println("Node " + ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot());
		}
		System.out.println("nbAvgNode: " + (double) totnbnode / (double) echt + " tottime: " + (System.currentTimeMillis() - tps));

	}

	public static void testEnd6() { // nb solutions = 48
		int n = 3;
		AbstractProblem pb = new Problem();
		IntDomainVar[] durations = new IntDomainVar[n];
		IntDomainVar[] starts = new IntDomainVar[n];
		IntDomainVar[] ends = new IntDomainVar[n];
		IntDomainVar[] heigths = new IntDomainVar[n];
		for (int i = 0; i < n; i++) {
			durations[i] = pb.makeBoundIntVar("d", 2, 2);
			heigths[i] = pb.makeBoundIntVar("h", 3, 4);   // 3 a la place de 4
			starts[i] = pb.makeBoundIntVar("s" + i, 0, 6);
			ends[i] = pb.makeBoundIntVar("e" + i, 0, 6);
		}
		SchedulingSettings.setCumulativeEdgeFinding();
		Solver.setVerbosity(Solver.PROPAGATION);
		pb.post(pb.cumulative(starts, ends, durations, heigths, 4));

		try {
			starts[0].setVal(0);
			starts[1].setVal(4);
			starts[2].setVal(2);
			heigths[2].setVal(4);
			pb.propagate();
		} catch (ContradictionException e) {

			Solver.flushLogs();
			assertTrue(false);
		}
		Solver.flushLogs();

	}

	public static void _test6() { // nb solutions = 48
		for (int seed = 0; seed < 20; seed++) {
			int n = 3;
			AbstractProblem pb = new Problem();
			IntDomainVar[] durations = new IntDomainVar[n];
			IntDomainVar[] starts = new IntDomainVar[n];
			IntDomainVar[] ends = new IntDomainVar[n];
			IntDomainVar[] heigths = new IntDomainVar[n];
			for (int i = 0; i < n; i++) {
				durations[i] = pb.makeBoundIntVar("d", 2, 2);
				heigths[i] = pb.makeBoundIntVar("h", 3, 4);   // 3 a la place de 4
				starts[i] = pb.makeBoundIntVar("s" + i, 0, 6);
				ends[i] = pb.makeBoundIntVar("e" + i, 0, 6);
			}
			//Solver.setVerbosity(Solver.PROPAGATION);
			pb.post(pb.cumulative(starts, ends, durations, heigths, 4));

			pb.getSolver().setValSelector(new RandomIntValSelector((long) seed));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, (long) seed + 1002));//, t2, t3}));
			pb.solveAll();
			assertEquals(pb.getSolver().getNbSolutions(), 48);
		}
	}

	// 0 nodes with taskIntervals, 836 otherwise
	public static void test7TaskInterval() {
		for (int seed = 0; seed < 10; seed++) {
			try {
				int n = 10;
				AbstractProblem pb = new Problem();
				IntDomainVar[] durations = new IntDomainVar[n];
				IntDomainVar[] starts = new IntDomainVar[n];
				IntDomainVar[] ends = new IntDomainVar[n];
				int[] h = new int[n];
				for (int i = 0; i < n; i++) {
					durations[i] = pb.makeEnumIntVar("t" + i, 2, 2);
					starts[i] = pb.makeEnumIntVar("t" + i, 0, 4);
					ends[i] = pb.makeEnumIntVar("t" + i, 0, 4);
					h[i] = 1;
				}
				SchedulingSettings.setCumulativeTaskInterval();
				pb.post(pb.cumulative(starts, ends, durations, h, 4));
				pb.getSolver().setValSelector(new RandomIntValSelector((long) seed));
				pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, (long) seed + 1002));

				pb.solveAll();
				assertEquals(0, pb.getSolver().getNbSolutions());
				assertEquals(0, pb.getSolver().getSearchSolver().getNodeCount());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
