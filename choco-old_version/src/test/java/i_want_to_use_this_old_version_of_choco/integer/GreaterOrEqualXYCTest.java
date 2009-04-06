package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.constraints.GreaterOrEqualXYC;
import junit.framework.TestCase;

import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.search.GreaterOrEqualXYCTest.java, last modified by Francois 11 sept. 2003 00:36:20 */

public class GreaterOrEqualXYCTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar x;
  private IntDomainVar y;
  private IntDomainVar z;
  private Constraint c1;
  private Constraint c2;

  protected void setUp() {
    logger.fine("GreaterOrEqualXYCTest Testing...");
    pb = new Problem();
    x = pb.makeBoundIntVar("X", 1, 5);
    y = pb.makeBoundIntVar("Y", 1, 5);
    z = pb.makeBoundIntVar("Z", 1, 5);
    c1 = new GreaterOrEqualXYC(x, y, 1);
    c2 = new GreaterOrEqualXYC(y, z, 2);
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
    try {
      pb.post(c1);
      pb.post(c2);
      pb.propagate();
      assertFalse(x.isInstantiated());
      assertFalse(y.isInstantiated());
      assertEquals(4, x.getInf());
      assertEquals(3, y.getInf());
      assertEquals(1, z.getInf());
      assertEquals(5, x.getSup());
      assertEquals(4, y.getSup());
      assertEquals(2, z.getSup());
      logger.finest("domains OK after first propagate");
      z.setVal(2);
      pb.propagate();
      assertTrue(x.isInstantiated());
      assertTrue(y.isInstantiated());
      assertTrue(z.isInstantiated());
      assertEquals(5, x.getVal());
      assertEquals(4, y.getVal());
      assertEquals(2, z.getVal());
    } catch (ContradictionException e) {
      assertTrue(false);
    }
  }
}
