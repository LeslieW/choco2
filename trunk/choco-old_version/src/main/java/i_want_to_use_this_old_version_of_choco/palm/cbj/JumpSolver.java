package i_want_to_use_this_old_version_of_choco.palm.cbj;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.IncreasingDomain;
import i_want_to_use_this_old_version_of_choco.integer.search.MinDomain;
import i_want_to_use_this_old_version_of_choco.integer.var.IntDomainVarImpl;
import i_want_to_use_this_old_version_of_choco.palm.cbj.search.*;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.real.search.AbstractRealOptimize;
import i_want_to_use_this_old_version_of_choco.real.search.RealBranchAndBound;
import i_want_to_use_this_old_version_of_choco.real.search.RealOptimizeWithRestarts;
import i_want_to_use_this_old_version_of_choco.search.NodeLimit;
import i_want_to_use_this_old_version_of_choco.search.TimeLimit;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class JumpSolver extends Solver {

  public JumpSolver(AbstractProblem pb) {
    super(pb);
  }

  public void generateSearchSolver(AbstractProblem pb) {
    problem = pb;
    if (null == objective) {
      solver = new JumpGlobalSearchSolver(this.getProblem());
    } else if (restart) {
      if (objective instanceof IntDomainVar)
        solver = new JumpRestartOptimizer((IntDomainVarImpl) objective, doMaximize);
      else if (objective instanceof RealVar)
        solver = new RealOptimizeWithRestarts((RealVar) objective, doMaximize);
    } else {
      if (objective instanceof IntDomainVar)
        solver = new JumpBranchAndBoundOptimizer((IntDomainVarImpl) objective, doMaximize);
      else if (objective instanceof RealVar)
        solver = new RealBranchAndBound((RealVar) objective, doMaximize);
    }
    solver.stopAtFirstSol = firstSolution;

    solver.limits.add(new TimeLimit(solver, timeLimit));
    solver.limits.add(new NodeLimit(solver, nodeLimit));

    generateGoal(pb);
  }

  protected void generateGoal(AbstractProblem pb) {
    if (varIntSelector == null) varIntSelector = new MinDomain(pb);
    if (valIntIterator == null && valIntSelector == null) valIntIterator = new IncreasingDomain();
    if (valIntIterator != null)
      attachGoal(new JumpAssignVar(varIntSelector, valIntIterator));
    else
      attachGoal(new JumpAssignVar(varIntSelector, valIntSelector));
  }

  public Number getOptimumValue() {
    if (solver instanceof JumpAbstractOptimizer) {
      return ((JumpAbstractOptimizer) solver).getBestObjectiveValue();
    } else if (solver instanceof AbstractRealOptimize) {
      return ((AbstractRealOptimize) solver).getBestObjectiveValue();
    }
    return null;
  }
}
