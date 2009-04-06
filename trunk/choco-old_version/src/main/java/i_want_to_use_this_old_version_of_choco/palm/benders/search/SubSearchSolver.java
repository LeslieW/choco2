package i_want_to_use_this_old_version_of_choco.palm.benders.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.AbstractIntBranching;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.BendersProblem;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.cbj.explain.JumpExplanation;
import i_want_to_use_this_old_version_of_choco.palm.cbj.search.JumpAssignVar;
import i_want_to_use_this_old_version_of_choco.palm.cbj.search.JumpContradictionException;
import i_want_to_use_this_old_version_of_choco.palm.cbj.search.JumpGlobalSearchSolver;
import i_want_to_use_this_old_version_of_choco.search.IntBranchingTrace;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 26 d�c. 2004
 * Time: 11:13:01
 * To change this template use File | Settings | File Templates.
 */
public class SubSearchSolver extends JumpGlobalSearchSolver {

  protected boolean slave = true;

  protected IntBranchingTrace currentCtx = null;

  public SubSearchSolver(AbstractProblem pb) {
    super(pb);
    slave = false;
  }

  /**
   * @param pb
   * @param slave is true if the global search solver is dedicated to subproblems
   */
  public SubSearchSolver(AbstractProblem pb, boolean slave) {
    super(pb);
    this.slave = slave;
  }

  /**
   * change the goal of the search solver (called when one want to solve
   * the next sub-problem)
   *
   * @param branching
   */
  protected void changeGoal(AbstractIntBranching branching) {
    branching.setSolver(this);
    if (slave) currentCtx = null;
    traceStack = new ArrayList();
    currentTraceIndex = -1;
    mainGoal = branching;
    nextMove = INIT_SEARCH;
  }

  /**
   * add the new branching to the current goal to perform the search over the both goals
   *
   * @param branching to be added to the main branching of the search solver
   */
  public void fusionGoal(AbstractIntBranching branching) {
    branching.setSolver(this);
    AbstractIntBranching currentBranching = mainGoal;
    AbstractIntBranching nextBranching = mainGoal;
    do {
      currentBranching = nextBranching;
      nextBranching = (AbstractIntBranching) currentBranching.getNextBranching();
    } while ((nextBranching != null));
    currentBranching.setNextBranching(branching);
    nextMove = INIT_SEARCH;
  }

  /**
   * get the next solution of the master problem
   *
   * @return
   */
  public Boolean nextOptimalSolution(int masterWorld) {
    return nextSolution();
  }

  public Boolean nextSolution() {
    int previousNbSolutions = nbSolutions;
    encounteredLimit = null;
    IntBranchingTrace ctx = null;
    if (currentCtx != null)
      ctx = currentCtx;
    boolean stop = false;

    // specific initialization for the very first solution search (start from the tree root, instead of last leaf)
    if (nextMove == INIT_SEARCH) {
      nextMove = OPEN_NODE;
      ctx = new IntBranchingTrace(mainGoal);
    } else if (slave || !stopAtFirstSol) {
      ctx = topTrace();
    }
    while (!stop) {
      switch (nextMove) {
        case OPEN_NODE:
          {
            try {
              newTreeNode();
              Object branchingObj = null;
              AbstractIntBranching currentBranching = (AbstractIntBranching) ctx.getBranching();
              AbstractIntBranching nextBranching = currentBranching;
              do {
                currentBranching = nextBranching;
                branchingObj = currentBranching.selectBranchingObject();
                nextBranching = (AbstractIntBranching) currentBranching.getNextBranching();
              } while ((branchingObj == null) && (nextBranching != null));
              if (branchingObj != null) {
                ctx = pushTrace();
                ctx.setBranching(currentBranching);
                ctx.setBranchingObject(branchingObj);
                ctx.setBranchIndex(currentBranching.getFirstBranch(ctx.getBranchingObject()));
                nextMove = DOWN_BRANCH;
              } else {
                solutionFound(ctx);
                stop = true;
              }
            } catch (ContradictionException e) {
              currentFail = ((BendersProblem) problem).makeExplanation();
              nextMove = UP_BRANCH;
            }
            break;
          }
        case UP_BRANCH:
          {
            int contradictionLevel = ((JumpExplanation) currentFail).getLastLevel(problem.getWorldIndex());
            while (this.currentTraceIndex >= 0 && problem.getWorldIndex() > (contradictionLevel + 1)) {
              problem.worldPop();
              ctx = popTrace();
            }
            if (this.currentTraceIndex < 0) {
              stop = true;
              ((ExplainedProblem) problem).setContradictionExplanation(currentFail);
            } else {
              try {
                problem.worldPop();
                endTreeNode();
                postDynamicCut();
                //MasterGlobalSearchSolver.logger.fine(ctx.getBranchingObject() + " != " + ctx.getBranchIndex() + " " + problem.getEnvironment().getWorldIndex() + " " + currentFail);
                ((JumpAssignVar) ctx.getBranching()).goUpBranch(ctx.getBranchingObject(), ctx.getBranchIndex(), currentFail);
                if (!ctx.getBranching().finishedBranching(ctx.getBranchingObject(), ctx.getBranchIndex())) {
                  ctx.setBranchIndex(ctx.getBranching().getNextBranch(ctx.getBranchingObject(), ctx.getBranchIndex()));
                  nextMove = DOWN_BRANCH;
                } else {
                  System.err.println("Le branching est fini et on a pas eu de contradictions : " + ctx.getBranchingObject() + " " + ((IntDomainVar) ctx.getBranchingObject()).getInf() + " -> " + ((IntDomainVar) ctx.getBranchingObject()).getSup() + ": " + ctx.getBranchIndex());
                  ctx = popTrace();
                  nextMove = UP_BRANCH;
                }
              } catch (JumpContradictionException e) {
                currentFail = e.getExplanation();
                ctx = popTrace();
                nextMove = UP_BRANCH;
              } catch (ContradictionException e) {
                System.err.println("Contradiction exception avec UP_BRANCH");
                e.printStackTrace();
                ctx = popTrace();
                nextMove = UP_BRANCH;
              }
            }
            break;
          }
        case DOWN_BRANCH:
          {
            try {
              //problem.getPropagationEngine().checkCleanState();
              problem.getEnvironment().worldPush();
              //MasterGlobalSearchSolver.logger.fine(""+ ctx.getBranchingObject() + "=" + ctx.getBranchIndex() + " " + problem.getEnvironment().getWorldIndex());
              ctx.getBranching().goDownBranch(ctx.getBranchingObject(), ctx.getBranchIndex());
              problem.propagate();
              nextMove = OPEN_NODE;
            } catch (JumpContradictionException e) {
              currentFail = e.getExplanation();
              nextMove = UP_BRANCH;
            } catch (ContradictionException e) {
              System.err.println("Contradiction exception avec DOWN_BRANCH");
              currentFail = null;
              nextMove = UP_BRANCH;
            }
            break;
          }
      }
    }
    /*for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(false);
    }*/
    if (nbSolutions > previousNbSolutions) {
      return Boolean.TRUE;
    } else if (isEncounteredLimit()) {
      return null;
    } else {
      return Boolean.FALSE;
    }
  }

  public void solutionFound(IntBranchingTrace ctx) {
    currentCtx = ctx;
    nbSolutions += 1;
    nextMove = INIT_SEARCH;
  }

  public void setCurrentFail(Explanation e) {
    currentFail = e;
  }
}
