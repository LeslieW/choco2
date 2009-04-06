//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran?ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.test;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.JumpProblem;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.PalmSolver;
import junit.framework.TestCase;

import java.util.BitSet;
import java.util.Random;

public class LatinSquare extends TestCase {
  public void solveLatinSquare(boolean jump, boolean decisionRepair) {
    // Toutes les solutions de n=5 en 90 sec  (161280 solutions)
    final int n = 4;
    final int[] soluces = new int[]{1, 2, 12, 576, 161280};

    // Problem
    AbstractProblem myPb = null;
    if (jump) {
      myPb = new JumpProblem();
      decisionRepair = false;
    } else {
      myPb = new PalmProblem();
    }

    // Variables
    IntDomainVar[] vars = new IntDomainVar[n * n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++) {
        vars[i * n + j] = myPb.makeEnumIntVar("C" + i + "_" + j, 1, n);
      }

    // Constraints
    for (int i = 0; i < n; i++) {
      for (int x = 0; x < n; x++) {
        for (int y = 0; y < x; y++) {
          myPb.post(myPb.neq(vars[i * n + y], vars[i * n + x]));
          myPb.post(myPb.neq(vars[y * n + i], vars[x * n + i]));
        }
      }
    }

    if (decisionRepair) {
      ((PalmSolver) myPb.getSolver()).setPathRepairValues(200, 10000);
    }

    myPb.solveAll();

    int nbsolutions = myPb.getSolver().getNbSolutions();
    assertEquals(soluces[n - 1], nbsolutions);
    System.out.println("LatinSquare Solutions : " + nbsolutions);
    myPb.printRuntimeSatistics();
    //Logger.getLogger("choco").getHandlers()[0].flush();
  }

  public int[][] affect(int n, int toComplete) {
    int[][] ret = new int[toComplete][2];

    BitSet init = new BitSet();
    for (int i = 0; i < n; i++) {
      init.set(i);
    }

    BitSet[][] vars = new BitSet[n][n];
    for (int x = 0; x < n; x++)
      for (int y = 0; y < n; y++) {
        vars[x][y] = new BitSet();
        vars[x][y].or(init);
      }

    Random random = new Random(61816871);

    int affected = 0;
    while (affected < toComplete) {
      int x = (int) Math.floor(random.nextDouble() * n);
      int y = (int) Math.floor(random.nextDouble() * n);
      int l = vars[x][y].cardinality();
      if (l > 1) {
        int pos = (int) Math.floor(random.nextDouble() * l);
        int val = -1;
        for (int i = 0; i <= pos; i++) {
          val = vars[x][y].nextSetBit(val + 1);
        }

        vars[x][y].clear();
        vars[x][y].set(val);
        for (int i = 0; i < n; i++) {
          if (i != y) vars[x][i].clear(val);
          if (i != x) vars[i][y].clear(val);
        }

        ret[affected][0] = x * n + y;
        ret[affected][1] = val + 1;
        affected++;
      }
    }

    for (int x = 0; x < n; x++)
      for (int y = 0; y < n; y++) {
        if (vars[x][y].cardinality() == 0) return null;
      }

    return ret;
  }

  public void testLatinSquareAllDBT() {
    solveLatinSquare(false, false);
  }

  public void testLatinSquareAllCBJ() {
    solveLatinSquare(true, false);
  }

  public void testLatinSquareAllDR() {
    solveLatinSquare(false, true);
  }

  public void testCompleteLatinSquareOneDBT() { // Non active par defaut, car peut prendre du temps !
    solveCompleteLatinSquare(false, false);
  }

  public void testCompleteLatinSquareOneDR() {     // Non active par defaut, car peut prendre du temps !
    solveCompleteLatinSquare(true, false);
  }

  public void testCompleteLatinSquareOneCBJ() {     // Non active par defaut, car peut prendre du temps !
    solveCompleteLatinSquare(false, true);
  }

  public void solveCompleteLatinSquare(boolean pathRepair, boolean jump) {
    final int n = 11;

    int[][] preAffect = null;
    int preAffected = (n * n * 42) / 100;

    while (preAffect == null) {
      preAffect = affect(n, preAffected);
    }

    // Problem
    AbstractProblem myPb = null;
    if (jump) {
      myPb = new JumpProblem();
      pathRepair = false;
    } else {
      myPb = new PalmProblem();
    }

    // Variables
    IntDomainVar[] vars = new IntDomainVar[n * n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++) {
        vars[i * n + j] = myPb.makeEnumIntVar("C" + i + "_" + j, 1, n);
      }

    for (int i = 0; i < preAffected; i++) {
      //System.out.println("Affect " + vars[preAffect[i][0]] + "  to  " + preAffect[i][1]);
      myPb.post(myPb.eq(vars[preAffect[i][0]], preAffect[i][1]));
    }

    // Constraints
    for (int i = 0; i < n; i++) {
      for (int x = 0; x < n; x++) {
        for (int y = 0; y < x; y++) {
          myPb.post(myPb.neq(vars[i * n + y], vars[i * n + x]));
          myPb.post(myPb.neq(vars[y * n + i], vars[x * n + i]));
        }
      }
    }

    if (pathRepair) {
      ((PalmSolver) myPb.getSolver()).setPathRepairValues(10, 100000);
    }
    myPb.solve();

    if (myPb.isFeasible() == Boolean.TRUE) {
      for (int i = 0; i < n; i++) {
        for (int x = 0; x < n; x++) {
          for (int y = 0; y < x; y++) {
            assertTrue(vars[i * n + y].getVal() != vars[i * n + x].getVal());
            assertTrue(vars[y * n + i].getVal() != vars[x * n + i].getVal());
          }
        }
      }
    }

    System.out.println((pathRepair ? "Decision Repair: " : "MAC-DBT: ") +
        "LatinSquare Completion Relax: " + myPb.isFeasible());
    myPb.printRuntimeSatistics();
  }

}
