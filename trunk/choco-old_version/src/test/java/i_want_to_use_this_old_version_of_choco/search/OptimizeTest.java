package i_want_to_use_this_old_version_of_choco.search;

import i_want_to_use_this_old_version_of_choco.Problem;
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

public class OptimizeTest extends TestCase {
  private Logger logger = Logger.getLogger("choco.currentElement");
  private Problem pb;
  private IntDomainVar v1, v2, v3, obj;

  protected void setUp() {
    logger.fine("StoredInt Testing...");
    pb = new Problem();
    obj = pb.makeBoundIntVar("objectif", -10, 1000);
    v1 = pb.makeEnumIntVar("v1", 1, 10);
    v2 = pb.makeEnumIntVar("v2", -3, 10);
    v3 = pb.makeEnumIntVar("v3", 1, 10);
    pb.post(pb.eq(pb.sum(new IntDomainVar[]{v1, v2, v3}), obj));
  }

  protected void tearDown() {
    v1 = null;
    v2 = null;
    v3 = null;
    obj = null;
    pb = null;
  }

  /**
   * testing b&b search
   */
  public void test1() {
    logger.finer("test1");
    assertEquals(Boolean.TRUE, pb.maximize(obj, false));
    assertTrue(pb.getSolver().getNbSolutions() == 32);
    assertEquals(pb.getSolver().getOptimumValue().intValue(), 30);
  }

  /**
   * testing search with restarts
   */
  public void test2() {
    logger.finer("test2");
    assertEquals(Boolean.TRUE, pb.maximize(obj, true));
    assertTrue(pb.getSolver().getNbSolutions() == 32);
    assertEquals(pb.getSolver().getOptimumValue().intValue(), 30);
  }

}