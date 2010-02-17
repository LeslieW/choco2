/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.integer;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Implements a constraint X = min(Y_0, Y_1...Y_n).
 */
public class MinOfAList extends AbstractLargeIntSConstraint {
  /**
   * Index of the minimum variable.
   */
  public static final int MIN_INDEX = 0;
  /**
   * First index of the variables among which the minimum should be chosen.
   */
  public static final int VARS_OFFSET = 1;

  /**
   * Index of the minimum variable.
   */
  protected final IStateInt indexOfMinimumVariable;

  public MinOfAList(final IntDomainVar[] vars, IEnvironment environment) {
    super(vars);
    indexOfMinimumVariable = environment.makeInt(-1);
  }

  public int getFilteredEventMask(int idx) {
    return IntVarEvent.INSTINTbitvector + IntVarEvent.BOUNDSbitvector;
    // return 0x0B;
  }
  /**
   * If only one candidate to be the min of the list, some additionnal
   * propagation can be performed (as in usual x == y constraint).
   */
  protected void onlyOneMaxCandidatePropagation() throws ContradictionException {
    int nbVars = vars.length;
    IntDomainVar minVar = vars[MIN_INDEX];
    if (this.indexOfMinimumVariable.get() == -1) {
      int minMin = Integer.MAX_VALUE, minMinIdx = -1;
      int minMin2 = Integer.MAX_VALUE, minMin2Idx = -1;
      for (int i = VARS_OFFSET; i < nbVars; i++) {
        int val = vars[i].getInf();
        if (val <= minMin) {
          minMin2 = minMin;
          minMin2Idx = minMinIdx;
          minMin = val;
          minMinIdx = i;
        } else if (val < minMin2) {
          minMin2 = val;
          minMin2Idx = i;
        }
      }
      if (minMin2 > minVar.getSup()) {
        this.indexOfMinimumVariable.set(minMinIdx);
      }
    }
    int idx = this.indexOfMinimumVariable.get();
    if (idx != -1) {
      minVar.updateSup(vars[idx].getSup(),
          this.getConstraintIdx(MIN_INDEX));
      vars[idx].updateSup(minVar.getSup(),
          this.getConstraintIdx(idx));
    }
  }

  /**
   * Checks if one of the variables in the list is instantiated to the min.
   *
   * @return true if one variables in the list is instantaited to the min.
   */
  protected boolean testIfOneCandidateToTakeMaxValue() {
    int minValue = vars[MIN_INDEX].getVal();
    int nbVars = vars.length;
    boolean existsInstantiated = false;
    for (int i = VARS_OFFSET; i < nbVars; i++) {
      if (vars[i].getInf() <= minValue) {
		return false;
	}
      if (vars[i].getSup() == minValue) {
		existsInstantiated = true;
	}
    }
    return existsInstantiated;
  }

  protected final int minInf() {
    int nbVars = vars.length;
    int min = Integer.MAX_VALUE;
    for (int i = VARS_OFFSET; i < nbVars; i++) {
      int val = vars[i].getInf();
      if (val < min) {
		min = val;
	}
    }
    return min;
  }

  protected final int minSup() {
    int nbVars = vars.length;
    int min = Integer.MAX_VALUE;
    for (int i = VARS_OFFSET; i < nbVars; i++) {
      int val = vars[i].getSup();
      if (val < min) {
		min = val;
	}
    }
    return min;
  }

  /**
   * Propagation of the constraint. It should be called only with initial
   * propagation here, since no constraint events are posted.
   *
   * @throws ContradictionException if a domain becomes empty.
   */
  @Override
public void propagate() throws ContradictionException {
    int nbVars = vars.length;
    IntDomainVar minVar = vars[MIN_INDEX];
    minVar.updateInf(minInf(), this.getConstraintIdx(MIN_INDEX));
    minVar.updateSup(minSup(), this.getConstraintIdx(MIN_INDEX));
    int minValue = minVar.getInf();
    for (int i = VARS_OFFSET; i < nbVars; i++) {
      vars[i].updateInf(minValue, this.getConstraintIdx(i));
    }
    onlyOneMaxCandidatePropagation();
  }

