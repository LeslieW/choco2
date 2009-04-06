package i_want_to_use_this_old_version_of_choco.palm.benders.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.AbstractIntBranching;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.BendersProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.benders.MasterSlavesRelation;
import i_want_to_use_this_old_version_of_choco.palm.cbj.explain.JumpExplanation;
import i_want_to_use_this_old_version_of_choco.palm.cbj.search.JumpGlobalSearchSolver;
import i_want_to_use_this_old_version_of_choco.palm.search.Assignment;
import i_want_to_use_this_old_version_of_choco.palm.search.NogoodConstraint;
import i_want_to_use_this_old_version_of_choco.palm.search.SymbolicDecision;
import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchLimit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 26 d�c. 2004
 * Time: 11:12:39
 * To change this template use File | Settings | File Templates.
 */

/**
 * Default implementation of Benders search (used for P_{0})
 * basis for {P'_{0},P_{y}, and P_{xy}
 */
public class MasterGlobalSearchSolver extends JumpGlobalSearchSolver {

  protected static Logger logger = Logger.getLogger("choco");

  /**
   * The nogood constraint gathering all benders cuts
   */
  protected NogoodConstraint cuts;

  /**
   * A search solver corresponding to the master
   */
  protected SubSearchSolver master;

  /**
   * A search solver used for the subproblems
   */
  protected SubSearchSolver subproblems;

  /**
   * the goal corresponding to the variables of each subproblems
   */
  protected AbstractIntBranching[] subgoals;

  /**
   * store bendersCuts extracted on each subproblem
   */
  protected Explanation[] bendersCut;

  /**
   * number of cuts extracted at the current iteration
   */
  protected int nbCutLearned = 0;

  /**
   * number of feasible sub-problems found at the current iteration
   */
  protected int nbFeasibleProblems = 0;

  /**
   * Store the solutions found on sub-problems. As only one global search solver is used,
   * solutions of sub-problems need to be stored
   */
  protected ArrayList partialSol; // TODO : Should be a Solution object

  /**
   * feasability of the whole problem
   */
  protected boolean feasible = true;

  /**
   * Objective function formulated as a specific relation
   */
  protected MasterSlavesRelation decomposition;

  public MasterGlobalSearchSolver(AbstractProblem pb, int nbsub, MasterSlavesRelation relation) {
    super(pb);
    partialSol = new ArrayList();//int[pb.getNbIntVars()];
    subgoals = new AbstractIntBranching[nbsub];
    bendersCut = new Explanation[nbsub];
    for (int i = 0; i < nbsub; i++) {
      partialSol.add(new int[((BendersProblem) pb).getSubvariablesList(i).size()]);
    }
    decomposition = relation;
  }

  public MasterGlobalSearchSolver(AbstractProblem pb, int nbsub) {
    super(pb);
    master = new SubSearchSolver(pb, false);
    subproblems = new SubSearchSolver(pb, true);
    subgoals = new AbstractIntBranching[nbsub];
    bendersCut = new Explanation[nbsub];
    partialSol = new ArrayList();//int[pb.getNbIntVars()];
    for (int i = 0; i < nbsub; i++) {
      partialSol.add(new int[((BendersProblem) pb).getSubvariablesList(i).size()]);
    }
    decomposition = new MasterSlavesRelation();
  }

  /**
   * set the limit objects to both master and slaves.
   * They share the same limits.
   */
  public void updateLimit() {
    for (int i = 0; i < limits.size(); i++) {
      master.limits.add(limits.get(i));
      subproblems.limits.add(limits.get(i));
    }
  }

  /**
   * getter on the number of cuts stored (without inclusion)
   *
   * @return the number of cuts learned
   */
  public int getNbCuts() {
    return cuts.getPermanentMemorySize();
  }

  /**
   * @return the global search solver associated to the subproblems
   */
  public SubSearchSolver getSubproblems() {
    return subproblems;
  }

  /**
   * @return the global search solver associated to the master
   */
  public SubSearchSolver getMaster() {
    return master;
  }

  /**
   * set the branching of the master solver
   */
  public void setMainGoal(AbstractIntBranching branch) {
    master.mainGoal = branch;
  }

  /**
   * set the branching of subproblem number i
   */
  public void setSubGoal(int i, AbstractIntBranching branch) {
    subgoals[i] = branch;
  }

  /**
   * set the way BendersCut are managed
   */
  public void setCutsConstraint(NogoodConstraint cuts) {
    this.cuts = cuts;
  }

  /**
   * return -1 as it is a satisfaction problem
   */
  public int getOptimumValue() {
    return -1;
  }

  protected Explanation fail;

  protected int masterWorld;

  protected boolean stop = false;

  public void incrementalRun() {
    baseWorld = problem.getEnvironment().getWorldIndex();
    boolean feasibleRootState = true;
    try {
      newTreeSearch();
      problem.propagate();
    } catch (ContradictionException e) {
      feasibleRootState = false;
    }
    if (feasibleRootState) {
      problem.worldPush();
      while (!stop && master.nextOptimalSolution(masterWorld) == Boolean.TRUE) {
        masterWorld = problem.getWorldIndex();
        solveSubProblems(); // solve the subproblems
        stop = (nbCutLearned == 0);
        manageCuts();                  // add the benders cuts to the master problem
        if (stop && feasible) {        // store and stop if a solution has been found
          solutionFound();
        } else if (!stop && feasible) { // if cuts have been identified, come back to the master in a consistent state
          nextMasterMove();
        } else if (!feasible) {
          stop = true;
        }
      }
      for (int i = 0; i < limits.size(); i++) {
        AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
        lim.reset(false);
      }
      if ((maxNbSolutionStored > 0) && (!stopAtFirstSol) && existsSolution()) {
        problem.worldPopUntil(baseWorld);
        restoreBestSolution();
      } else if (!existsSolution()) {
        problem.feasible = Boolean.FALSE;
      }
    } else {
      problem.feasible = Boolean.FALSE;
    }
    endTreeSearch();
  }

