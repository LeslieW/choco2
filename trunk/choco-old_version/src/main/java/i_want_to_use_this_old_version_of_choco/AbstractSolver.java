// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.search.AbstractOptimize;
import i_want_to_use_this_old_version_of_choco.set.SetVar;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * An abstract class handling the control for solving a problem
 */
public abstract class AbstractSolver extends AbstractEntity {
  /**
   * The historical record of solutions that were found
   */
  public ArrayList solutions; //Solution[]

  /**
   * capacity of the history record (keeping solutions)
   */
  public int maxNbSolutionStored = 5;

  /**
   * an object for logging trace statements
   */
  protected static Logger logger = Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search");

  public AbstractSolver() {
    solutions = new ArrayList();
  }

  /**
   * recording the current state as a solution
   * stores information from the current state in the next solution of the problem
   * note: only instantiated variables are recorded in the Solution object
   * either all variables or a user-defined subset of them are recorded
   * this may erase a soolution that was previously stored in the ith position
   * this may also increase the size of the pb.solutions vector.
   */
  public void recordSolution() {
    Solution sol = makeSolutionFromCurrentState();
    storeSolution(sol);
  }

  protected Solution makeSolutionFromCurrentState() {
    int nbv = problem.getNbIntVars();
    Solution sol = new Solution(problem);
    // sol.time = time_read()
    for (int i = 0; i < nbv; i++) {
      IntDomainVar vari = (IntDomainVar) problem.getIntVar(i);
      if (vari.isInstantiated()) {
        sol.recordIntValue(i, vari.getVal());
      }
    }
    int nbsv = problem.getNbSetVars();
    for (int i = 0; i < nbsv; i++) {
      SetVar vari = problem.getSetVar(i);
      if (vari.isInstantiated()) {
        sol.recordSetValue(i, vari.getValue());
      }
    }
    int nbrv = problem.getNbRealVars();
    for (int i = 0; i < nbrv; i++) {
      RealVar vari = problem.getRealVar(i);
      //if (vari.isInstantiated()) { // Not always "instantiated" : for instance, if the branching
      // does not contain the variable, the precision can not be reached....
      sol.recordRealValue(i, vari.getValue());
      //}
    }
    if (this instanceof AbstractOptimize) {
      sol.recordIntObjective(((AbstractOptimize) this).getObjectiveValue());
    }
    return sol;
  }

  /**
   * showing information about the last solution
   */
  public void showSolution() {
    System.out.println(problem.pretty());
    // TODO
  }

  protected void storeSolution(Solution sol) {
    //[SVIEW] store solution ~S // sol,
    if (solutions.size() == maxNbSolutionStored) {
      solutions.remove(solutions.size() - 1);
    }
    solutions.add(0, sol);
  }

  public boolean existsSolution() {
    return (solutions.size() > 0);
  }

  public void restoreBestSolution() {
    ((Solution) solutions.get(0)).restore();
  }

  /**
   * main entry point: running the search algorithm controlled the Solver object
   * @deprecated
   */
  //public abstract void run();
}
