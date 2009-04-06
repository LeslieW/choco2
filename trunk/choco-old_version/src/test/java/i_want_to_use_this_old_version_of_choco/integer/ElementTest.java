package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Narendra Jussien
 * Date: 18 mai 2005
 * Time: 16:29:42
 * To change this template use File | Settings | File Templates.
 */
public class ElementTest extends TestCase {
    public void test1() {
        Problem pb = new Problem();
        int[] values = new int[]{1, 2, 0, 4, 3};
        IntDomainVar index = pb.makeEnumIntVar("index", -3, 10);
        IntDomainVar var = pb.makeEnumIntVar("value", -20, 20);
        pb.post(pb.nth(index, values, var));

        pb.solve();
        do {
            System.out.println("index = " + index.getVal());
            System.out.println("value = " + var.getVal());
            assertEquals(var.getVal(), values[index.getVal()]);
        } while (pb.nextSolution().booleanValue());

        assertEquals(5, pb.getSolver().getNbSolutions());
    }

    public void test2() {
        Problem pb = new Problem();
        int[] values = new int[]{1, 2, 0, 4, 3};
        IntDomainVar index = pb.makeEnumIntVar("index", 2, 10);
        IntDomainVar var = pb.makeEnumIntVar("value", -20, 20);
        pb.post(pb.nth(index, values, var));

        pb.solve();
        do {
            System.out.println("index = " + index.getVal());
            System.out.println("value = " + var.getVal());
            assertEquals(var.getVal(), values[index.getVal()]);
        } while (pb.nextSolution().booleanValue());

        assertEquals(3, pb.getSolver().getNbSolutions());
    }

    public void test3() {
        Problem pb = new Problem();
        IntDomainVar X = pb.makeEnumIntVar("X", 0, 5);
        IntDomainVar Y = pb.makeEnumIntVar("Y", 3, 7);
        IntDomainVar Z = pb.makeEnumIntVar("Z", 5, 8);
        IntDomainVar I = pb.makeEnumIntVar("index", -5, 12);
        IntDomainVar V = pb.makeEnumIntVar("V", -3, 20);
        pb.post(pb.nth(I, new IntDomainVar[]{X, Y, Z}, V));
        try {
            pb.propagate();
            assertEquals(I.getInf(), 0);
            assertEquals(I.getSup(), 2);
            assertEquals(V.getInf(), 0);
            assertEquals(V.getSup(), 8);
            V.setSup(5);
            Z.setInf(6);
            pb.propagate();
            assertEquals(I.getSup(), 1);
            Y.remVal(4);
            Y.remVal(5);
            V.remVal(3);
            pb.propagate();
            assertTrue(I.isInstantiatedTo(0));
            V.setSup(2);
            V.remVal(1);
            pb.propagate();
            assertEquals(X.getSup(), 2);
            assertFalse(X.canBeInstantiatedTo(1));
        } catch (ContradictionException e) {
            assertFalse(true);
        }
    }

    public void test4() {
        Problem pb = new Problem();
        IntDomainVar V = pb.makeEnumIntVar("V", 0, 20);
        IntDomainVar X = pb.makeEnumIntVar("X", 10, 50);
        IntDomainVar Y = pb.makeEnumIntVar("Y", 0, 1000);
        IntDomainVar I = pb.makeEnumIntVar("I", 0, 1);
        pb.post(pb.nth(I, new IntDomainVar[]{X, Y}, V));
        try {
            pb.propagate();
            assertFalse(I.isInstantiated());
            Y.setInf(30);
            pb.propagate();
            assertTrue(I.isInstantiatedTo(0));
            assertEquals(V.getInf(), X.getInf());
            assertEquals(V.getSup(), X.getSup());
        } catch (ContradictionException e) {
            assertFalse(true);
        }
    }

    /**
     * testing the initial propagation and the behavior when the index variable is instantiated
     */
    public void test5() {
        Problem pb = new Problem();
        int n = 2;
        IntDomainVar[] vars = new IntDomainVar[n];
        for (int idx = 0; idx < n; idx++) {
            vars[idx] = pb.makeEnumIntVar("t" + idx, 0 + 3 * idx, 2 + 3 * idx);
        }
        IntDomainVar index = pb.makeEnumIntVar("index", -3, 15);
        IntDomainVar var = pb.makeEnumIntVar("value", -25, 20);

        pb.post(pb.nth(index, vars, var));

        try {
            pb.propagate();
        } catch (ContradictionException e) {
            assertFalse(true);
        }
        assertEquals(0, var.getInf());
        assertEquals(3 * n - 1, var.getSup());
        assertEquals(var.getDomainSize(), 3 * n);
        assertEquals(0, index.getInf());
        assertEquals(n - 1, index.getSup());
        assertEquals(index.getDomainSize(), n);

        assertEquals(0, vars[0].getInf());
        assertEquals(2, vars[0].getSup());
        assertEquals(vars[0].getDomainSize(), 3);
        assertEquals(3, vars[1].getInf());
        assertEquals(5, vars[1].getSup());
        assertEquals(vars[1].getDomainSize(), 3);

        try {
            index.setVal(1);
            pb.propagate();
            assertEquals(3, var.getInf());
            assertEquals(5, var.getSup());
            assertEquals(0, vars[0].getInf());
            assertEquals(2, vars[0].getSup());
            assertEquals(3, vars[1].getInf());
            assertEquals(5, vars[1].getSup());
            vars[0].setVal(0);
            pb.propagate();
        } catch (ContradictionException e) {
            assertFalse(true);
        }
    }

