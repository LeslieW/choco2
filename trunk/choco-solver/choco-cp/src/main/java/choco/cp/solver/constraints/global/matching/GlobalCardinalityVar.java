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
package choco.cp.solver.constraints.global.matching;

import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Simple implementation of global cardinality constraint with occurrence constrained by
 * variables and not only integer bounds.
 */
public final class GlobalCardinalityVar extends GlobalCardinality {
  //protected StoredIntVector minOccurence;
  //protected StoredIntVector maxOccurence;

  public GlobalCardinalityVar(IntDomainVar[] values,
                              IntDomainVar[] occurences, IEnvironment environment) {
    this(values, 1, occurences.length, occurences, environment);
  }

  public GlobalCardinalityVar(IntDomainVar[] values,
                              int minValue, int maxValue,
                              IntDomainVar[] occurences, IEnvironment environment) {
    super(values, minValue, maxValue, new int[occurences.length], new int[occurences.length], environment);
    int nbVarsTotal = values.length + occurences.length;
    vars = new IntDomainVar[nbVarsTotal];
    System.arraycopy(values, 0, vars, 0, values.length);
    System.arraycopy(occurences, 0, vars, values.length, occurences.length);
    cIndices = new int[nbVarsTotal];
    //minOccurence = getProblem().getEnvironment().makeIntVector(occurencesMin.length, -1);
    //maxOccurence = getProblem().getEnvironment().makeIntVector(occurencesMin.length, -1);
  }


  public void awakeOnInf(int idx) throws ContradictionException {
    if (idx < nbLeftVertices) super.awakeOnInf(idx);
    else {
      checkSumInfs();
      deleteSupport();
      this.constAwake(false);
    }
  }

  private void checkSumInfs() throws ContradictionException {
    int sum = 0;
    for(int j = 0; j < nbRightVertices; j++) {
      sum += vars[j + nbLeftVertices].getInf();
    }
    if (sum > nbLeftVertices) this.fail();
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    if (idx < nbLeftVertices) super.awakeOnRem(idx, x);
    else {
      deleteSupport();
      this.constAwake(false);
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    if (idx < nbLeftVertices) super.awakeOnSup(idx);
    else {
      deleteSupport();
      this.constAwake(false);
    }
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    if (idx < nbLeftVertices) super.awakeOnInst(idx);
    else {
      checkSumInfs();
      deleteSupport();
      this.constAwake(false);
    }
  }

  public void awake() throws ContradictionException {
    super.awake();
  }

  public void deleteSupport() {
    for(int i = 0; i < nbLeftVertices; i++) {
      refMatch.set(i, -1);
    }
    for(int j = 0; j < nbRightVertices; j++) {
      flow.set(j, 0);
    }
    matchingSize.set(0);
  }

  protected int getMinFlow(int j) {
    return vars[nbLeftVertices + j].getInf();
  }

  protected int getMaxFlow(int j) {
    return vars[nbLeftVertices + j].getSup();
  }

  public boolean isSatisfied(int[] tuple) {
    int[] occurrences = new int[this.maxValue - this.minValue + 1];
    int nbvar = tuple.length - occurrences.length;
	for (int i = 0; i < nbvar; i++) {
      occurrences[tuple[i]-this.minValue]++;
    }
    for (int i = 0; i < occurrences.length; i++) {
      int occurrence = occurrences[i];
      if (tuple[i + nbvar] != occurrence)
        return false;
    }
    return true;
  }

}
