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

import choco.ecp.solver.explanations.dbt.PalmExplanation;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;

import java.util.ArrayList;


public class PalmUnsureRepair extends PalmRepair {

  public SConstraint selectDecisionToUndo(PalmExplanation expl) {
    PalmGlobalSearchStrategy strategy = this.getManager();
    PalmLearn learner = strategy.getLearning();
    ArrayList constraints = learner.sortConstraintToUndo(expl);
    int nbCandidates = constraints.size(), idx_ct_out = 0;
    boolean foundCandidate = false;
    AbstractSConstraint ct_out = null;
    if (!constraints.isEmpty()) {
      while (idx_ct_out < nbCandidates & !foundCandidate) {
        ct_out = (AbstractSConstraint) constraints.get(idx_ct_out);
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
