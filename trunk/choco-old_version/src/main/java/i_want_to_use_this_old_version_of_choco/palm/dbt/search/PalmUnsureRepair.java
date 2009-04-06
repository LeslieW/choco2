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

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;

import java.util.ArrayList;


public class PalmUnsureRepair extends PalmRepair {

  public Constraint selectDecisionToUndo(PalmExplanation expl) {
    PalmGlobalSearchSolver solver = this.getManager();
    PalmLearn learner = solver.getLearning();
    ArrayList constraints = learner.sortConstraintToUndo(expl);
    int nbCandidates = constraints.size(), idx_ct_out = 0;
    boolean foundCandidate = false;
    AbstractConstraint ct_out = null;
    if (!constraints.isEmpty()) {
      while (idx_ct_out < nbCandidates & !foundCandidate) {
        ct_out = (AbstractConstraint) constraints.get(idx_ct_out);
        foundCandidate = learner.checkAcceptableRelaxation(ct_out);
        idx_ct_out++;
      }
      //((PathRepairLearn) learner).debugMemory();
      if (foundCandidate)
        return ct_out;
      else
        return null;
    } else
      return null;
  }

}
