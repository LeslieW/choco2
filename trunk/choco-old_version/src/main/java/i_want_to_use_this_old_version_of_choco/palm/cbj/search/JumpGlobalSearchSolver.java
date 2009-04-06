package i_want_to_use_this_old_version_of_choco.palm.cbj.search;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.branch.AbstractIntBranching;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.palm.ExplainedProblem;
import i_want_to_use_this_old_version_of_choco.palm.Explanation;
import i_want_to_use_this_old_version_of_choco.palm.cbj.explain.JumpExplanation;
import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchLimit;
import i_want_to_use_this_old_version_of_choco.search.IntBranchingTrace;
import i_want_to_use_this_old_version_of_choco.search.Solve;

import java.util.logging.Logger;

// **************************************************
// *                   J-CHOCO                      *
// *   Copyright (C) F. Laburthe, 1999-2003         *
// **************************************************
// *  an open-source Constraint Programming Kernel  *
// *     for Research and Education                 *
// **************************************************

/**
 * A solver based on backjumping algorithm. When a contradiction occurs,
 * the latest responsible choice is found and all choice since his one are
 * removed.
 */
public class JumpGlobalSearchSolver extends Solve {
  
  /**
   * An explanation justifying the current contradiction.
   */
  protected Explanation currentFail;
  
  /**
   * Builds a backjumping solver for the specified problem.
   * @param pb the problem to solve with this solver
   */
  public JumpGlobalSearchSolver(final AbstractProblem pb) {
    super(pb);
  }

  /**
   * called after a node is expanded in the search tree (choice point creation)
   */
  public void endTreeNode() throws ContradictionException {
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.endNode(this);      
    }
  }

  /**
   * Browses the search tree until the next solution or until all the tree
   * has been checked.
   * @return Boolean.TRUE if a solution has been found, Boolean.FALSE if no
   * solution has been found and null if the search has been interrupted by
   * a limit
   */
  public Boolean nextSolution() {
    int previousNbSolutions = nbSolutions;
    encounteredLimit = null;
    IntBranchingTrace ctx = null;
    boolean stop = false;
    
    // specific initialization for the very first solution search
    // (start from the tree root, instead of last leaf)
    if (nextMove == INIT_SEARCH) {
      nextMove = OPEN_NODE;
      ctx = new IntBranchingTrace(mainGoal);
    } else {
      ctx = topTrace();
    }
    while (!stop) {
      switch (nextMove) {
        case OPEN_NODE:
          try {
            newTreeNode();
            Object branchingObj = null;
            AbstractIntBranching currentBranching =
                (AbstractIntBranching) ctx.getBranching();
            AbstractIntBranching nextBranching = currentBranching;
            do {
              currentBranching = nextBranching;
              branchingObj = currentBranching.selectBranchingObject();
              nextBranching = (AbstractIntBranching)
              currentBranching.getNextBranching();
            } while ((branchingObj == null) && (nextBranching != null));
            if (branchingObj != null) {
              ctx = pushTrace();
              ctx.setBranching(currentBranching);
              ctx.setBranchingObject(branchingObj);
              ctx.setBranchIndex(currentBranching
                  .getFirstBranch(ctx.getBranchingObject()));
              nextMove = DOWN_BRANCH;
            } else {
              recordSolution();
              // showSolution();
              currentFail = ((ExplainedProblem) problem).makeExplanation();
              ((JumpExplanation) currentFail).add(1, problem.getWorldIndex());
              nextMove = UP_BRANCH;
              stop = true;
            }
          } catch (ContradictionException e) {
            currentFail = ((ExplainedProblem) problem).makeExplanation();
            nextMove = UP_BRANCH;
          }
          break;
        case UP_BRANCH:
          int contradictionLevel = ((JumpExplanation) currentFail)
          .getLastLevel(problem.getWorldIndex());
          while (problem.getWorldIndex() > (contradictionLevel + 1)) {
            problem.worldPop();
            ctx = popTrace();
          }
          if (this.currentTraceIndex < 0) {
            stop = true;
            ((ExplainedProblem) problem)
            .setContradictionExplanation(currentFail);
          } else {
            try {
              problem.worldPop();
              endTreeNode();
              postDynamicCut();
              ((JumpAssignVar) ctx.getBranching()).goUpBranch(
                  ctx.getBranchingObject(), ctx.getBranchIndex(), currentFail);
              if (!ctx.getBranching().finishedBranching(
                  ctx.getBranchingObject(), ctx.getBranchIndex())) {
                ctx.setBranchIndex(ctx.getBranching().getNextBranch(
                    ctx.getBranchingObject(), ctx.getBranchIndex()));
                nextMove = DOWN_BRANCH;
              } else {
                System.err.println("Le branching est fini et on a pas eu de "
                    + "contradictions : " + ctx.getBranchingObject() + " "
                    + ((IntDomainVar) ctx.getBranchingObject()).getInf()
                    + " -> "
                    + ((IntDomainVar) ctx.getBranchingObject()).getSup() + ": "
                    + ctx.getBranchIndex());
                ctx = popTrace();
                nextMove = UP_BRANCH;
              }
            } catch (JumpContradictionException e) {
              currentFail = e.cause;
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
        case DOWN_BRANCH:
          try {
            //problem.getPropagationEngine().checkCleanState();
            problem.getEnvironment().worldPush();
            ctx.getBranching().goDownBranch(
                ctx.getBranchingObject(), ctx.getBranchIndex());
            problem.propagate();
            nextMove = OPEN_NODE;
          } catch (JumpContradictionException e) {
            currentFail = e.cause;
            nextMove = UP_BRANCH;
          } catch (ContradictionException e) {
            System.err.println("Contradiction exception avec DOWN_BRANCH");
            currentFail = null;
            nextMove = UP_BRANCH;
          }
          break;
        default:
          throw new Error("Illegal state reached during search.");
      }
    }
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(false);
    }
    if (nbSolutions > previousNbSolutions) {
      Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search").fine("Une solution de trouv�e !");
      for (int i = 0; i < problem.getNbIntVars(); i++) {
        Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search").fine(problem.getIntVar(i) + " = "
            + ((IntDomainVar) problem.getIntVar(i)).getVal());
      }
      return Boolean.TRUE;
    } else if (isEncounteredLimit()) {
      return null;
    } else {
      return Boolean.FALSE;
    }
  }
}
