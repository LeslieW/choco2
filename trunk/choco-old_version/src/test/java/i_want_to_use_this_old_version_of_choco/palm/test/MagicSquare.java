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

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.BendersProblem;
import i_want_to_use_this_old_version_of_choco.palm.JumpProblem;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.PalmSolver;
import junit.framework.TestCase;

public class MagicSquare extends TestCase {
  public void testSquareOneDBT() {
    square(5, true, false, false, false);
  }

  public void testSquareOneDR() {
    square(4, true, true, false, false);
  }

  public void testSquareOneCBJ() {
    square(5, true, false, true, false);
  }

  public void testSquareOneBenders() {
    square(4, true, false, false, true);
  }


  public void square(int n, boolean help, boolean pathRepair, boolean jump, boolean benders) {
    int magic = n * (n * n + 1) / 2;

    AbstractProblem myPb = null;
    if (jump) {
      myPb = new JumpProblem();
      pathRepair = false;
    } else if (benders) {
      myPb = new BendersProblem();
      pathRepair = false;
    } else {
      myPb = new PalmProblem();
    }
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search").setLevel(Level.FINEST);
    //Logger.getLogger("choco").getHandlers()[0].setLevel(Level.FINEST);
    IntDomainVar[] vars = new IntDomainVar[n * n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++) {
        vars[i * n + j] = myPb.makeEnumIntVar("C" + i + "_" + j, 1, n * n);
        if (myPb instanceof BendersProblem) {
          if (j > 0 && j < (n - 1) && i > 0 && i < (n - 1))
            ((BendersProblem) myPb).addMasterVariables(vars[i * n + j]);
          else
            ((BendersProblem) myPb).addSubVariables(0, vars[i * n + j]);
        }
      }
    IntDomainVar sum = myPb.makeEnumIntVar("S", 1, n * n * (n * n + 1) / 2);

    try {
      if (help) myPb.post(myPb.eq(sum, magic));

      for (int i = 0; i < n * n; i++)
        for (int j = 0; j < i; j++) {
          myPb.post(myPb.neq(vars[i], vars[j]));
        }

      int[] coeffs = new int[n];
      for (int i = 0; i < n; i++) {
        coeffs[i] = 1;
      }

      for (int i = 0; i < n; i++) {
        IntDomainVar[] col = new IntDomainVar[n];
        IntDomainVar[] row = new IntDomainVar[n];

        for (int j = 0; j < n; j++) {
          col[j] = vars[i * n + j];
          row[j] = vars[j * n + i];
        }

        myPb.post(myPb.eq(myPb.scalar(coeffs, row), sum));
        myPb.post(myPb.eq(myPb.scalar(coeffs, col), sum));
      }

      if (pathRepair) {
        ((PalmSolver) myPb.getSolver()).setPathRepairValues(10, 10000);
      }
      myPb.solve();

      System.out.println("Solution (among " + myPb.getSolver().getNbSolutions() + "):\n");// + ((PalmSolver) myPb.getSolver()).getSolution(0));

      int[] col = new int[n];
      int[] lig = new int[n];
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          col[i] += vars[j + i * n].getVal();
          lig[i] += vars[j * n + i].getVal();
        }
      }
      for (int i = 0; i < n - 1; i++) {
        assertEquals(col[i], col[i + 1]);   // toutes les lignes sont �gales
        assertEquals(lig[i], lig[i + 1]);  // les colonnes
      }
      assertEquals(col[1], lig[1]);           // il suffit d'une ligne �gale � une colonne
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          if (i != j) assertTrue(vars[i].getVal() != vars[j].getVal());
        }
      }

      System.out.println((pathRepair ? "AbstractDecision Repair: " : "MAC-DBT: ")
          + "MAGICSQUARE(" + n + ") Solutions: " + myPb.isFeasible());
      myPb.printRuntimeSatistics();
    } catch (Exception e) {
      e.printStackTrace();
      assertTrue(false);
    }
  }
}
