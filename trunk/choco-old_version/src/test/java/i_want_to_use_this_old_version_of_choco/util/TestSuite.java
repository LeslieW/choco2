/** -------------------------------------------------
 *                   J-CHOCO
 *   Copyright (C) F. Laburthe, 1999-2003
 * --------------------------------------------------
 *    an open-source Constraint Programming Kernel
 *          for Research and Education
 * --------------------------------------------------
 *
 * file: choco.currentElement.util.TestSuite.java
 * last modified by Francois 28 ao�t 2003:14:59:09
 */
package i_want_to_use_this_old_version_of_choco.util;

import junit.framework.Test;

import java.util.logging.Logger;

public class TestSuite extends junit.framework.TestSuite {
  private static Logger logger = Logger.getLogger("choco.currentElement");

  public static Test suite() {
    TestSuite test = new TestSuite();

    logger.fine("Build TestSuite for choco.currentElement.util");
    test.addTestSuite(BitSetTest.class);
    test.addTestSuite(BipartiteSetTest.class);
    test.addTestSuite(PriorityQueueTest.class);
    test.addTestSuite(StoredPointerCycleTest.class);

    return test;
  }
}
