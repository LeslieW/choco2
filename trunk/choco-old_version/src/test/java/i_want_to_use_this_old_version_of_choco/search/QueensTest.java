package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import junit.framework.TestCase;

import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.search.QueensTest.java, last modified by flaburthe 12 janv. 2004 18:03:29 */

/**
 * A currentElement placing n-queens on a chessboard, so that no two attack each other
 */
public class QueensTest extends TestCase {
  public static int nbQueensSolution[] = {0, 0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};
  public static final boolean LINKED = false;
  private Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar[] queens;

  protected void setUp() {
    logger.fine("Queens Testing...");
    pb = new Problem();
  }

  protected void tearDown() {
    pb = null;
    queens = null;
  }

  public IntDomainVar createVar(String name, int min, int max) {
    if (LINKED) return pb.makeIntVar(name, IntDomainVar.LINKEDLIST, min, max);
    return pb.makeEnumIntVar(name, min, max);
  }

  private void queen0(int n) {
    logger.finer("n queens, binary model, n=" + n);
    // create variables
    queens = new IntDomainVar[n];
    for (int i = 0; i < n; i++) {
      queens[i] = createVar("Q" + i, 1, n);
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
    pb.solve(true);
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
    queen0(4);
  }

  public void test1() {
    queen0(5);
  }

  public void test2() {
    queen0(6);
  }

  public void test3() {
    queen0(7);
  }

  public void test4() {
    queen0(8);
  }

  public void test5() {
    queen0(9);
  }

  public void notest6() {
    queen0(10);
  }
  //  public void test7() { queen0(11); }

}