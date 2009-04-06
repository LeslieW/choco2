//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Francois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Solution;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.IncreasingDomain;
import i_want_to_use_this_old_version_of_choco.integer.search.MinDomain;
import i_want_to_use_this_old_version_of_choco.palm.PalmProblem;
import i_want_to_use_this_old_version_of_choco.palm.dbt.explain.PalmExplanation;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.*;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.pathrepair.PathRepairAssignVar;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.pathrepair.PathRepairLearn;
import i_want_to_use_this_old_version_of_choco.palm.real.search.PalmRealBranchAndBound;
import i_want_to_use_this_old_version_of_choco.palm.search.NogoodConstraint;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.search.NodeLimit;

import java.util.ArrayList;
import java.util.List;

/**
 * This class extends Solver as a factory for PaLM search solvers.
 */
public class PalmSolver extends Solver {
  /**
   * The extender class the generated solver should use to choose decision constraints during search.
   */
  protected PalmExtend extend = null;

  /**
   * The state class the generated solver should use to maintain the state of the search.
   */
  protected PalmState state = null;

  /**
   * The learning class the generated solver should use to learn forbidden or authorized nodes during search.
   */
  protected PalmLearn learn = null;

  /**
   * The repair class the generated solver should use to choose which decision constraints to remove for repairing.
   */
  protected PalmRepair repair = null;

  /**
   * Lists of the branchings that should be used by the generated solver.
   */
  protected List branchings = new ArrayList();

  /**
   * <i>Decision Repair</i>: number of explanations the decision repair algorithm should store.
   */
  protected int prSize = -1;
  /**
   * <i>Decision Repair</i>: number of moves without improvment the decision repair algorithm should try before
   * stopping.
   */
  protected int prMoves = -1;

  /**
   * Creates a new solver factory for the specified problem
   *
   * @param pb
   */
  public PalmSolver(AbstractProblem pb) {
    super(pb);
  }

  /**
   * The factory method: builds the solver needed to solve the problem (an optimization solver if a variable
   * should be minimized or maximized, a classical solver (mac-dbt) for constraint problems, or a path-repair
   * or decision-repair solver if explanations should be kept.
   *
   * @param pb The problem the solver should solve.
   */
  public void generateSearchSolver(AbstractProblem pb) {
    problem = pb; // TODO : a priori inutile !
    if (null == objective) {                   // MAC-DBT
      solver = new PalmGlobalSearchSolver(pb);
    } else             // MAC-DBT + Dynamic Cuts
        if (objective instanceof IntDomainVar)
          solver = new PalmBranchAndBound(pb, (IntDomainVar) objective, doMaximize);
        else if (objective instanceof RealVar)
          solver = new PalmRealBranchAndBound(pb, (RealVar) objective, doMaximize);
      if (prSize >= 0) {  // Decision repair
      if (repair == null) repair = new PalmUnsureRepair();
      NogoodConstraint ng = new NogoodConstraint(((PalmProblem) this.getProblem()).getVars());
      pb.post(ng);
      if (learn == null) {
        learn = new PathRepairLearn(prSize, ng);
      } else {
        ((PathRepairLearn) learn).setMemory(ng);
      }
    }

    // Classical solver tools
    if (state == null) state = new PalmState((PalmExplanation) (((PalmProblem) this.problem).makeExplanation()));
    if (repair == null) repair = new PalmRepair();
    if (learn == null) learn = new PalmLearn();
    if (extend == null) extend = new PalmExtend();

    // Classical limits
    solver.limits.add(new PalmTimeLimit(solver, timeLimit));
    solver.limits.add(new NodeLimit(solver, nodeLimit));
    // TODO : limits.add(new relaxLimit());

    // Solver should stop at first solution ? TODO : see if useful !
    solver.stopAtFirstSol = firstSolution;

    // Attach solver tools
    ((PalmGlobalSearchSolver) solver).attachPalmState(state);
    ((PalmGlobalSearchSolver) solver).attachPalmExtend(extend);
    ((PalmGlobalSearchSolver) solver).attachPalmLearn(learn);
    ((PalmGlobalSearchSolver) solver).attachPalmRepair(repair);

    // Attach branchings (with a default one if needed
    if (branchings.size() == 0) {
      if (varIntSelector == null) varIntSelector = new MinDomain(pb);
      if (valIntIterator == null && valIntSelector == null) valIntIterator = new IncreasingDomain();
      if (prSize < 0)
        if (valIntIterator != null)
          branchings.add(new PalmAssignVar(varIntSelector, valIntIterator));
        else
          branchings.add(new PalmAssignVar(varIntSelector, valIntSelector));
      else if (valIntIterator != null)
        branchings.add(new PathRepairAssignVar(varIntSelector, valIntIterator));
      else
        System.err.println("Path repair cannot use valSelector");
    }
    ((PalmGlobalSearchSolver) solver).attachPalmBranchings(branchings);
  }

  /**
   * Attaches a list of goals -- or branching in the case of Palm -- that should be attched
   * to the generated solver.
   *
   * @param lst list of branchings (PalmAbstractBranching)
   */
  public void attachGoals(List lst) { // TODO : dans Choco ?
    branchings = lst;
  }

  /**
   * Adds a new branching to the branching list to attach to the generated solver.
   *
   * @param br
   */
  public void addGoal(PalmAbstractBranching br) { // TODO : utiliser meme methode que CHoco ?
    branchings.add(br);
  }

  /**
   * Return the <code>i</code>th solutions.
   *
   * @param i
   * @return The i-th solution of the problem
   */
  public Solution getSolution(int i) {  // TODO : dans Choco !!
    return (Solution) solver.solutions.get(i);
  }

  /**
   * Sets a custom extender to attach to the generated solver.
   */
  public void setPalmExtender(PalmExtend ext) {
    extend = ext;
  }

  /**
   * Sets a custom learner to attach to the generated solver.
   */
  public void setPalmLearner(PalmLearn ext) {
    learn = ext;
  }

  /**
   * Sets a custom state to attach to the generated solver.
   */
  public void setPalmState(PalmState ext) {
    state = ext;
  }

  /**
   * Sets a custom repairer to attach to the generated solver.
   */
  public void setPalmRepairer(PalmRepair ext) {
    repair = ext;
  }

  /**
   * <i>Decision Repair</i> Sets the maximal number of explanations stored and the maximal moves without
   * improvement of the decision repair algorithm.
   *
   * @param size
   * @param moves
   */
  public void setPathRepairValues(int size, int moves) {
    this.prSize = size;
    this.nodeLimit = moves;
  }

  public void setPathRepair() {
    this.prSize = 10;
    this.nodeLimit = 100000;
  }

  public Number getOptimumValue() {
    if (solver instanceof PalmAbstractBranchAndBound) {
      return ((PalmAbstractBranchAndBound) solver).getOptimumValue();
    }
    return null;
  }

  public void launch() {
    solver.incrementalRun();
  }

}