    /**
     * same as test5, but counting the number of solutions
     */
    public void test6() {
        subtest6(2);
        subtest6(3);
        subtest6(4);
    }

    private void subtest6(int n) {
        Problem pb = new Problem();
        IntDomainVar[] vars = new IntDomainVar[n];
        for (int idx = 0; idx < n; idx++) {
            vars[idx] = pb.makeEnumIntVar("t" + idx, 0 + 3 * idx, 2 + 3 * idx);
        }
        IntDomainVar index = pb.makeEnumIntVar("index", -3, n + 15);
        IntDomainVar var = pb.makeEnumIntVar("value", -25, 4 * n + 20);

        pb.post(pb.nth(index, vars, var));

        try {
            pb.propagate();
        } catch (ContradictionException e) {
            assertFalse(true);
        }
        assertEquals(0, var.getInf());
        assertEquals(3 * n - 1, var.getSup());
        assertEquals(var.getDomainSize(), 3 * n);
        assertEquals(0, index.getInf());
        assertEquals(n - 1, index.getSup());
        assertEquals(index.getDomainSize(), n);
        for (int i = 0; i < n; i++) {
            assertEquals(3 * i, vars[i].getInf());
            assertEquals(2 + 3 * i, vars[i].getSup());
            assertEquals(vars[i].getDomainSize(), 3);
        }
        Solver.setVerbosity(Solver.SEARCH);
        pb.getSolver().setLoggingMaxDepth(2);
        pb.solveAll();
        Solver.flushLogs();
        assertEquals(Math.round(n * Math.pow(3, n)), pb.getSolver().getNbSolutions());

    }

    public static void testElement1() {
        for (int i = 0; i < 10; i++) {
            Problem pb = new Problem();
            int[][] values = new int[][]{
                    {1, 2, 0, 4, -323},
                    {2, 1, 0, 3, 42},
                    {6, 1, -7, 4, -40},
                    {-1, 0, 6, 2, -33},
                    {2, 3, 0, -1, 49}};
            IntDomainVar index1 = pb.makeEnumIntVar("index1", -3, 10);
            IntDomainVar index2 = pb.makeEnumIntVar("index2", -3, 10);
            IntDomainVar var = pb.makeEnumIntVar("value", -20, 20);
          Constraint c = pb.nth(index1, index2, values, var);
          System.out.println("posted constraint = " + c.pretty());
            pb.post(c);
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
            pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));

            pb.solveAll();
            assertEquals(pb.getSolver().getNbSolutions(), 20);
        }
    }

    public static void testElement2() {
        for (int i = 0; i < 10; i++) {
            Problem pb = new Problem();
            int[][] values = new int[][]{
                    {1, 2, 0, 4, 3},
                    {2, 1, 0, 3, 3},
                    {6, 1, -7, 4, -4},
                    {-1, 0, 6, 2, -33},
                    {2, -3, 0, -1, 4}};
            IntDomainVar index1 = pb.makeEnumIntVar("index1", 2, 10);
            IntDomainVar index2 = pb.makeEnumIntVar("index2", -3, 2);
            IntDomainVar var = pb.makeEnumIntVar("value", -20, 20);
            pb.post(pb.nth(index1, index2, values, var));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
            pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
            pb.solveAll();
            assertEquals(pb.getSolver().getNbSolutions(), 9);
        }
    }

    public void testNthG() {
        for (int i = 0; i < 100; i++) {
            Problem pb = new Problem();
            IntDomainVar X = pb.makeEnumIntVar("X", 0, 5);
            IntDomainVar Y = pb.makeEnumIntVar("Y", 3, 7);
            IntDomainVar Z = pb.makeEnumIntVar("Z", 5, 8);
            IntDomainVar I = pb.makeEnumIntVar("index", -5, 12);
            IntDomainVar V = pb.makeEnumIntVar("V", -3, 20);
            pb.post(pb.nth(I, new IntDomainVar[]{X, Y, Z}, V));
            pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, i));
            pb.getSolver().setValSelector(new RandomIntValSelector(i + 1));
            pb.solveAll();
            int nbSol = pb.getSolver().getNbSolutions();
            //System.out.println("nbsol " + nbSol);
            assertEquals(nbSol,360);
        }

    }
}
