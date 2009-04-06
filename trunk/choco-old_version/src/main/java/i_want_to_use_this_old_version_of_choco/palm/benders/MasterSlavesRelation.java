package i_want_to_use_this_old_version_of_choco.palm.benders;

import i_want_to_use_this_old_version_of_choco.palm.Explanation;

import java.util.ArrayList;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/**
 * A Class for describing the objective function among variables
 * of the master and subproblems
 */
public class MasterSlavesRelation {

  protected int nbSlave;


  /**
   * compute a bound for the subproblem k. 0 denotes the master problem
   *
   * @param objs : the objectives of each subproblem objective
   * @param mobj : objective value of the master
   * @return a integer denoting the bound (upper or lower depending if we are in maximization/ minimization)
   *         of the considered problem k
   */
  public int computeBound(int mobj, int[] objs, int k) {
    return 0;
  }

  /**
   * Compute an explanation for the master problem using a serie of
   * explanations extracted from the subproblems
   *
   * @param cuts a list of Explanation[]
   * @return a list of explanations which play the role of a benders cuts
   */
  public ArrayList computeExpl(Explanation[] cuts) {
    ArrayList list = new ArrayList();
    for (int i = 0; i < cuts.length; i++) {
      if (cuts[i] != null) list.add(cuts[i]);
    }
    return list;
  }

}
