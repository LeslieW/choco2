// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.real.RealInterval;

import java.util.logging.Logger;

/**
 * A class storing a state of the problem
 */
public class Solution {
  protected static final Logger logger = Logger.getLogger("choco");

  /**
   * the problem owning the solution
   */
  protected AbstractProblem problem;

  /**
   * data storage for values of search variables
   */
  protected int[] intVarValues;

  protected RealInterval[] realVarValues;

  protected int[][] setVarValues;

  protected int objectiveValue;

  /**
   * Constructor
   *
   * @param pb the problem owning the solution
   */
  public Solution(AbstractProblem pb) {
    problem = pb;
    int nbv = problem.getNbIntVars();
    intVarValues = new int[nbv];
    setVarValues = new int[problem.getNbSetVars()][];
    realVarValues = new RealInterval[problem.getNbRealVars()];
    for (int i = 0; i < nbv; i++) {
      intVarValues[i] = Integer.MAX_VALUE;
    }
    objectiveValue = Integer.MAX_VALUE;
  }

  public void recordIntValue(int intVarIndex, int intVarValue) {
    intVarValues[intVarIndex] = intVarValue;
  }

  public void recordSetValue(int setVarIndex, int[] setVarValue) {
    setVarValues[setVarIndex] = setVarValue;
  }

  public void recordRealValue(int realVarIndex, RealInterval realVarValue) {
    realVarValues[realVarIndex] = realVarValue;
  }

  public void recordIntObjective(int intObjectiveValue) {
    objectiveValue = intObjectiveValue;
  }

  public void restore() {
    try {
      for (int i = 0; i < intVarValues.length; i++) {
        if (intVarValues[i] != Integer.MAX_VALUE) {
          ((IntDomainVar) problem.getIntVar(i)).setVal(intVarValues[i]);
        }
      }
      for (int i = 0; i < realVarValues.length; i++) {
        problem.getRealVar(i).intersect(realVarValues[i]);
      }
      for (int i = 0; i < setVarValues.length; i++) {
        problem.getSetVar(i).setVal(setVarValues[i]);
      }
      problem.propagate();
    } catch (ContradictionException e) {
      logger.severe("BUG in restoring solution !!!!!!!!!!!!!!!!");
      throw(new Error("Restored solution not consistent !!"));
      // TODO : � voir comment g�rer les erreurs en g�n�ral
      // TODO
    }
  }

  /**
   * Accessor to the value of a variable in a solution
   *
   * @param varIndex the index of the variable among all variables of the problem
   * @return its value (whenever it is instantiated in the solution), or Integer.MAX_VALUE otherwise
   */
  public int getValue(int varIndex) {
    return intVarValues[varIndex];
  }

  public int[] getSetValue(int varIndex) {
    return setVarValues[varIndex];
  }

  public RealInterval getRealValue(int varIndex) {
    return realVarValues[varIndex];
  }
}
