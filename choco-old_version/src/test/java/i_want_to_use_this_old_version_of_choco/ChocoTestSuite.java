package i_want_to_use_this_old_version_of_choco;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        i_want_to_use_this_old_version_of_choco.mem.TestSuite.class,
    i_want_to_use_this_old_version_of_choco.integer.TestSuite.class,
    i_want_to_use_this_old_version_of_choco.util.TestSuite.class,
    i_want_to_use_this_old_version_of_choco.search.TestSuite.class,
    i_want_to_use_this_old_version_of_choco.set.TestSuite.class,
    i_want_to_use_this_old_version_of_choco.real.RealTestSuite.class,
    i_want_to_use_this_old_version_of_choco.global.TestSuite.class,
	i_want_to_use_this_old_version_of_choco.igoals.TestSuite.class,
    i_want_to_use_this_old_version_of_choco.regression.RegressionTest.class
})
public class ChocoTestSuite {
}
/*public class ChocoTestSuite extends TestSuite {
  private static Logger logger = Logger.getLogger("choco.currentElement");

  public static Test suite() {
    TestSuite test = new ChocoTestSuite();

    logger.fine("Build main TestSuite for choco.currentElement");
    test.addTest(dev.i_want_to_use_this_old_version_of_choco.mem.TestSuite.suite());
    test.addTest(dev.i_want_to_use_this_old_version_of_choco.integer.TestSuite.suite());
    test.addTest(dev.i_want_to_use_this_old_version_of_choco.util.TestSuite.suite());
    test.addTest(dev.i_want_to_use_this_old_version_of_choco.search.TestSuite.suite());
    //test.addTest(dev.i_want_to_use_this_old_version_of_choco.bool.TestSuite.suite());
    test.addTest(dev.i_want_to_use_this_old_version_of_choco.set.TestSuite.suite());
    test.addTest(dev.i_want_to_use_this_old_version_of_choco.real.RealTestSuite.suite());
    test.addTest(dev.i_want_to_use_this_old_version_of_choco.global.TestSuite.suite());
	test.addTest(dev.i_want_to_use_this_old_version_of_choco.igoals.TestSuite.suite());
    test.addTestSuite(dev.i_want_to_use_this_old_version_of_choco.regression.RegressionTest.class);
	//test.addTest(dev.i_want_to_use_this_old_version_of_choco.reified.ReifiedSuite.suite());
	new ReifiedSuite();
    //test.addTestSuite(new ReifiedSuite());
	//currentElement.addTestSuite(ChocoSolveTest.class);
    return test;
  }
} */
