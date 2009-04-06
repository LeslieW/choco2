package i_want_to_use_this_old_version_of_choco.palm;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.benders.BendersSolver;
import i_want_to_use_this_old_version_of_choco.palm.benders.MasterSlavesRelation;
import i_want_to_use_this_old_version_of_choco.palm.benders.explain.BendersExplanation;
import i_want_to_use_this_old_version_of_choco.palm.benders.search.MasterGlobalSearchSolver;
import i_want_to_use_this_old_version_of_choco.prop.ChocEngine;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 26 d�c. 2004
 * Time: 10:51:28
 * To change this template use File | Settings | File Templates.
 */

/**
 * Choco problem extension involving explanations and a Benders
 * decomposition algorithm based on the use of explanations.
 * The decomposition is made among the variables and the choco model need only
 * to be enrich by indicating for each variable the problem to which it belongs.
 * Warning : subproblems must be independent once the master is instantiated.
 * If it is not the case, it has to be precised with the use of setApproximatedStructure().
 */
public class BendersProblem extends JumpProblem {

  /**
   * The number of sub problems considered at each iteration
   */
  protected int nbSubProblems = 1;

  /**
   * List of the Master variables.
   */
  protected ArrayList masterVariables;

  /**
   * for each subproblems, a table of variables corresponding to the
   * subproblem n�i is stored in subvariables.get(i)
   */
  protected ArrayList subvariables;

  /**
   * Boolean indicating whether the subproblems are completely independant once
   * master variables instantiated.
   */
  protected boolean approximatedStructure;

  /**
   * build a problem that will use the decomposition algorithm
   */
  public BendersProblem() {
    super();
    masterVariables = new ArrayList();
    subvariables = new ArrayList();
    subvariables.add(new ArrayList());
    // Specialized engine and solver for Palm
    this.propagationEngine = new ChocEngine(this);
    this.solver = new BendersSolver(this);
  }

  /**
   * Add a variable to the master set.
   *
   * @param v the variable to be added to the master
   */
  public void addMasterVariables(IntDomainVar v) {
    masterVariables.add(v);
  }

  /**
   * Add a variable to a given sub-problem.
   *
   * @param i the number of considered sub-problem
   * @param v the variable to be added
   */
  public void addSubVariables(int i, IntDomainVar v) {
    while (subvariables.size() <= i) {
      subvariables.add(new ArrayList());
      nbSubProblems += 1;
    }
    ((ArrayList) subvariables.get(i)).add(v);
  }

  /**
   * factory to build explanation within the Benders Framework
   *
   * @return
   */
  public Explanation makeExplanation() {
    return new BendersExplanation(this);
  }

  /**
   * factory to build an explanation at a given level within the Benders Framework
   *
   * @return
   */
  public Explanation makeExplanation(int level) {
    return new BendersExplanation(level, this);
  }

  public boolean isApproximatedStructure() {
    return approximatedStructure;
  }

  /**
   * precise that the structures used as subproblems is not ideal
   * but has week relationships
   * WARNING : it can only be used in case of satisfaction problem.
   */
  public void setApproximatedStructure() {
    this.approximatedStructure = true;
  }

  /**
   * @return the number of subproblems considered by the algorithm
   */
  public int getNbSubProblems() {
    return nbSubProblems;
  }

  /**
   * @return the number of cuts learned during the search
   */
  public int getNbCutsLearned() {
    return ((MasterGlobalSearchSolver) solver.getSearchSolver()).getNbCuts();
  }

  /**
   * @return the array of master variables
   */
  public IntDomainVar[] getMasterVariables() {
    IntDomainVar[] mvs = new IntDomainVar[masterVariables.size()];
    masterVariables.toArray(mvs);
    return mvs;
  }

  /**
   * @return the list of master variables
   */
  public ArrayList getMasterVariablesList() {
    return masterVariables;
  }

  /**
   * @return the list of variable of the subproblem number i
   */
  public ArrayList getSubvariablesList(int i) {
    return ((ArrayList) subvariables.get(i));
  }

  /**
   * @return the array of variable of the subproblem number i
   */
  public IntDomainVar[] getSubvariables(int i) {
    IntDomainVar[] subvs = new IntDomainVar[((ArrayList) subvariables.get(i)).size()];
    ((ArrayList) subvariables.get(i)).toArray(subvs);
    return subvs;
  }

  /**
   * minimize an objective function over both the master and sub-problems.
   *
   * @param mobj     objective variable of the master
   * @param objs     objectives variables of each subproblem
   * @param relation a relation representing the objective function
   */
  public void minimize(IntDomainVar mobj, IntDomainVar[] objs, MasterSlavesRelation relation) {
    optimize(false, mobj, objs, relation);
  }

  /**
   * minimize an objective function only including variables of the master.
   *
   * @param obj objective variable of the master
   */
  public void minimize(IntDomainVar obj) {
    optimize(false, obj, null);
  }

  /**
   * maximize an objective function over both the master and sub-problems.
   *
   * @param mobj     objective variable of the master
   * @param objs     objectives variables of each subproblem
   * @param relation a relation representing the objective function
   */
  public void maximize(IntDomainVar mobj, IntDomainVar[] objs, MasterSlavesRelation relation) {
    optimize(true, mobj, objs, relation);
  }

  /**
   * minimize an objective function only including variables of the master.
   *
   * @param obj objective variable of the master
   */
  public void maximize(IntDomainVar obj) {
    optimize(true, obj, null);
  }

  protected Boolean optimize(boolean maximize, IntDomainVar obj, MasterSlavesRelation relation) {
    return optimize(maximize, obj, null, relation);
  }

  protected Boolean optimize(boolean maximize, IntDomainVar mobj, IntDomainVar[] objs, MasterSlavesRelation relation) {
    solver.setDoMaximize(maximize);
    ((BendersSolver) solver).setObjectives(objs);
    ((BendersSolver) solver).setObjective(mobj);
    ((BendersSolver) solver).setRelation(relation);
    solver.setFirstSolution(false);
    solver.generateSearchSolver(this);
    solver.launch();
    return this.isFeasible();
  }
}
