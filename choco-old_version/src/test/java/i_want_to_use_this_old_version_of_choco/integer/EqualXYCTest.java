/*
 * EqualXYCTest.java
 *
 */

package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.constraints.EqualXYC;
import junit.framework.TestCase;

import java.util.logging.Logger;

/**
 * a class implementing tests for backtrackable search
 */
public class EqualXYCTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar x;
  private IntDomainVar y;
  private IntDomainVar z;
  private Constraint c1;
  private Constraint c2;

  protected void setUp() {
    logger.fine("EqualXYC Testing...");
    pb = new Problem();
    x = pb.makeBoundIntVar("X", 1, 5);
    y = pb.makeBoundIntVar("Y", 1, 5);
    z = pb.makeBoundIntVar("Z", 1, 5);
    c1 = new EqualXYC(x, y, 2);
    c2 = new EqualXYC(y, z, 1);
  }

  protected void tearDown() {
    c1 = null;
    c2 = null;
    x = null;
    y = null;
    z = null;
    pb = null;
  }

  public void test1() {
    logger.finer("test1");
    pb.post(c1);
    pb.post(c2);

    try {
      pb.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }
    logger.finest("X : " + x.getInf() + " - > " + x.getSup());
    logger.finest("Y : " + y.getInf() + " - > " + y.getSup());
    logger.finest("Z : " + z.getInf() + " - > " + z.getSup());
    assertEquals(4, x.getInf());
    assertEquals(5, x.getSup());
    assertEquals(2, y.getInf());
    assertEquals(3, y.getSup());
    assertEquals(1, z.getInf());
    assertEquals(2, z.getSup());
    logger.finest("domains OK after first propagate");

    try {
      z.setInf(2);
      pb.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }

    assertTrue(x.isInstantiated());
    assertTrue(y.isInstantiated());
    assertTrue(z.isInstantiated());
    assertEquals(5, x.getVal());
    assertEquals(3, y.getVal());
    assertEquals(2, z.getVal());
  }

}