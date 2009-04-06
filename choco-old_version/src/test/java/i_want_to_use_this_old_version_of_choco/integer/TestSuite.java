package i_want_to_use_this_old_version_of_choco.integer;

import junit.framework.Test;

import java.util.logging.Logger;

public class TestSuite extends junit.framework.TestSuite {
  private static Logger logger = Logger.getLogger("choco.currentElement");

  public static Test suite() {
    TestSuite test = new TestSuite();

    logger.fine("Build TestSuite for choco.currentElement.search");
    //currentElement.addTestSuite(LinkedListIntDomainTest.class);
    test.addTestSuite(BitSetIntDomainTest.class);
    test.addTestSuite(IntervalIntDomainTest.class);
    test.addTestSuite(IntVarEventTest.class);
    test.addTestSuite(ElementTest.class);
    test.addTestSuite(EqualXCTest.class);
    test.addTestSuite(EqualXYCTest.class);
    test.addTestSuite(GreaterOrEqualXCTest.class);
    test.addTestSuite(GreaterOrEqualXYCTest.class);
    test.addTestSuite(LessOrEqualXCTest.class);
    test.addTestSuite(NotEqualXCTest.class);
    test.addTestSuite(NotEqualXYCTest.class);
    test.addTestSuite(IntLinCombTest.class);
    test.addTestSuite(BoolLinCombTest.class);
    test.addTestSuite(BinRelationApiTest.class);
    test.addTestSuite(NaryRelationTest.class);
    test.addTestSuite(BinRelationSearchTest.class);
    test.addTestSuite(ChannelingTest.class);
    test.addTestSuite(TimesXYZTest.class);
    test.addTestSuite(MinTest.class);
    test.addTestSuite(MaxTest.class);
    test.addTestSuite(AbsTest.class);
    test.addTestSuite(DistanceTest.class);
    return test;
  }
}
