package i_want_to_use_this_old_version_of_choco.palm.test;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.PalmSolver;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;
import i_want_to_use_this_old_version_of_choco.palm.search.NogoodConstraint;
import junit.framework.TestCase;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class DRTestSuite extends TestCase {

  public void testNogoodConstraint() {
    PalmProblem myPb = new PalmProblem();
    IntDomainVar x = myPb.makeEnumIntVar("X", 1, 3);
    IntDomainVar y = myPb.makeEnumIntVar("Y", 1, 3);
    IntDomainVar z = myPb.makeEnumIntVar("Z", 1, 3);

    Explanation ng1 = myPb.makeExplanation();
    Explanation ng2 = myPb.makeExplanation();

    AbstractConstraint dx1 = (AbstractConstraint) ((PalmIntVar) x).getDecisionConstraint(1);
    AbstractConstraint dx2 = (AbstractConstraint) ((PalmIntVar) x).getDecisionConstraint(2);
    AbstractConstraint dy2 = (AbstractConstraint) ((PalmIntVar) y).getDecisionConstraint(2);
    AbstractConstraint dy3 = (AbstractConstraint) ((PalmIntVar) y).getDecisionConstraint(3);
    AbstractConstraint dz3 = (AbstractConstraint) ((PalmIntVar) z).getDecisionConstraint(3);

    myPb.post(dx1);
    myPb.remove(dx1);
    myPb.post(dx2);
    myPb.remove(dx2);
    myPb.post(dy2);
    myPb.remove(dy2);
    myPb.post(dy3);
    myPb.remove(dy3);
    myPb.post(dz3);
    myPb.remove(dz3);

    // 1er nogood : (x = 1, y = 2, z = 3)
    ng1.add(dx1);
    ng1.add(dy2);
    ng1.add(dz3);

    // 2�me nogood : (y = 3, z = 3)
    ng2.add(dy3);
    ng2.add(dz3);

    NogoodConstraint ngct = new NogoodConstraint(new IntDomainVar[]{x, y, z});
    myPb.post(ngct);

    ngct.addNogoodFirst(ng1);
    ngct.addNogoodFirst(ng2);

    myPb.solveAll();

    int nbSolutions = myPb.getSolver().getNbSolutions();
    /*for (int i = 0; i < nbSolutions; i++) {
      Solution solution = ((PalmSolver) myPb.getSolver()).getSolution(i);
      System.out.println(solution);
    }*/

    System.out.println("nb Sol " + nbSolutions);

    assertEquals(23, nbSolutions);
  }

  /**
   * Compute all solutions with Decision Repair
   */
  public void testDRAllsolutions() {
    PalmProblem myPb = new PalmProblem();
    IntDomainVar x = myPb.makeEnumIntVar("X", 1, 3);
    IntDomainVar y = myPb.makeEnumIntVar("Y", 1, 3);
    IntDomainVar z = myPb.makeEnumIntVar("Z", 1, 3);

    myPb.post(myPb.neq(x, y));
    myPb.post(myPb.neq(x, z));
    myPb.post(myPb.leq(y, z));

    ((PalmSolver) myPb.getSolver()).setPathRepairValues(10, 10000);

    myPb.solveAll();
    int nbSolutions = myPb.getSolver().getNbSolutions();
    System.out.println("nb Sol " + nbSolutions);
    assertEquals(9, nbSolutions);
  }
}
