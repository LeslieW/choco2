package i_want_to_use_this_old_version_of_choco.palm.benders.explain;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Propagator;
import i_want_to_use_this_old_version_of_choco.integer.search.AssignVar;
import i_want_to_use_this_old_version_of_choco.palm.JumpProblem;
import i_want_to_use_this_old_version_of_choco.palm.benders.search.MasterGlobalSearchSolver;
import i_want_to_use_this_old_version_of_choco.palm.cbj.explain.JumpExplanation;
import i_want_to_use_this_old_version_of_choco.palm.integer.ExplainedIntVar;
import i_want_to_use_this_old_version_of_choco.palm.search.Assignment;
import i_want_to_use_this_old_version_of_choco.search.IntBranchingTrace;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class BendersExplanation extends JumpExplanation {

  public BendersExplanation(AbstractProblem pb) {
    super(pb);
  }

  public BendersExplanation(int level, AbstractProblem pb) {
    super(level, pb);
  }

  public Propagator getConstraint(int i) {
    return (Propagator) getMasterConstraint(i);
  }

  public Constraint getMasterConstraint(int i) {
    IntBranchingTrace btrace = (IntBranchingTrace) ((MasterGlobalSearchSolver) ((JumpProblem) pb).getSolver().getSearchSolver()).getMaster().traceStack.get(i - 1);
    if (btrace.getBranching() instanceof AssignVar)
      return new Assignment((ExplainedIntVar) btrace.getBranchingObject(), btrace.getBranchIndex());
    else {
      throw new UnsupportedOperationException("the branching " + btrace.getBranching() + " is not yet supported by the JumpExplanation");
    }
  }

  public Constraint getCurrentSubConstraint(int i) {
    // The initial propagation is not stored in the stack of subproblems, the index is therefore used directly (i instead i-1 for the master)
    IntBranchingTrace btrace = (IntBranchingTrace) ((MasterGlobalSearchSolver) ((JumpProblem) pb).getSolver().getSearchSolver()).getSubproblems().traceStack.get(i);
    if (btrace.getBranching() instanceof AssignVar)
      return new Assignment((ExplainedIntVar) btrace.getBranchingObject(), btrace.getBranchIndex());
    else {
      throw new UnsupportedOperationException("the branching " + btrace.getBranching() + " is not yet supported by the JumpExplanation");
    }
  }
}

