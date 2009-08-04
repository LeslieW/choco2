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


import choco.ecp.solver.PalmSolver;
import choco.ecp.solver.explanations.dbt.PalmConstraintPlugin;
import choco.ecp.solver.explanations.dbt.PalmExplanation;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;

import java.util.*;

public abstract class PalmLearn extends PalmAbstractSolverTool {
  public void learnFromContradiction(PalmExplanation expl) {
  }

  public abstract void learnFromRemoval(SConstraint constraint);

  public abstract boolean checkAcceptable(List constraints);

  public abstract boolean checkAcceptableRelaxation(SConstraint constraint);

  public ArrayList sortConstraintToUndo(PalmExplanation expl) {
    PalmSolver pb = ((PalmSolver) this.getManager().getSolver());
    ArrayList<AbstractSConstraint> list = new ArrayList<AbstractSConstraint>();
    BitSet bset = expl.getBitSet();    // iterate on the bitset and avoid HashSet !!!!!!
    for (int i = bset.nextSetBit(0); i >= 0; i = bset.nextSetBit(i + 1)) {
      AbstractSConstraint ct = pb.getConstraintNb(i);
      if (((PalmConstraintPlugin) (ct).getPlugIn()).getWeight() == 0)
        list.add(ct);
    }
    // We assume that all decision constraints use the same comparator defined by the user
    if (!list.isEmpty()) {
      Comparator<AbstractSConstraint> Comp = ((PalmConstraintPlugin) ((AbstractSConstraint) list.get(0)).getPlugIn()).getSearchInfo().getComparator();
      Collections.sort(list, Comp);
    }
    return list;
  }

}
