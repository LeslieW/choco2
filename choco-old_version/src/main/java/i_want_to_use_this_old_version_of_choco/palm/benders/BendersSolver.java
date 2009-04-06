package i_want_to_use_this_old_version_of_choco.palm.benders;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.branch.AbstractIntBranching;
import i_want_to_use_this_old_version_of_choco.branch.VarSelector;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.IncreasingDomain;
import i_want_to_use_this_old_version_of_choco.integer.search.MinDomain;
import i_want_to_use_this_old_version_of_choco.integer.search.ValIterator;
import i_want_to_use_this_old_version_of_choco.integer.search.ValSelector;
import i_want_to_use_this_old_version_of_choco.palm.BendersProblem;
import i_want_to_use_this_old_version_of_choco.palm.benders.search.ApproximateMaster;
import i_want_to_use_this_old_version_of_choco.palm.benders.search.MasterGlobalSearchSolver;
import i_want_to_use_this_old_version_of_choco.palm.benders.search.MasterOptimizer;
import i_want_to_use_this_old_version_of_choco.palm.benders.search.MasterSlaveOptimizer;
import i_want_to_use_this_old_version_of_choco.palm.cbj.search.JumpAssignVar;
import i_want_to_use_this_old_version_of_choco.palm.search.NogoodConstraint;
import i_want_to_use_this_old_version_of_choco.search.NodeLimit;
import i_want_to_use_this_old_version_of_choco.search.TimeLimit;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 28 d�c. 2004
 * Time: 13:39:20
 * To change this template use File | Settings | File Templates.
 */

/**
 * A solver for the benders algorithm
 */
public class BendersSolver extends Solver {

  public static Logger logger = Logger.getLogger("choco");
  /**
   * Branching heuristics for each sub-problem
   */
  protected ArrayList subVarSelector;

  protected ArrayList subValIterator;

  protected ArrayList subValSelector;

  protected IntDomainVar[] objectives;

  /**
   * Objective function
   */
  protected MasterSlavesRelation relation;

  public BendersSolver(AbstractProblem pb) {
    super(pb);
    subVarSelector = new ArrayList();
    subValIterator = new ArrayList();
    subValSelector = new ArrayList();
  }

  public void initHeuristic(ArrayList heuri) {
    while (heuri.size() < ((BendersProblem) problem).getNbSubProblems())
      heuri.add(null);
  }


  /**
   * Sets the variable selector the search solver should use.
   */
  public void setSubVarSelector(int i, VarSelector varSelector) {
    initHeuristic(subVarSelector);
    this.subVarSelector.set(i, varSelector);
  }

  /**
   * Sets the value iterator the search should use
   */
  public void setSubValIterator(int i, ValIterator valIterator) {
    initHeuristic(subValIterator);
    subValIterator.set(i, valIterator);
  }

  /**
   * Sets the value selector the search should use
   */
  public void setSubValSelector(int i, ValSelector valSelector) {
    initHeuristic(subValSelector);
    this.subValSelector.set(i, valSelector);
  }

  public void generateSearchSolver(AbstractProblem pb) {
    problem = pb;
    if (null == objectives && null == objective) {
      if (((BendersProblem) problem).isApproximatedStructure())
        solver = new ApproximateMaster(this.getProblem(), ((BendersProblem) problem).getNbSubProblems());
      else
        solver = new MasterGlobalSearchSolver(this.getProblem(), ((BendersProblem) problem).getNbSubProblems());
    } else {
      if (objectives == null)
        solver = new MasterOptimizer((IntDomainVar) objective, ((BendersProblem) problem).getNbSubProblems(), doMaximize);
      else
        solver = new MasterSlaveOptimizer((IntDomainVar) objective, objectives, doMaximize, relation);
    }
    solver.stopAtFirstSol = firstSolution;
    if (!firstSolution && objectives == null && objective == null)
      throw new UnsupportedOperationException("Searching for all solutions is not yet available within the decomposition");
    solver.limits.add(new TimeLimit(solver, timeLimit));
    solver.limits.add(new NodeLimit(solver, nodeLimit));
    ((MasterGlobalSearchSolver) solver).updateLimit();

    // Add the cut manager for master problem
    NogoodConstraint ng = new NogoodConstraint(((BendersProblem) this.getProblem()).getVars());//getMasterVariables());
    ((MasterGlobalSearchSolver) solver).setCutsConstraint(ng);
    pb.post(ng);

    generateMasterGoal(pb);
    generateSubGoals(pb);
  }


  //  default var and valselector are heuristics of the master problem

  protected void generateMasterGoal(AbstractProblem pb) {
    if (varIntSelector == null)
      varIntSelector = new MinDomain(pb, ((BendersProblem) pb).getMasterVariables());
    if (valIntIterator == null && valIntSelector == null) valIntIterator = new IncreasingDomain();
    if (valIntIterator != null)
      attachMasterGoal(new JumpAssignVar(varIntSelector, valIntIterator));
    else
      attachMasterGoal(new JumpAssignVar(varIntSelector, valIntSelector));
  }

  protected void attachMasterGoal(AbstractIntBranching branching) {
    branching.setSolver(solver);
    ((MasterGlobalSearchSolver) solver).setMainGoal(branching);
  }

  protected void generateSubGoals(AbstractProblem pb) {
    int n = ((BendersProblem) problem).getNbSubProblems();
    initHeuristic(subVarSelector);
    initHeuristic(subValIterator);
    initHeuristic(subValSelector);
    for (int i = 0; i < n; i++) {
      if (subVarSelector.get(i) == null)
        subVarSelector.set(i, new MinDomain(pb, ((BendersProblem) pb).getSubvariables(i)));
      if (subValIterator.get(i) == null && subValSelector.get(i) == null)
        subValIterator.set(i, new IncreasingDomain());
      if (subValIterator.get(i) != null)
        attachSubGoal(i, new JumpAssignVar((VarSelector) subVarSelector.get(i), (ValIterator) subValIterator.get(i)));
      else
        attachSubGoal(i, new JumpAssignVar((VarSelector) subVarSelector.get(i), (ValIterator) subValSelector.get(i)));
    }
  }

  protected void attachSubGoal(int i, AbstractIntBranching branching) {
    branching.setSolver(solver);
    ((MasterGlobalSearchSolver) solver).setSubGoal(i, branching);
  }

  public void setRelation(MasterSlavesRelation relation) {
    this.relation = relation;
  }

  public void setObjectives(IntDomainVar[] objectives) {
    this.objectives = objectives;
  }

  public Number getOptimumValue() {
    return ((MasterGlobalSearchSolver) solver).getOptimumValue();
  }
}
