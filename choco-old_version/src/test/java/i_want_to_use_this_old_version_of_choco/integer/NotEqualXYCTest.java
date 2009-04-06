// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.search.NotEqualXYCTest.java, last modified by Francois 21 sept. 2003 10:59:44 */

package i_want_to_use_this_old_version_of_choco.integer;

import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.integer.constraints.NotEqualXYC;
import junit.framework.TestCase;

import java.util.logging.Logger;

public class NotEqualXYCTest extends TestCase {
  private static Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar x;
  private IntDomainVar y;
  private IntDomainVar z;
  private Constraint c1;
  private Constraint c2;

  protected void setUp() {
    logger.fine("NotEqualXYC Testing...");
    pb = new Problem();
    x = pb.makeEnumIntVar("X", 1, 5);
    y = pb.makeEnumIntVar("Y", 1, 5);
    z = pb.makeEnumIntVar("Z", 1, 5);
    c1 = new NotEqualXYC(x, y, -2);
    c2 = new NotEqualXYC(y, z, 1);
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
      x.setSup(2);
      y.setVal(3);
      pb.propagate();
      assertFalse(x.canBeInstantiatedTo(1));
      assertTrue(x.isInstantiatedTo(2));
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

  public void test2() {
    logger.finer("test2");
    try {
      pb.post(c1);
      pb.post(c2);
      y.setVal(3);
      pb.propagate();
      assertFalse(x.canBeInstantiatedTo(1));
      assertFalse(z.canBeInstantiatedTo(2));
    } catch (ContradictionException e) {
      assertFalse(true);
    }
  }

}
