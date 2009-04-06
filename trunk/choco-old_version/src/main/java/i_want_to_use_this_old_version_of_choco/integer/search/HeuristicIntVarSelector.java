package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;

import java.util.List;


/**
 * A class the selects the variables which minimizes a heuristic
 *  (such classes support ties)
 */
public abstract class HeuristicIntVarSelector extends AbstractIntVarSelector {
  public HeuristicIntVarSelector(AbstractProblem problem) {
    this.problem = problem;
  }
  /**
   * @param vars the set of vars among which the variable is returned
   * @return the first variable minimizing a given heuristic
   */
  public abstract IntDomainVar getMinVar(List<IntDomainVar> vars) throws ContradictionException;

  /**
   * @param vars the set of vars among which the variable is returned
   * @return the first variable minimizing a given heuristic
   */
  public abstract IntDomainVar getMinVar(IntDomainVar[] vars) throws ContradictionException;

  /**
    * @param p the problem
    * @return the first variable minimizing a given heuristic among all variables of the problem
    */
  public abstract IntDomainVar getMinVar(AbstractProblem p) throws ContradictionException;


  public IntDomainVar selectIntVar() throws ContradictionException {
    if (null != vars)
      return getMinVar(vars);
    else
      return getMinVar(problem);
  }

  public IntDomainVar getMinVar(IntConstraint c) throws ContradictionException {
    IntDomainVar[] vars = new IntDomainVar[c.getNbVars()];
    for(int i = 0; i < c.getNbVars(); i++) {
      vars[i] = c.getIntVar(i);
    }
    return getMinVar(vars);
  }

  public abstract List<IntDomainVar> getAllMinVars(AbstractProblem p) throws ContradictionException;

  public abstract List<IntDomainVar> getAllMinVars(IntConstraint c) throws ContradictionException;  



  public List<IntDomainVar> selectTiedIntVars() throws ContradictionException {
      return getAllMinVars(problem);
  }

}
