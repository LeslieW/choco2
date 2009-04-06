package i_want_to_use_this_old_version_of_choco.palm.global.matching;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.global.matching.GlobalCardinality;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedConstraintPlugin;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.PalmConstraint;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntDomain;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;
import i_want_to_use_this_old_version_of_choco.palm.integer.PalmIntVarListener;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class PalmCardinality extends GlobalCardinality implements PalmConstraint, PalmIntVarListener {
  private Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.prop.const");

  public PalmCardinality(IntDomainVar[] vars, int minValue, int maxValue, int[] low, int[] up) {
    super(vars, minValue, maxValue, low, up);
    this.hook = ((ExplainedProblem) this.getProblem()).makeConstraintPlugin(this);

  }

  public PalmCardinality(IntDomainVar[] vars, int[] low, int[] up) {
    super(vars, low, up);
    this.hook = ((ExplainedProblem) this.getProblem()).makeConstraintPlugin(this);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public void deleteEdgeAndPublish(int i, int j) throws ContradictionException {
    this.deleteMatch(i, j);
    Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
    int compi = this.component[i];
    int compj = this.component[j + this.minValue];

    ((ExplainedConstraintPlugin) this.hook).self_explain(expl);
    for (int i2 = 0; i2 < this.nbLeftVertices; i2++) {
      int compi2 = this.component[i2];
      if (this.componentOrder[compi][compi2]) {
        for (int j2 = 0; j2 < this.nbRightVertices; j2++) {
          int compj2 = this.component[j2 + this.nbLeftVertices];
          if (this.componentOrder[compj2][compj]) {
            ((ExplainedIntVar) this.vars[i2]).self_explain(ExplainedIntDomain.VAL, j2 + this.minValue, expl);
          }
        }
      }
    }

    if (this.componentOrder[this.component[this.source]][this.component[j + this.nbLeftVertices]]) { // toujours vrai !!
      for (int i2 = 0; i2 < this.nbLeftVertices; i2++) {
        //for (int j2=0; j2 < n2; j2 ++) {
        int j2 = this.match(i2);
        //int i2 = this.inverseMatch(j2);
        if ((j2 != -1)) {   // a priori c sur...
          if ((this.componentOrder[this.component[i]][this.component[j2 + this.nbLeftVertices]])) {
            for (int k = 1; k < this.nbRightVertices; k++) {
              //if (this.componentOrder[this.component[j2 + n1]][this.component[k + n1]]) {    // TODO : voir si ca serait bon !!
              if (this.component[k + this.nbLeftVertices] != this.component[j2 + this.nbLeftVertices]) {
                ((ExplainedIntDomain) this.vars[i2].getDomain()).self_explain(ExplainedIntDomain.VAL, k + this.minValue, expl);
              }
            }
          }
        }
      }
    }

    //if (this.componentOrder[this.component[i]][this.component[this.source]]) {
    //System.err.println("Should do something here !!??");
    //}


    /*for (int k = 0; k < vars.length; k++) {
      IntDomainVar var = vars[k];
      ((ExplainedIntVar)var).self_explain(ExplainedIntDomain.DOM, expl);
    } */

    ((ExplainedIntVar) this.vars[i]).removeVal(j + this.minValue, this.cIndices[i], expl);
  }

  public void takeIntoAccountStatusChange(int index) {
  }

  public void updateDataStructuresOnConstraint(int idx, int select, int newValue, int oldValue) {
    this.deleteMatch(idx, newValue - this.minValue);
    this.constAwake(false);
  }

  public void updateDataStructuresOnRestoreConstraint(int idx, int select, int newValue, int oldValue) {
    if (this.matchingSize.get() < this.nbLeftVertices || this.component[idx] != this.component[newValue - this.minValue + this.nbLeftVertices]) {
      this.constAwake(false);
    }
  }

  public void awakeOnRem(int idx, int val) {
  }

  public void awakeOnRestoreInf(int idx) throws ContradictionException {
  }

  public void awakeOnRestoreSup(int idx) throws ContradictionException {
  }

  public void awakeOnRestoreVal(int idx, int val) throws ContradictionException {
  }

  public void awakeOnRestoreVal(int idx, IntIterator it) throws ContradictionException {
    for (; it.hasNext();) {
      awakeOnRestoreVal(idx, it.next());
    }
  }

  public void awake() throws ContradictionException {
    for (int i = 0; i < this.nbLeftVertices; i++) {
      Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
      ((ExplainedConstraintPlugin) this.hook).self_explain(expl);

      ((ExplainedIntVar) this.vars[i]).updateInf(this.minValue, this.cIndices[i], (Explanation) expl.copy());
      ((ExplainedIntVar) this.vars[i]).updateSup(this.maxValue, this.cIndices[i], expl);
    }
    this.propagate();
  }

  public Set whyIsTrue() {
    return null;
  }

  public Set whyIsFalse() {
    return null;
  }

  public void augmentFlow() throws ContradictionException {
    int eopath = this.findAlternatingPath();
    int n1 = this.nbLeftVertices;

    if (this.matchingSize.get() < n1) {
      if (logger.isLoggable(Level.INFO)) this.logger.info("Current flow of size: " + this.matchingSize.get());
      while (eopath >= 0) {
        this.augment(eopath);
        eopath = this.findAlternatingPath();
      }
      if (this.matchingSize.get() < n1) {
        // assert exist i, 0 <= i < n1, this.match(i) == 0
        if (logger.isLoggable(Level.INFO)) logger.info("There exists no perfect matching.");
        //this.fail();
        Explanation expl = ((ExplainedProblem) this.getProblem()).makeExplanation();
        ((ExplainedConstraintPlugin) this.hook).self_explain(expl);
        for (int i = 0; i < vars.length; i++) {
          IntDomainVar var = vars[i];
          ((ExplainedIntDomain) var.getDomain()).self_explain(ExplainedIntDomain.DOM, expl);
        }
        ((ExplainedProblem) this.getProblem()).explainedFail(expl);
      } else {
        if (logger.isLoggable(Level.INFO)) {
          logger.info("Found a perfect metching (size: " + this.matchingSize.get() + ").");
          for (int i = 0; i < this.nbLeftVertices; i++)
            logger.info("Match " + i + " with " + this.match(i));
        }
        // TODO CheckFlow ...
      }
    }
  }
}
