package i_want_to_use_this_old_version_of_choco.set;

import junit.framework.Test;

import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class TestSuite extends junit.framework.TestSuite {
  private static Logger logger = Logger.getLogger("choco.currentElement");

  public static Test suite() {
    TestSuite test = new TestSuite();

    logger.fine("Build TestSuite for dev.i_want_to_use_this_old_version_of_choco.set");

    test.addTestSuite(VariableTests.class);
	test.addTestSuite(SetUnionTest.class);
    test.addTestSuite(BasicConstraintsTests.class);
    test.addTestSuite(SearchTests.class);


    return test;
  }
}
