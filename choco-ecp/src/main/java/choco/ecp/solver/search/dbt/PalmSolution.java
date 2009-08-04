//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package choco.ecp.solver.search.dbt;

import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Solution for a Palm problem.
 */

public class PalmSolution extends Solution {

  /**
   * Statistics for this solution (CPU, extensiions...).
   */

  protected int[] lstat;


  /**
   * Creates a solution for the specified problem.
   */

  public PalmSolution(Solver pb) {
    super(pb);
    this.lstat = new int[((PalmGlobalSearchStrategy) pb.getSearchStrategy()).getNbLimit()];
  }


  public void recordStatistic(int statIndex, int statValue) {
    lstat[statIndex] = statValue;
  }


  /**
   * Returns a statistic about the solution.
   *
   * @param stat The statitic constant (<code>PalmProblem.RLX/EXT/CPU</code>).
   */

  public int getStatistic(int stat) {
    return lstat[stat];
  }

  /**
   * Returns the value of the variable <code>idx</code>.
   *
   * @param idx the variable number
   * @return the value of the variable
   */

  public int getValue(int idx) {    // TODO : dans Choco ?
    return this.intVarValues[idx];
  }


  /**
   * Pretty display of the solution.
   */

  public String toString() {
    StringBuffer buf = new StringBuffer();

    buf.append("SOL ");
    for (int i = 0; i < this.solver.getNbIntVars(); i++) {
      IntDomainVar var = (IntDomainVar) this.solver.getIntVar(i);
      buf.append(var + ":" + this.intVarValues[i] + " \n");
    }

    return buf.toString();
  }
}