  public Boolean nextSolution() {
    throw new Error("api not yet available on benders solver (MasterGlobalSearchSolver)");
  }

  /**
   * Main iteration over the subproblems
   */
  public void solveSubProblems() {
    BendersProblem pb = (BendersProblem) problem;
    for (int i = 0; i < pb.getNbSubProblems(); i++) { // solve the subproblems
      if (logger.isLoggable(Level.FINE))
        logger.fine("START SUBPB " + i);
      subproblems.changeGoal(subgoals[i]);
      Boolean sol = subproblems.nextOptimalSolution(masterWorld);
      if (sol == Boolean.FALSE) {
        fail = pb.getContradictionExplanation();
        if (((JumpExplanation) fail).nogoodSize() == 0) feasible = false;
        storeCuts(fail, i);
      } else if (sol == Boolean.TRUE && nbCutLearned == 0) {
        storePartialSolution(i);
      } else if (sol == null) {
        feasible = false;
      }
      problem.worldPopUntil(masterWorld);
      if (masterWorld == pb.getEnvironment().getWorldIndex())
        pb.getPropagationEngine().flushEvents();
      if (!feasible) break;
    }
  }

  /**
   * Describes the way the master search solver has to be set
   * to look for the next solution.
   */
  public void nextMasterMove() {
    master.setCurrentFail((Explanation) fail.copy()); // choose one explanation among all cuts
    master.nextMove = UP_BRANCH; // restart the master from the current master solution
    fail = null;
    if (logger.isLoggable(Level.FINE))
      logger.fine("START MASTERPB ");
  }

  public void storeCuts(Explanation expl, int i) {
    bendersCut[i] = expl;
    nbCutLearned++;
  }

  /**
   * compute the global cut using the MasterSlavesRelation
   * and add the cut the nogood constraint managing BendersCut
   */
  public void manageCuts() {
    if (feasible) {
      ArrayList currentCuts;
      currentCuts = decomposition.computeExpl(bendersCut);
      addCuts(currentCuts);
    }
    nbCutLearned = 0;
    for (int i = 0; i < bendersCut.length; i++) {
      bendersCut[i] = null;
    }
  }

  public void addCuts(ArrayList currentCuts) {
    logCuts(currentCuts);
    for (int i = 0; i < currentCuts.size(); i++) {
      SymbolicDecision[] exp = (SymbolicDecision[]) ((Explanation) currentCuts.get(i)).getNogood();
      cuts.addPermanentNogood(exp);
    }
  }

  public void solutionFound() {
    logSolution();
    restorePartialSolutions();
    recordSolution();
    /*if (!stopAtFirstSol) {
      System.out.println("Asking for all solutions is not yet implemented");
    }*/
    cleanPartialSolutions();
  }

  // ----------------------------------------------------
  // ---------------- Partial Solution managment -------
  // ----------------------------------------------------

  public void cleanPartialSolutions() {
    for (Iterator iterator = partialSol.iterator(); iterator.hasNext();) {
      int[] vs = (int[]) iterator.next();
      for (int i = 0; i < vs.length; i++) {
        vs[i] = -1;
      }
    }
  }

  public void storePartialSolution(int subpb) {
    int[] vs = (int[]) partialSol.get(subpb);
    for (int i = 0; i < vs.length; i++) {
      vs[i] = ((IntDomainVar) ((BendersProblem) problem).getSubvariablesList(subpb).get(i)).getVal();
    }
  }

  public void restorePartialSolutions() {
    try {
      int subPb = 0;
      for (Iterator iterator = partialSol.iterator(); iterator.hasNext();) {
        int[] vs = (int[]) iterator.next();
        for (int i = 0; i < vs.length; i++) {
          ((IntDomainVar) ((BendersProblem) problem).getSubvariablesList(subPb).get(i)).setVal(vs[i]);
        }
        subPb++;
      }
      problem.propagate();
    } catch (ContradictionException e) {
      throw new Error("Bug in restoring partial solutions in benders master solver");
    }
  }

  // ----------------------------------------------------
  // ------------------- logs ---------------------------
  // ----------------------------------------------------

  public void logSolution() {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("Une solution de trouv�e !");
      for (int i = 0; i < problem.getNbIntVars(); i++) {
        logger.fine(problem.getIntVar(i) + " = " + ((IntDomainVar) problem.getIntVar(i)).getVal());
      }
    }
  }

  public void logCuts(ArrayList li) {
    if (logger.isLoggable(Level.FINE)) {
      String mes = "Cuts added : {";
      Iterator it = li.iterator();
      while (it.hasNext()) {
        SymbolicDecision[] cut = (SymbolicDecision[]) ((Explanation) it.next()).getNogood();
        for (int i = 0; i < cut.length; i++) {
          Assignment d = (Assignment) cut[i];
          mes += d.getVar(0) + "==" + d.getBranch();
          if (i < (cut.length - 1)) mes += ",";
        }
        if (it.hasNext()) mes += " | ";
      }
      mes += "}";
      //System.out.println(mes);
      logger.fine(mes);
    }
  }
}
