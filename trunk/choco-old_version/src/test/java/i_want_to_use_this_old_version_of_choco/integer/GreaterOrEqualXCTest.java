// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.search.GreaterOrEqualXCTest.java, last modified by Francois 23 ao�t 2003:17:40:29 */
package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import junit.framework.TestCase;

import java.util.logging.Logger;

public class GreaterOrEqualXCTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar x;
  private IntDomainVar y;
  private Constraint c1;
  private Constraint c2;

  protected void setUp() {
    logger.fine("GreaterOrEqualXCTest Testing...");
    pb = new Problem();
    x = pb.makeBoundIntVar("X", 1, 5);
    y = pb.makeBoundIntVar("Y", 1, 5);
    c1 = pb.geq(x, 1);
    c2 = pb.geq(y, 2);
  }

  protected void tearDown() {
    c1 = null;
    c2 = null;
    x = null;
    y = null;
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
      assertEquals(1, x.getInf());
      assertEquals(2, y.getInf());
      logger.finest("domains OK after first propagate");
      assertTrue(((AbstractConstraint)c1).isConsistent());
      assertTrue(((AbstractConstraint)c2).isConsistent());
    } catch (ContradictionException e) {
      assertTrue(false);
    }
  }
}
