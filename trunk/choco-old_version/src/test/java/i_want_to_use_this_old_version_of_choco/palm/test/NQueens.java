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
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.PalmSolution;
import junit.framework.TestCase;

public class NQueens extends TestCase {

  public void solveNQueens(int nbqueen, boolean all, boolean decisionRepair, boolean jump, boolean benders) {
    // A priori fonctionne jusqu'a au moins 13
    // - 13 : plus de 4000 sec (plus d'une heure)
    // - 12 : 1013 sec
    // - 11 : 180 sec
    // - 10 : 38 sec
    // - 9 : 5 sec

    final int NBQueen = nbqueen;
    final int[] NBSols = new int[]{0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};

    AbstractProblem myPb = null;
    if (jump) {
      myPb = new JumpProblem();
      decisionRepair = false;
    } else if (benders) {
      myPb = new BendersProblem();
      decisionRepair = false;
    } else {
      myPb = new PalmProblem();
    }
    //Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search").setLevel(Level.FINEST);
    //Logger.getLogger("choco").getHandlers()[0].setLevel(Level.FINEST);
    int step = NBQueen / 4;
    IntDomainVar[] vars = new IntDomainVar[NBQueen];
    for (int i = 0; i < vars.length; i++) {
      vars[i] = myPb.makeEnumIntVar("C" + (i + 1), 1, NBQueen);
      if (myPb instanceof BendersProblem) {
        if (i >= step && i < (NBQueen - step))
          ((BendersProblem) myPb).addMasterVariables(vars[i]);
        else
          ((BendersProblem) myPb).addSubVariables(0, vars[i]);
      }
    }

    for (int i = 0; i < NBQueen; i++)
      for (int j = i + 1; j < NBQueen; j++) {
        int k = j - i;
        myPb.post(myPb.neq(vars[i], vars[j]));
        myPb.post(myPb.neq(vars[i], myPb.plus(vars[j], k)));
        myPb.post(myPb.neq(vars[j], myPb.plus(vars[i], k)));
      }

    if (decisionRepair) {
      ((PalmSolver) myPb.getSolver()).setPathRepairValues(100, 10000);
    }
    //((PalmSolver)myPb.getSolver()).setPalmState(new TraceState(((PalmProblem) myPb).makeExplanation()));
    if (all)
      myPb.solveAll();
    else
      myPb.solve();
    int nbSolutions = myPb.getSolver().getNbSolutions();

    if (myPb instanceof PalmProblem) {
      int falseSoluce = 0;

      for (int i = 0; i < nbSolutions; i++) {
        PalmSolution solution = (PalmSolution) ((PalmSolver) myPb.getSolver()).getSolution(i);
        for (int x = 0; x < NBQueen; x++)
          for (int y = x + 1; y < NBQueen; y++) {
            int k = y - x;
            if ((solution.getValue(x) == solution.getValue(y))
                || (solution.getValue(x) == solution.getValue(y) + k)
                || (solution.getValue(y) == solution.getValue(x) + k)) {
              System.out.println("Fausse solution : ");
              System.out.println(solution);
              falseSoluce++;
            }
          }
      }

      //assertEquals(falseSoluce, 0);
    }

    if (all) assertEquals(NBSols[NBQueen - 1], nbSolutions);
    System.out.println((decisionRepair ? "DecisionRepair: " : "MAC-DBT: ")
        + "NQueens (" + NBQueen + "): " + myPb.isFeasible());
    System.out.println("NBQueen(" + NBQueen + ") Solutions : " + nbSolutions);
    myPb.printRuntimeSatistics();
    //Logger.getLogger("choco").getHandlers()[0].flush();
  }

  public void testNQueensOneDBT() {
    solveNQueens(25, false, false, false, false);
  }

  public void testNQueensOneDR() {
    solveNQueens(20, false, true, false, false);
  }

  public void testNQueensAllDBT() {
    solveNQueens(8, true, false, false, false);
  }

  public void testNQueensAllDR() {
    solveNQueens(7, true, true, false, false);
  }

  public void testNQueensAllCBJ() {
    solveNQueens(8, true, false, true, false);
  }

  public void testNQueensOneBenders() {
    solveNQueens(25, false, false, false, true);
  }
}