  /**
   * Propagation when lower bound is increased.
   *
   * @param idx the index of the modified variable.
   * @throws ContradictionException if a domain becomes empty.
   */
  @Override
public void awakeOnInf(final int idx) throws ContradictionException {
    if (idx >= VARS_OFFSET) { // Variable in the list
      vars[MIN_INDEX].updateInf(minInf(), getConstraintIdx(MIN_INDEX));
      onlyOneMaxCandidatePropagation();
    } else { // Minimum variable
      int nbVars = vars.length;
      int minVal = vars[MIN_INDEX].getInf();
      for (int i = VARS_OFFSET; i < nbVars; i++) {
        vars[i].updateInf(minVal, getConstraintIdx(i));
      }
    }
  }

  /**
   * Propagation when upper bound is decreased.
   *
   * @param idx the index of the modified variable.
   * @throws ContradictionException if a domain becomes empty.
   */
  @Override
public void awakeOnSup(final int idx) throws ContradictionException {
    if (idx >= VARS_OFFSET) { // Variable in the list
      vars[MIN_INDEX].updateSup(minSup(), getConstraintIdx(MIN_INDEX));
    } else { // Maximum variable
      onlyOneMaxCandidatePropagation();
    }
  }

  /**
   * Propagation when a variable is instantiated.
   *
   * @param idx the index of the modified variable.
   * @throws ContradictionException if a domain becomes empty.
   */
  @Override
public void awakeOnInst(final int idx) throws ContradictionException {
    if (idx >= VARS_OFFSET) { // Variable in the list
      IntDomainVar minVar = vars[MIN_INDEX];
      minVar.updateInf(minInf(), this.getConstraintIdx(MIN_INDEX));
      minVar.updateSup(minSup(), this.getConstraintIdx(MIN_INDEX));
    } else { // Maximum variable
      int nbVars = vars.length;
      int minValue = vars[MIN_INDEX].getInf();
      for (int i = VARS_OFFSET; i < nbVars; i++) {
        vars[i].updateInf(minValue, this.getConstraintIdx(i));
      }
      onlyOneMaxCandidatePropagation();
    }
  }

    @Override
    public Boolean isEntailed() {
        int minInf = vars[MIN_INDEX].getInf();
        int minSup = vars[MIN_INDEX].getSup();

        int cptIn = 0;
        int cptBelow = 0;
        IntDomainVar tmp;
        for(int i = VARS_OFFSET; i < vars.length; i++) {
            tmp = vars[i];
            int inf = tmp.getInf();
            int sup = tmp.getSup();
            if(inf == minInf
                    && minSup == sup
                    && sup == inf){
                cptIn++;
            }else if(sup < minInf){
                return Boolean.FALSE;
            }else if(inf > minSup){
                cptBelow++;
            }
        }
        if(cptBelow == vars.length-1)return Boolean.FALSE;
        if(cptIn > 0)return Boolean.TRUE;
        return null;
    }

    @Override
    public boolean isSatisfied(int[] tuple) {
    int minValue = Integer.MAX_VALUE;
    for(int i = VARS_OFFSET; i < vars.length; i++) {
      if (minValue > tuple[i]) {
        minValue = tuple[i];
      }
    }
    return tuple[MIN_INDEX] == minValue;
  }

  @Override
public String pretty() {
    StringBuilder sb = new StringBuilder();
    sb.append(vars[MIN_INDEX].pretty()).append(" = min({");
    for(int i = VARS_OFFSET; i < vars.length; i++) {
      if (i > VARS_OFFSET) {
		sb.append(", ");
	}
      sb.append(vars[i].pretty());
    }
    sb.append("})");
    return sb.toString();
  }
}