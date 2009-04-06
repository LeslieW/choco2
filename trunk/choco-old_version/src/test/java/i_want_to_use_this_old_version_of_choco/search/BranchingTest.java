package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.integer.IntVar;
import i_want_to_use_this_old_version_of_choco.integer.search.DomOverDeg;
import i_want_to_use_this_old_version_of_choco.integer.search.MostConstrained;
import junit.framework.TestCase;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 23 sept. 2005
 * Time: 21:08:43
 * To change this template use File | Settings | File Templates.
 */
public class BranchingTest extends TestCase {
  public static int nbQueensSolution[] = {0, 0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};
  private Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntVar[] queens;

  protected void setUp() {
    logger.fine("Queens Testing...");
    pb = new Problem();
  }

  protected void tearDown() {
    pb = null;
    queens = null;
  }

  private void queen0(int n, int branching) {
    logger.finer("n queens, binary model, n=" + n);
    // create variables
    queens = new IntVar[n];
    for (int i = 0; i < n; i++) {
      queens[i] = pb.makeEnumIntVar("Q" + i, 1, n);
    }
    // diagonal constraints
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        int k = j - i;
        pb.post(pb.neq(queens[i], queens[j]));
        pb.post(pb.neq(queens[i], pb.plus(queens[j], k)));
        pb.post(pb.neq(queens[i], pb.minus(queens[j], k)));
      }
    }
    Solver s = pb.getSolver();
    switch (branching) {
      case 0:
        s.setVarSelector(new MostConstrained(pb));
        break;
      case 1:
        s.setVarSelector(new DomOverDeg(pb));
        break;
    }

    pb.solveAll();
    if (n >= 4) {
      if (n <= 13) {
        assertEquals(Boolean.TRUE, pb.isFeasible());
        assertEquals(nbQueensSolution[n], s.getNbSolutions());
      }
    } else {
      assertEquals(Boolean.FALSE, pb.isFeasible());
    }
  }

  public void test0() {
    queen0(4, 0);
  }

  public void test1() {
    queen0(4, 1);
  }

  public void test2() {
    queen0(5, 0);
  }

  public void test3() {
    queen0(5, 1);
  }

  public void test4() {
    queen0(6, 0);
  }

  public void test5() {
    queen0(6, 1);
  }

  public void test6() {
    queen0(7, 0);
  }

  public void test7() {
    queen0(7, 1);
  }

  public void test8() {
    queen0(10, 0);
  }

  public void test9() {
    queen0(10, 1);
  }
}
