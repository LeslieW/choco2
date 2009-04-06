package i_want_to_use_this_old_version_of_choco.real;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.logging.Logger;

/**
 * J-CHOCO
 * Copyright (C) F. Laburthe, 1999-2003
 * <p/>
 * An open-source Constraint Programming Kernel
 * for Research and Education
 * <p/>
 * Created by: Guillaume on 18 juin 2004
 * <p/>
 * This class calls several tests about real variables and constraints
 */
public class RealTestSuite extends TestSuite {
  private static Logger logger = Logger.getLogger("choco.currentElement");

  public static Test suite() {
    RealTestSuite test = new RealTestSuite();

    logger.fine("Build TestSuite for dev.choco.i_want_to_use_this_old_version_of_choco.real");

    test.addTestSuite(TrigoTests.class);
    test.addTestSuite(IATests.class);
    test.addTestSuite(MixedEqualityTests.class);

    return test;
  }
}
