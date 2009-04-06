/*
 * EqualXCTest.java
 *
 */

package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import junit.framework.TestCase;

import java.util.logging.Logger;

/**
 * a class implementing tests for backtrackable search
 */
public class EqualXCTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar x;
  private IntDomainVar y;
  private IntDomainVar z;
  private Constraint c1;
  private Constraint c2;
  private Constraint c3;

  protected void setUp() {
    logger.fine("EqualXC Testing...");
    pb = new Problem();
    x = pb.makeBoundIntVar("X", 1, 5);
    y = pb.makeBoundIntVar("Y", 1, 5);
    c1 = pb.eq(x, 1);
    c2 = pb.eq(y, 2);
    c3 = pb.eq(y, 3);
  }

  protected void tearDown() {
    c1 = null;
    c2 = null;
    c3 = null;
    x = null;
    y = null;
    z = null;
    pb = null;
  }

  public void test1() {
    logger.finer("test1");
    try {
      pb.post(c1);
      pb.post(c2);
      pb.propagate();
    } catch (ContradictionException e) {
      assertTrue(false);
    }
    assertTrue(x.isInstantiated());
    assertTrue(y.isInstantiated());
    assertEquals(1, x.getVal());
    assertEquals(2, y.getVal());
    logger.finest("domains OK after first propagate");
    assertFalse(c3.isSatisfied());
  }
}
