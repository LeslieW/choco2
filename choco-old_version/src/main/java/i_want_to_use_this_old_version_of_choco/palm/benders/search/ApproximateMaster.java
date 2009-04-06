package i_want_to_use_this_old_version_of_choco.palm.benders.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.palm.BendersProblem;
import i_want_to_use_this_old_version_of_choco.palm.benders.MasterSlavesRelation;
import i_want_to_use_this_old_version_of_choco.palm.cbj.explain.JumpExplanation;

import java.util.logging.Level;
import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/**
 * Benders search with approximated (not independent) substructures
 * in case of satisfaction problems.
 * <p/>
 * Provide a specific implementation of the resolution of
 * subproblems by fusionning branching of previous feasible subproblems.
 * Otherwise, it attempts to derive disjoint cuts on each subproblem.
 */
public class ApproximateMaster extends MasterGlobalSearchSolver {


  public ApproximateMaster(AbstractProblem pb, int nbsub, MasterSlavesRelation relation) {
    super(pb, nbsub, relation);
  }

  public ApproximateMaster(AbstractProblem pb, int nbsub) {
    super(pb, nbsub);
  }

  public void solveSubProblems() {
    BendersProblem pb = (BendersProblem) problem;
    for (int i = 0; i < pb.getNbSubProblems(); i++) { // solve the subproblems
      if (i > 0 && nbCutLearned == 0) { // tant que le sous-probl�me est faisable, "�largir son branching"
        if (logger.isLoggable(Level.FINE))
          logger.fine("FUSION SUBPB " + i + " with SUBPB " + (i - 1));
        Logger.getLogger("choco").getHandlers()[0].flush();
        subproblems.fusionGoal(subgoals[i]);
      } else {
        problem.worldPush();
        if (logger.isLoggable(Level.FINE))
          logger.fine("START SUBPB " + i);
        // une fois qu'on a une contradiction, n'examiner que les sous-probl�mes un � un
        subproblems.changeGoal(subgoals[i]);
      }
      Boolean sol = subproblems.nextOptimalSolution(masterWorld);
      if (sol == Boolean.FALSE) {
        fail = pb.getContradictionExplanation();
        ((JumpExplanation) fail).delete(masterWorld + 1);
        if (((JumpExplanation) fail).nogoodSize() == 0) feasible = false;
        storeCuts(fail, i);
      } else if (sol == Boolean.TRUE && nbCutLearned == 0) {
        storePartialSolution(0, i);
      } else if (sol == null) {
        feasible = false;
      }
      if (nbCutLearned != 0 || !feasible)
        problem.worldPopUntil(masterWorld);
      if (!feasible) break;
    }
  }

  public void storePartialSolution(int firstSpb, int lastSpb) {
    for (int i = firstSpb; i <= lastSpb; i++) {
      super.storePartialSolution(i);
    }
  }
}
