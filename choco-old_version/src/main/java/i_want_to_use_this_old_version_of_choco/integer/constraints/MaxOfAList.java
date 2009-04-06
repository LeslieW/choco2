package i_want_to_use_this_old_version_of_choco.integer.constraints;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;

/**
 * Implements a constraint X = max(Y_0, Y_1...Y_n).
 */
public class MaxOfAList extends AbstractLargeIntConstraint {
  /**
   * Index of the maximum variable.
   */
  public static final int MAX_INDEX = 0;
  /**
   * First index of the variables among which the maximum should be chosen.
   */
  public static final int VARS_OFFSET = 1;

  /**
   * Index of the maximum variable.
   */
  protected final IStateInt indexOfMaximumVariable;

  public MaxOfAList(final IntDomainVar[] vars) {
    super(vars);
    indexOfMaximumVariable = this.getProblem().getEnvironment().makeInt(-1);
  }

  /**
   * If only one candidate to be the max of the list, some additionnal
   * propagation can be performed (as in usual x == y constraint).
   */
  protected void onlyOneMaxCandidatePropagation() throws ContradictionException {
    int nbVars = vars.length;
    IntDomainVar maxVar = vars[MAX_INDEX];
    if (this.indexOfMaximumVariable.get() == -1) {
      int maxMax = Integer.MIN_VALUE, maxMaxIdx = -1;
      int maxMax2 = Integer.MIN_VALUE, maxMax2Idx = -1;
      for (int i = VARS_OFFSET; i < nbVars; i++) {
        int val = vars[i].getSup();
        if (val >= maxMax) {
          maxMax2 = maxMax;
          maxMax2Idx = maxMaxIdx;
          maxMax = val;
          maxMaxIdx = i;
        } else if (val > maxMax2) {
          maxMax2 = val;
          maxMax2Idx = i;
        }
      }
      if (maxMax2 < maxVar.getInf()) {
        this.indexOfMaximumVariable.set(maxMaxIdx);
      }
    }
    int idx = this.indexOfMaximumVariable.get();
    if (idx != -1) {
      maxVar.updateInf(vars[idx].getInf(),
          this.getConstraintIdx(MAX_INDEX));
      vars[idx].updateInf(maxVar.getInf(),
          this.getConstraintIdx(idx));
    }
  }

  /**
   * Checks if one of the variables in the list is instantiated to the max.
   *
   * @return true if one variables in the list is instantaited to the max.
   */
  protected boolean testIfOneCandidateToTakeMaxValue() {
    int maxValue = vars[MAX_INDEX].getVal();
    int nbVars = vars.length;
    boolean existsInstantiated = false;
    for (int i = VARS_OFFSET; i < nbVars; i++) {
      if (vars[i].getSup() >= maxValue) return false;
      if (vars[i].getInf() == maxValue) existsInstantiated = true;
    }
    return existsInstantiated;
  }

  protected final int maxInf() {
    int nbVars = vars.length;
    int max = Integer.MIN_VALUE;
    for (int i = VARS_OFFSET; i < nbVars; i++) {
      int val = vars[i].getInf();
      if (val > max) max = val;
    }
    return max;
  }

  protected final int maxSup() {
    int nbVars = vars.length;
    int max = Integer.MIN_VALUE;
    for (int i = VARS_OFFSET; i < nbVars; i++) {
      int val = vars[i].getSup();
      if (val > max) max = val;
    }
    return max;
  }

  /**
   * Propagation of the constraint. It should be called only with initial
   * propagation here, since no constraint events are posted.
   *
   * @throws i_want_to_use_this_old_version_of_choco.ContradictionException if a domain becomes empty.
   */
  public void propagate() throws ContradictionException {
    int nbVars = vars.length;
    IntDomainVar maxVar = vars[MAX_INDEX];
    maxVar.updateInf(maxInf(), this.getConstraintIdx(MAX_INDEX));
    maxVar.updateSup(maxSup(), this.getConstraintIdx(MAX_INDEX));
    int maxValue = maxVar.getSup();
    for (int i = VARS_OFFSET; i < nbVars; i++) {
      vars[i].updateSup(maxValue, this.getConstraintIdx(i));
    }
    onlyOneMaxCandidatePropagation();
  }

  /**
   * Propagation when lower bound is increased.
   *
   * @param idx the index of the modified variable.
   * @throws ContradictionException if a domain becomes empty.
   */
  public void awakeOnInf(final int idx) throws ContradictionException {
    if (idx >= VARS_OFFSET) { // Variable in the list
      vars[MAX_INDEX].updateInf(maxInf(), getConstraintIdx(MAX_INDEX));
    } else { // Maximum variable
      onlyOneMaxCandidatePropagation();
    }
  }

  /**
   * Propagation when upper bound is decreased.
   *
   * @param idx the index of the modified variable.
   * @throws ContradictionException if a domain becomes empty.
   */
  public void awakeOnSup(final int idx) throws ContradictionException {
    if (idx >= VARS_OFFSET) { // Variable in the list
      vars[MAX_INDEX].updateSup(maxSup(), getConstraintIdx(MAX_INDEX));
      onlyOneMaxCandidatePropagation();
    } else { // Maximum variable
      int nbVars = vars.length;
      int maxVal = vars[MAX_INDEX].getSup();
      for (int i = VARS_OFFSET; i < nbVars; i++) {
        vars[i].updateSup(maxVal, getConstraintIdx(i));
      }
    }
  }

  /**
   * Propagation when a variable is instantiated.
   *
   * @param idx the index of the modified variable.
   * @throws ContradictionException if a domain becomes empty.
   */
  public void awakeOnInst(final int idx) throws ContradictionException {
    if (idx >= VARS_OFFSET) { // Variable in the list
      IntDomainVar maxVar = vars[MAX_INDEX];
      maxVar.updateInf(maxInf(), this.getConstraintIdx(MAX_INDEX));
      maxVar.updateSup(maxSup(), this.getConstraintIdx(MAX_INDEX));
    } else { // Maximum variable
      int nbVars = vars.length;
      int maxValue = vars[MAX_INDEX].getSup();
      for (int i = VARS_OFFSET; i < nbVars; i++) {
        vars[i].updateSup(maxValue, this.getConstraintIdx(i));
      }
      onlyOneMaxCandidatePropagation();
    }
  }

  public Boolean isEntailed() {
    throw new Error("isEntailed not yet implemented on MaxOfAList");
    //return null;
  }

  public boolean isSatisfied(int[] tuple) {
    int maxValue = tuple[MAX_INDEX]; //vars[MAX_INDEX].getVal();
    for(int i = VARS_OFFSET; i < vars.length; i++) {
      if (maxValue < tuple[i]) return false;
    }
    return true;
  }

  public String pretty() {
    StringBuilder sb = new StringBuilder();
    sb.append(vars[MAX_INDEX].pretty()).append(" = max({");
    for(int i = VARS_OFFSET; i < vars.length; i++) {
      if (i > VARS_OFFSET) sb.append(", ");
      sb.append(vars[i].pretty());
    }
    sb.append("})");
    return sb.toString();
  }
}
