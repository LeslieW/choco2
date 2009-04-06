//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.test;

import junit.framework.Test;
import junit.framework.TestSuite;


public class PalmTestSuite extends TestSuite {
  public static Test suite() {
    TestSuite test = new TestSuite();

    test.addTestSuite(LatinSquare.class);
    test.addTestSuite(MagicSquare.class);
    test.addTestSuite(NQueens.class);
    test.addTestSuite(PalmSolveTest.class);
    test.addTestSuite(SendMoney.class);
    test.addTestSuite(DRTestSuite.class);
    test.addTestSuite(JumpSolveTest.class);
    test.addTestSuite(MultiKnapsack.class);
    test.addTestSuite(BendersTest.class);
    return test;
  }
}
