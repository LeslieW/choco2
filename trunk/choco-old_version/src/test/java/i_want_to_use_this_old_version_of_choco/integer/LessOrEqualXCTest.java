package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Propagator;
import junit.framework.TestCase;

import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.search.LessOrEqualXC.java, last modified by Francois 11 sept. 2003 00:46:25 */

public class LessOrEqualXCTest extends TestCase {
  private static Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar x;
  private IntDomainVar y;
  private IntDomainVar z;
  private Constraint c1;
  private Constraint c2;

  protected void setUp() {
    logger.finer("LessOrEqualXC Testing...");
    pb = new Problem();
    x = pb.makeBoundIntVar("X", 1, 5);
    y = pb.makeBoundIntVar("Y", 1, 5);
    c1 = pb.leq(x, 1);
    c2 = pb.leq(y, 2);
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
    } catch (ContradictionException e) {
      assertTrue(false);
    }
    assertTrue(x.isInstantiated());
    assertFalse(y.isInstantiated());
    assertEquals(1, x.getSup());
    assertEquals(2, y.getSup());
    logger.finest("domains OK after first propagate");
    assertTrue(c1.isSatisfied());
    assertEquals(((Propagator) c2).isEntailed(), Boolean.TRUE);
  }
}
