package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.IntVar;
import i_want_to_use_this_old_version_of_choco.search.NodeLimit;
import junit.framework.TestCase;

/**
 * Tests for the AllDifferent constraint.
 */
public class AllDifferentTest extends TestCase {
    public void testDummy() {
        System.out.println("Dummy AllDifferent currentElement...");
        Problem pb = new Problem();
        IntDomainVar a = pb.makeEnumIntVar("a", 1, 2);
        IntDomainVar b = pb.makeEnumIntVar("b", 1, 2);
        IntDomainVar c = pb.makeEnumIntVar("c", 1, 4);
        IntDomainVar d = pb.makeEnumIntVar("d", 1, 4);
        IntDomainVar[] vars = new IntDomainVar[]{a, b, c, d};
        Constraint alldiff = pb.allDifferent(vars);

        pb.post(alldiff);
        try {
            pb.propagate();
        } catch (ContradictionException e) {
            assertTrue(false);
        }
        assertEquals(c.getInf(), 3);
    }

    public void testNQueen() {
        int n = 8;
        Problem pb = new Problem();
        IntVar[] queens = new IntVar[n];
        IntVar[] diag1 = new IntVar[n];
        IntVar[] diag2 = new IntVar[n];
        for (int i = 0; i < n; i++) {
            queens[i] = pb.makeEnumIntVar("Q" + i, 1, n);
            diag1[i] = pb.makeEnumIntVar("D1" + i, 1, 2 * n);
            diag2[i] = pb.makeEnumIntVar("D2" + i, -n + 1, n);
        }

        pb.post(pb.allDifferent(queens));
        for (int i = 0; i < n; i++) {
            pb.post(pb.eq(diag1[i], pb.plus(queens[i], i)));
            pb.post(pb.eq(diag2[i], pb.minus(queens[i], i)));
        }
        pb.post(pb.allDifferent(diag1,false));
        pb.post(pb.allDifferent(diag2,false));

        // diagonal constraints
        Solver s = pb.getSolver();
        //s.setTimeLimit(30000);
        long tps = System.currentTimeMillis();
        pb.solveAll();
        System.out.println("tps nreines1 " + (System.currentTimeMillis() - tps) + " nbNode " + ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot());
        assertEquals(92,pb.getSolver().getNbSolutions());
    }

     public void testNQueen2() {
        int n = 8;
        Problem pb = new Problem();
        IntVar[] queens = new IntVar[n];
        IntVar[] diag1 = new IntVar[n];
        IntVar[] diag2 = new IntVar[n];
        for (int i = 0; i < n; i++) {
            queens[i] = pb.makeEnumIntVar("Q" + i, 1, n);
            diag1[i] = pb.makeEnumIntVar("D1" + i, 1, 2 * n);
            diag2[i] = pb.makeEnumIntVar("D2" + i, -n + 1, n);
        }

        pb.post(pb.allDifferent(queens));
        for (int i = 0; i < n; i++) {
            pb.post(pb.eq(diag1[i], pb.plus(queens[i], i)));
            pb.post(pb.eq(diag2[i], pb.minus(queens[i], i)));
        }
        pb.post(pb.allDifferent(diag1));
        pb.post(pb.allDifferent(diag2));

        // diagonal constraints
        Solver s = pb.getSolver();
        //s.setTimeLimit(30000);
        long tps = System.currentTimeMillis();
        pb.solveAll();
        System.out.println("tps nreines2 " + (System.currentTimeMillis() - tps) + " nbNode " + ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot());
        assertEquals(92,pb.getSolver().getNbSolutions());
    }

    public void testNQueen3() {
        int n = 8;
        Problem pb = new Problem();
        IntVar[] queens = new IntVar[n];
        IntVar[] diag1 = new IntVar[n];
        IntVar[] diag2 = new IntVar[n];
        for (int i = 0; i < n; i++) {
            queens[i] = pb.makeBoundIntVar("Q" + i, 1, n);
            diag1[i] = pb.makeBoundIntVar("D1" + i, 1, 2 * n);
            diag2[i] = pb.makeBoundIntVar("D2" + i, -n + 1, n);
        }

        pb.post(pb.allDifferent(queens));
        for (int i = 0; i < n; i++) {
            pb.post(pb.eq(diag1[i], pb.plus(queens[i], i)));
            pb.post(pb.eq(diag2[i], pb.minus(queens[i], i)));
        }
        pb.post(pb.allDifferent(diag1));
        pb.post(pb.allDifferent(diag2));

        // diagonal constraints
        Solver s = pb.getSolver();
        //s.setTimeLimit(30000);
        long tps = System.currentTimeMillis();
        pb.solveAll();
        System.out.println("tps nreines3 " + (System.currentTimeMillis() - tps) + " nbNode " + ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot());
        assertEquals(92,pb.getSolver().getNbSolutions());
    }

    public void testLatinSquare() {
        System.out.println("Latin Square Test...");
        // Toutes les solutions de n=5 en 90 sec  (161280 solutions)
        final int n = 4;
        final int[] soluces = new int[]{1, 2, 12, 576, 161280};

        // Problem
        Problem myPb = new Problem();

        // Variables
        IntDomainVar[] vars = new IntDomainVar[n * n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                vars[i * n + j] = myPb.makeEnumIntVar("C" + i + "_" + j, 1, n);
            }

        // Constraints
        for (int i = 0; i < n; i++) {
            IntDomainVar[] row = new IntDomainVar[n];
            IntDomainVar[] col = new IntDomainVar[n];
            for (int x = 0; x < n; x++) {
                row[x] = vars[i * n + x];
                col[x] = vars[x * n + i];
            }
            myPb.post(myPb.allDifferent(row));
            myPb.post(myPb.allDifferent(col));
        }

        myPb.solve(true);

        assertEquals(soluces[n - 1], myPb.getSolver().getNbSolutions());
        System.out.println("LatinSquare Solutions : " + myPb.getSolver().getNbSolutions());
    }

    public void testLatinSquare2() {
        System.out.println("Latin Square Test...");
        // Toutes les solutions de n=5 en 90 sec  (161280 solutions)
        final int n = 4;
        final int[] soluces = new int[]{1, 2, 12, 576, 161280};

        // Problem
        Problem myPb = new Problem();

        // Variables
        IntVar[] vars = new IntVar[n * n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                vars[i * n + j] = myPb.makeBoundIntVar("C" + i + "_" + j, 1, n);
            }

        // Constraints
        for (int i = 0; i < n; i++) {
            IntVar[] row = new IntVar[n];
            IntVar[] col = new IntVar[n];
            for (int x = 0; x < n; x++) {
                row[x] = vars[i * n + x];
                col[x] = vars[x * n + i];
            }
            myPb.post(myPb.allDifferent(row));
            myPb.post(myPb.allDifferent(col));
        }

        myPb.solve(true);

        assertEquals(soluces[n - 1], myPb.getSolver().getNbSolutions());
        System.out.println("LatinSquare Solutions : " + myPb.getSolver().getNbSolutions());
    }
}
