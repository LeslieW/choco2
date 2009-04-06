package i_want_to_use_this_old_version_of_choco.igoals;

import junit.framework.Test;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 20 mars 2008
 * Time: 08:37:12
 * To change this template use File | Settings | File Templates.
 */
public class TestSuite extends junit.framework.TestSuite {
  private static Logger logger = Logger.getLogger("choco.currentElement");

  public static Test suite() {
    TestSuite test = new TestSuite();

    logger.fine("Build TestSuite for choco.currentElement.igolas");
    test.addTestSuite(SearchTests.class);
    return test;
  }
}
