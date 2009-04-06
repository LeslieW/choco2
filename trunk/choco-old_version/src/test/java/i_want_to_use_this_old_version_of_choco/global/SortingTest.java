package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 4 juin 2007
 * Time: 16:45:46
 * To change this template use File | Settings | File Templates.
 */
public class SortingTest extends TestCase {

    public static void testSorting() {
        Problem p = new Problem();
        IntDomainVar[] x = {
                p.makeEnumIntVar("x0", 1, 16),
                p.makeEnumIntVar("x1", 5, 10),
                p.makeEnumIntVar("x2", 7, 9),
                p.makeEnumIntVar("x3", 12, 15),
                p.makeEnumIntVar("x4", 1, 13)
        };
        IntDomainVar[] y = {
                p.makeEnumIntVar("y0", 2, 3),
                p.makeEnumIntVar("y1", 6, 7),
                p.makeEnumIntVar("y2", 8, 11),
                p.makeEnumIntVar("y3", 13, 16),
                p.makeEnumIntVar("y4", 14, 18)
        };
        SortingConstraint c = new SortingConstraint(x, y);
        try {
            c.boundConsistency();
        }
        catch (ContradictionException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public static void testSorting2() {
        for (int seed = 0; seed < 10; seed++) {
            Problem p = new Problem();
            int n = 4;
            IntDomainVar[] x = p.makeEnumIntVarArray("x", n, 0, 6);
            IntDomainVar[] y = p.makeEnumIntVarArray("y", n, 0, 6);
          Constraint c = new SortingConstraint(x, y);
            p.post(c);
            p.post(p.allDifferent(x));

            p.getSolver().setValSelector(new RandomIntValSelector(seed));
            p.getSolver().setVarSelector(new RandomIntVarSelector(p, seed + 2));
            p.solve();
            do {
              //System.out.println(c.pretty());
                /*for (int i = 0; i < x.length; i++) {
                    System.out.print(" " + x[i].getVal());
                }
                System.out.print(" -- ");
                for (int i = 0; i < y.length; i++) {
                    System.out.print(" " + y[i].getVal());
                }
                System.out.println(""); */
            } while (p.nextSolution() == Boolean.TRUE);
            System.out.println("Sorting nb solutions " + p.getSolver().getNbSolutions());
            assertEquals(840, p.getSolver().getNbSolutions());
        }

    }

}
