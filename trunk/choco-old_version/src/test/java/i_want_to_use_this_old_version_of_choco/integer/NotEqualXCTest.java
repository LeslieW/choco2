package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import junit.framework.TestCase;

import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.search.NotEqualXCTest.java, last modified by Francois 14 sept. 2003 16:00:54 */


public class NotEqualXCTest extends TestCase {
  private static Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar x;
  private IntDomainVar y;
  private Constraint c1;
  private Constraint c2;

  protected void setUp() {
    logger.fine("NotEqualXC Testing...");
    pb = new Problem();
    x = pb.makeEnumIntVar("X", 1, 5);
    y = pb.makeBoundIntVar("Y", 1, 5);
    c1 = pb.neq(x, 3);
    c2 = pb.neq(y, 3);
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
      assertFalse(x.canBeInstantiatedTo(3));
      assertTrue(y.canBeInstantiatedTo(3));
      x.remVal(2);
      x.remVal(1);
      pb.propagate();
      assertEquals(x.getInf(), 4);
      y.setInf(3);
      pb.propagate();
      assertEquals(y.getInf(), 4);
    } catch (ContradictionException e) {
      assertTrue(false);
    }
  }
}
