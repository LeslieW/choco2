package choco.ecp.solver.explanations.benders;

import choco.cp.solver.search.integer.branching.AssignVar;
import choco.ecp.solver.JumpSolver;
import choco.ecp.solver.explanations.dbt.JumpExplanation;
import choco.ecp.solver.search.Assignment;
import choco.ecp.solver.search.benders.MasterGlobalSearchStrategy;
import choco.ecp.solver.variables.integer.ExplainedIntVar;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.Propagator;
import choco.kernel.solver.search.IntBranchingTrace;


// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

public class BendersExplanation extends JumpExplanation {

  public BendersExplanation(Solver s) {
    super(s);
  }

  public BendersExplanation(int level, Solver s) {
    super(level, s);
  }

  public Propagator getConstraint(int i) {
    return (Propagator) getMasterConstraint(i);
  }

  public SConstraint getMasterConstraint(int i) {
    IntBranchingTrace btrace = (IntBranchingTrace) ((MasterGlobalSearchStrategy) ((JumpSolver) solver).getSearchStrategy()).getMaster().traceStack.get(i - 1);
    if (btrace.getBranching() instanceof AssignVar)
      return new Assignment((ExplainedIntVar) btrace.getBranchingObject(), btrace.getBranchIndex());
    else {
      throw new UnsupportedOperationException("the branching " + btrace.getBranching() + " is not yet supported by the JumpExplanation");
    }
  }

  public SConstraint getCurrentSubConstraint(int i) {
    // The initial propagation is not stored in the stack of subproblems, the index is therefore used directly (i instead i-1 for the master)
    IntBranchingTrace btrace = (IntBranchingTrace) ((MasterGlobalSearchStrategy) ((JumpSolver) solver).getSearchStrategy()).getSubproblems().traceStack.get(i);
    if (btrace.getBranching() instanceof AssignVar)
      return new Assignment((ExplainedIntVar) btrace.getBranchingObject(), btrace.getBranchIndex());
    else {
      throw new UnsupportedOperationException("the branching " + btrace.getBranching() + " is not yet supported by the JumpExplanation");
    }
  }
}

