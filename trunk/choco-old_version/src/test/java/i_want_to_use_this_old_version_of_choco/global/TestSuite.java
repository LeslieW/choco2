package i_want_to_use_this_old_version_of_choco.global;

import junit.framework.Test;

import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2004         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class TestSuite extends junit.framework.TestSuite {
  private static Logger logger = Logger.getLogger("choco.currentElement");

  public static Test suite() {
    TestSuite test = new TestSuite();

    logger.fine("Build TestSuite for choco.currentElement.global");
    test.addTestSuite(AllDifferentTest.class);
    test.addTestSuite(GlobalCardinalityTest.class);
    test.addTestSuite(OccurrenceTest.class);
    test.addTestSuite(CumulativeTest.class);
	  test.addTestSuite(DisjonctiveTest.class);
    test.addTestSuite(LexTest.class);
    test.addTestSuite(RegularTest.class);
    test.addTestSuite(NValueTest.class);
    test.addTestSuite(BoundGccTest.class);
    return test;
  }
}
