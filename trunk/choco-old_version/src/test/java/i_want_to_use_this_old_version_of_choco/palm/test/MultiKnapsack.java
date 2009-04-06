package i_want_to_use_this_old_version_of_choco.palm.test;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.JumpProblem;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.PalmSolver;
import junit.framework.TestCase;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class MultiKnapsack extends TestCase {
  /**
   * Multi dimensionnal knapsack :
   * mknap1_2 dans mknap1.txt � http://www.brunel.ac.uk/depts/ma/research/jeb/info.html
   */
  public void solveMultiKnapsack(boolean dr, boolean jump, boolean restart) {
    AbstractProblem myPb = null;
    if (jump) {
      myPb = new JumpProblem();
      dr = false;
    } else {
      myPb = new PalmProblem();
    }

    IntDomainVar[] vars = new IntDomainVar[15];
    for (int i = 0; i < 15; i++) {
      vars[i] = myPb.makeEnumIntVar("" + i, 0, 1);
    }
    IntDomainVar v = myPb.makeBoundIntVar("opt", 0, 1000000);
    // la fonction obj
    myPb.post(myPb.eq(myPb.scalar(new int[]{100, 220, 90, 400, 300, 400, 205, 120, 160, 580, 400, 140, 100, 1300, 650}, vars), v));
    // Les sacs � dos
    myPb.post(myPb.leq(myPb.scalar(new int[]{8, 24, 13, 80, 70, 80, 45, 15, 28, 90, 130, 32, 20, 120, 40}, vars), 550));
    myPb.post(myPb.leq(myPb.scalar(new int[]{8, 44, 13, 100, 100, 90, 75, 25, 28, 120, 130, 32, 40, 160, 40}, vars), 700));
    myPb.post(myPb.leq(myPb.scalar(new int[]{3, 6, 4, 20, 20, 30, 8, 3, 12, 14, 40, 6, 3, 20, 5}, vars), 130));
    myPb.post(myPb.leq(myPb.scalar(new int[]{5, 9, 6, 40, 30, 40, 16, 5, 18, 24, 60, 16, 11, 30, 25}, vars), 240));
    myPb.post(myPb.leq(myPb.scalar(new int[]{5, 11, 7, 50, 40, 40, 19, 7, 18, 29, 70, 21, 17, 30, 25}, vars), 280));
    myPb.post(myPb.leq(myPb.scalar(new int[]{5, 11, 7, 55, 40, 40, 21, 9, 18, 29, 70, 21, 17, 35, 25}, vars), 310));
    myPb.post(myPb.leq(myPb.scalar(new int[]{1, 10, 4, 10, 6, 6, 32, 3, 70, 10},
        new IntDomainVar[]{vars[2], vars[3], vars[4], vars[5], vars[7], vars[9], vars[10], vars[11], vars[13], vars[14]}), 110));
    myPb.post(myPb.leq(myPb.scalar(new int[]{3, 4, 5, 20, 14, 20, 6, 12, 10, 18, 42, 9, 12, 100, 20}, vars), 205));
    myPb.post(myPb.leq(myPb.scalar(new int[]{3, 6, 9, 30, 29, 20, 12, 12, 10, 30, 42, 18, 18, 110, 20}, vars), 260));
    myPb.post(myPb.leq(myPb.scalar(new int[]{3, 8, 9, 35, 29, 20, 16, 15, 10, 30, 42, 20, 18, 120, 20}, vars), 275));

    if (dr)
      ((PalmSolver) myPb.getSolver()).setPathRepairValues(20, 10000);

    myPb.maximize(v, restart);

    myPb.printRuntimeSatistics();
    //Logger.getLogger("choco").getHandlers()[0].flush();

    assertEquals(myPb.getSolver().getOptimumValue().intValue(), 4015);
  }

  public void testMultiKnapsackDR() {
    this.solveMultiKnapsack(true, false, false);
  }

  public void testMultiKnapsackDBT() {
    this.solveMultiKnapsack(false, false, false);
  }

  public void testMultiKnapsackCBJ() {
    this.solveMultiKnapsack(false, true, false);
  }

  public void testMultiKnapsackRestartCBJ() {
    this.solveMultiKnapsack(false, true, true);
  }
}
