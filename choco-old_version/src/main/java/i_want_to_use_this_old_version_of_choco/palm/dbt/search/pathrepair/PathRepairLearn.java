package i_want_to_use_this_old_version_of_choco.palm.dbt.search.pathrepair;

import i_want_to_use_this_old_version_of_choco.AbstractConstraint;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.ConstraintCollection;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.PalmGlobalSearchSolver;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.PalmLearn;
import i_want_to_use_this_old_version_of_choco.palm.search.NogoodConstraint;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;

public class PathRepairLearn extends PalmLearn {

  protected int maxSize = 7;
  protected NogoodConstraint explanations;

  public PathRepairLearn() {
  }

  public PathRepairLearn(int lSize, NogoodConstraint ngc) {
    this.maxSize = lSize;
    explanations = ngc;
  }

  public PathRepairLearn(int lSize) {
    this(lSize, null);
  }

  public void setMemory(NogoodConstraint exp) {
    this.explanations = exp;
  }

  public void addSolution() {
    PalmGlobalSearchSolver man = this.getManager();
    explanations.addPermanentNogood(man.getState().getPath().copy());
  }

  /**
   * Update the tabou list of nogood
   *
   * @param nogood
   */
  public void addForbiddenSituation(ConstraintCollection nogood) {
    if (explanations.getMemory().size() == maxSize) {
      explanations.removeLastNogood();
    }
    explanations.addNogoodFirst(nogood);
  }


  public void learnFromContradiction(PalmExplanation expl) {
    PalmGlobalSearchSolver man = this.getManager();
    PalmProblem pb = ((PalmProblem) man.getProblem());
    BitSet bset = expl.getBitSet();    // iterate on the bitset and avoid HashSet !!!!!!
    PalmExplanation nogood = (PalmExplanation) pb.makeExplanation();  // create the nogood associated to the conflict
    for (int i = bset.nextSetBit(0); i >= 0; i = bset.nextSetBit(i + 1)) {
      AbstractConstraint ct = pb.getConstraintNb(i);
      if (((PalmConstraintPlugin) (ct).getPlugIn()).getWeight() == 0)
        nogood.add(ct);
    }
    addForbiddenSituation(nogood);                    // add it in the tabou list
    informConstraintsInExplanation(nogood);
    //System.out.print("Nogood obtenu : ");
    //debugNogood(nogood);
    //System.out.println();
  }

  /**
   * maintain the searchInfo parameter on each constraint concerned by the conflict
   *
   * @param expl
   */
  public void informConstraintsInExplanation(PalmExplanation expl) {
    if (!expl.isEmpty()) {
      PalmProblem pb = ((PalmProblem) this.getManager().getProblem());
      float sCoef = 1 / (float) expl.size();
      //Iterator it = expl.toSet().iterator();
      BitSet bset = expl.getBitSet();    // iterate on the bitset and avoid HashSet !!!!!!
      for (int i = bset.nextSetBit(0); i >= 0; i = bset.nextSetBit(i + 1)) {
        AbstractConstraint ct = pb.getConstraintNb(i);
        PathRepairSearchInfo sInfo = (PathRepairSearchInfo) ((PalmConstraintPlugin) (ct).getPlugIn()).getSearchInfo();
        if (sInfo == null) {
          sInfo = new PathRepairSearchInfo();
          ((PalmConstraintPlugin) (ct).getPlugIn()).setSearchInfo(sInfo);
        }
        sInfo.add(sCoef);
      }
    }
  }

  public void learnFromRemoval(AbstractConstraint ct) {
    PathRepairSearchInfo sInfo = (PathRepairSearchInfo) ((PalmConstraintPlugin) (ct).getPlugIn()).getSearchInfo();
    sInfo.set(0);
  }


  private void debugNogood(PalmExplanation nogood) {
    Set no = nogood.toSet();
    Iterator it = no.iterator();
    while (it.hasNext()) {
      AbstractConstraint ct = (AbstractConstraint) it.next();
      System.out.print("" + ct + " : " + ((PathRepairSearchInfo) ((PalmConstraintPlugin) (ct).getPlugIn()).getSearchInfo()).getWeigth() + " ");
    }
  }

  private void debugMemory() {
    System.out.println("-----------");
    System.out.print("Chemin de decision :");
    debugDecisionPath();
    System.out.println("Memoire de DR :");
    Iterator it = explanations.getMemory().listIterator();
    for (; it.hasNext();) {
      debugNogood((PalmExplanation) it.next());
      System.out.println();
    }
    System.out.println("-----------");
  }

  private void debugDecisionPath() {
    Iterator it = this.getManager().getState().getPath().toSet().iterator();
    for (; it.hasNext();) {
      Constraint ct = (Constraint) it.next();
      System.out.print(" - " + ct);
    }
    System.out.println();
  }

  public void assertValidSearchInfo(PalmExplanation expl) {
    Iterator it = expl.toSet().iterator();
    for (; it.hasNext();) {
      AbstractConstraint ct = (AbstractConstraint) it.next();
      if (((PalmConstraintPlugin) (ct).getPlugIn()).getWeight() == 0) {
        PathRepairSearchInfo sInfo = (PathRepairSearchInfo) ((PalmConstraintPlugin) (ct).getPlugIn()).getSearchInfo();
        assert(sInfo != null);
      }
    }
  }
}
