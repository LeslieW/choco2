//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Solution;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

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

  public PalmSolution(AbstractProblem pb) {
    super(pb);
    this.lstat = new int[((PalmGlobalSearchSolver) pb.getSolver().getSearchSolver()).getNbLimit()];
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
    for (int i = 0; i < this.problem.getNbIntVars(); i++) {
      IntDomainVar var = (IntDomainVar) this.problem.getIntVar(i);
      buf.append(var + ":" + this.intVarValues[i] + " \n");
    }

    return buf.toString();
  }
}
