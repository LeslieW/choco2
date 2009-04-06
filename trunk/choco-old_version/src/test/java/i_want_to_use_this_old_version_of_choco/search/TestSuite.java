// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/* File choco.currentElement.search.TestSuite.java, last modified by Francois 3 d�c. 2003 00:01:54 */
package i_want_to_use_this_old_version_of_choco.search;

import junit.framework.Test;

import java.util.logging.Logger;

public class TestSuite extends junit.framework.TestSuite {
  private static Logger logger = Logger.getLogger("choco.currentElement");

  public static Test suite() {
    TestSuite test = new TestSuite();

    logger.fine("Build TestSuite for choco.currentElement.search");
    test.addTestSuite(SolveTest.class);
    test.addTestSuite(OptimizeTest.class);
    test.addTestSuite(QueensTest.class);
    test.addTestSuite(ZebraTest.class);
    test.addTestSuite(RandomSearchTest.class);
    test.addTestSuite(CutTest.class);
    test.addTestSuite(BranchingTest.class);
    test.addTestSuite(HeuristicTests.class);
	return test;
  }
}