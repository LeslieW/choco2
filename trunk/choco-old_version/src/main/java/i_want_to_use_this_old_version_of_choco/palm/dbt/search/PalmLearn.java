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
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;

import java.util.*;

public class PalmLearn extends PalmAbstractSolverTool {
  public void learnFromContradiction(PalmExplanation expl) {
  }

  public void learnFromRemoval(Constraint constraint) {
  }

  public boolean checkAcceptable(List constraints) {
    return true;
  }

  public boolean checkAcceptableRelaxation(Constraint constraint) {
    return true;
  }

  public ArrayList sortConstraintToUndo(PalmExplanation expl) {
    PalmProblem pb = ((PalmProblem) this.getManager().getProblem());
    ArrayList list = new ArrayList();
    BitSet bset = expl.getBitSet();    // iterate on the bitset and avoid HashSet !!!!!!
    for (int i = bset.nextSetBit(0); i >= 0; i = bset.nextSetBit(i + 1)) {
      AbstractConstraint ct = (AbstractConstraint) pb.getConstraintNb(i);
      if (((PalmConstraintPlugin) (ct).getPlugIn()).getWeight() == 0)
        list.add(ct);
    }
    // We assume that all decision constraints use the same comparator defined by the user
    if (!list.isEmpty()) {
      Comparator Comp = ((PalmConstraintPlugin) ((AbstractConstraint) list.get(0)).getPlugIn()).getSearchInfo().getComparator();
      Collections.sort(list, Comp);
    }
    return list;
  }

}
