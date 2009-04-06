package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Problem;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 13 oct. 2005
 * Time: 12:37:51
 * To change this template use File | Settings | File Templates.
 */
public class ChannelingTest extends TestCase {

  public void test1() {
    Problem pb = new Problem();
    IntVar y01 = pb.makeEnumIntVar("y01", 0, 1);
    IntVar x1 = pb.makeEnumIntVar("x1", 0, 5);
    Constraint A = pb.boolChanneling(y01, x1, 4);

    pb.post(A);

    pb.solveAll();
    assertEquals(6, pb.getSolver().getNbSolutions());
  }

  public void test2() {
    Problem pb = new Problem();
    IntDomainVar x1 = pb.makeEnumIntVar("x1", 0, 5);
    IntDomainVar x2 = pb.makeEnumIntVar("x2", 0, 5);
    IntDomainVar y1 = pb.makeEnumIntVar("y1", 0, 1);
    IntDomainVar y2 = pb.makeEnumIntVar("y2", 0, 1);
    IntDomainVar z = pb.makeEnumIntVar("z", 0, 5);
    pb.post(pb.boolChanneling(y1, x1, 4));
    pb.post(pb.boolChanneling(y2, x2, 1));
    pb.post(pb.eq(pb.plus(y1, y2), z));
    pb.maximize(z, false);
    System.out.println(x1.getVal() + " " + x2.getVal() + " " + z.getVal());
    assertEquals(4, x1.getVal());
    assertEquals(1, x2.getVal());
    assertEquals(2, z.getVal());
  }

  public void test3() {
    int n = 5;
    Problem pb = new Problem();
    IntDomainVar[] x = new IntDomainVar[n];
    IntDomainVar[] y = new IntDomainVar[n];
    for (int i = 0; i < n; i++) {
      x[i] = pb.makeEnumIntVar("x" + i, 0, n - 1);
      y[i] = pb.makeEnumIntVar("y" + i, 0, n - 1);
    }
    pb.post(pb.inverseChanneling(x, y));
    pb.solve();
    do {
      for (int i = 0; i < n; i++) {
        //System.out.println("" + x[i] + ":" + x[i].getVal() + " <=> " + y[x[i].getVal()] + ":" + y[x[i].getVal()].getVal());
        assertTrue(y[x[i].getVal()].getVal() == i);
      }
    } while (pb.nextSolution() == Boolean.TRUE);
    assertEquals(120, pb.getSolver().getNbSolutions());
  }

  public void test4() {
    int n = 5;
    int lb = 7;
    Problem pb = new Problem();
    IntDomainVar[] x = new IntDomainVar[n];
    IntDomainVar[] y = new IntDomainVar[n];
    for (int i = 0; i < n; i++) {
      x[i] = pb.makeEnumIntVar("x" + i, lb, lb + n - 1);
      y[i] = pb.makeEnumIntVar("y" + i, lb, lb + n - 1);
    }
    pb.post(pb.inverseChanneling(x, y));
    pb.solve();
    do {
      for (int i = 0; i < n; i++) {
        //System.out.println("" + x[i] + ":" + x[i].getVal() + " <=> " + y[x[i].getVal() - lb] + ":" + y[x[i].getVal() - lb].getVal());
        assertTrue(y[x[i].getVal() - lb].getVal() == (i + lb));
      }
    } while (pb.nextSolution() == Boolean.TRUE);
    assertEquals(120, pb.getSolver().getNbSolutions());
  }

}
