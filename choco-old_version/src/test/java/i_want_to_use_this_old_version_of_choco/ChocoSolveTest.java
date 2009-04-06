package i_want_to_use_this_old_version_of_choco;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: Jul 20, 2003
 * Time: 5:23:39 PM
 * To change this template use Options | File Templates.
 */
public class ChocoSolveTest extends TestCase {
  private Problem pb;
  private IntDomainVar x;
  private IntDomainVar y;
  private IntDomainVar z;
  private Propagator A;
  private Propagator B;

  public void setUp() {
    pb = new Problem();
    x = pb.makeBoundIntVar("X", 1, 5);
    y = pb.makeBoundIntVar("Y", 1, 5);
    z = pb.makeBoundIntVar("Z", 1, 5);
    A = (Propagator) pb.geq(x, pb.plus(y, 1));
    B = (Propagator) pb.geq(y, pb.plus(z, 1));
  }

  public void tearDown() {
    A = null;
    B = null;
    x = null;
    y = null;
    z = null;
    pb = null;
  }

  public void testArithmetic() {

    pb.post(A);
    pb.post(B);
    A.setPassive();
    A.setActive();
    try {
      pb.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }
    System.out.println("X : " + x.getInf() + " - > " + x.getSup());
    System.out.println("Y : " + y.getInf() + " - > " + y.getSup());
    System.out.println("Z : " + z.getInf() + " - > " + z.getSup());
    assertEquals(3, x.getInf());
    assertEquals(5, x.getSup());
    assertEquals(2, y.getInf());
    assertEquals(4, y.getSup());
    assertEquals(1, z.getInf());
    assertEquals(3, z.getSup());

    try {
      z.setVal(2);
      pb.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }
    System.out.println("X : " + x.getInf() + " - > " + x.getSup());
    assertEquals(4, x.getInf());
    assertEquals(5, x.getSup());
    System.out.println("Y : " + y.getInf() + " - > " + y.getSup());
    assertEquals(3, y.getInf());
    assertEquals(4, y.getSup());
    System.out.println("Z : " + z.getInf() + " - > " + z.getSup());
    assertEquals(2, z.getInf());
    assertEquals(2, z.getSup());

    try {
      x.setSup(4);
      pb.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }
    System.out.println("X : " + x.getInf() + " - > " + x.getSup());
    assertEquals(4, x.getInf());
    assertEquals(4, x.getSup());
    System.out.println("Y : " + y.getInf() + " - > " + y.getSup());
    assertEquals(3, y.getInf());
    assertEquals(3, y.getSup());
    System.out.println("Z : " + z.getInf() + " - > " + z.getSup());
    assertEquals(2, z.getInf());
    assertEquals(2, z.getSup());
  }

  public void testArithmetic2() {
    pb.post(A);
    pb.post(B);
    A.setPassive();
    try {
      pb.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }
    System.out.println("X : " + x.getInf() + " - > " + x.getSup());
    assertEquals(1, x.getInf());
    assertEquals(5, x.getSup());
    System.out.println("Y : " + y.getInf() + " - > " + y.getSup());
    assertEquals(2, y.getInf());
    assertEquals(5, y.getSup());
    System.out.println("Z : " + z.getInf() + " - > " + z.getSup());
    assertEquals(1, z.getInf());
    assertEquals(4, z.getSup());
  }
}
