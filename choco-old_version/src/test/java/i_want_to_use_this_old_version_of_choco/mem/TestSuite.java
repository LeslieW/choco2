package i_want_to_use_this_old_version_of_choco.mem;

import junit.framework.Test;

import java.util.logging.Logger;

public class TestSuite extends junit.framework.TestSuite {
  private static Logger logger = Logger.getLogger("choco.currentElement");

  public static Test suite() {
    TestSuite test = new TestSuite();

    logger.fine("Build TestSuite for choco.currentElement.mem");
    test.addTestSuite(StoredBoolTest.class);
    test.addTestSuite(StoredIntTest.class);
    test.addTestSuite(StoredIntVectorTest.class);
    test.addTestSuite(StoredBitSetTest.class);
    test.addTestSuite(PartiallyStoredVectorTest.class);
    test.addTestSuite(PartiallyStoredIntVectorTest.class);

    return test;
  }
}
