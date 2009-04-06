package i_want_to_use_this_old_version_of_choco.goals.solver;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.goals.Goal;
import i_want_to_use_this_old_version_of_choco.goals.choice.Generate;
import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchLimit;
import i_want_to_use_this_old_version_of_choco.search.AbstractGlobalSearchSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: grochart
 * Date: 19 mai 2007
 * Time: 09:10:43
 * To change this template use File | Settings | File Templates.
 */
public class GoalSearchSolver extends AbstractGlobalSearchSolver {


  private class GoalTrace {
    final ChoicePoint choicePoint;
    int choiceIndex;
    final List<Goal> goalStack;

    GoalTrace(ChoicePoint choicePoint, List<Goal> goalStack, int choiceIndex) {
      this.goalStack = goalStack;
      this.choicePoint = choicePoint;
      this.choiceIndex = choiceIndex;
    }
  }

  final protected Goal mainGoal;
  protected List<GoalTrace> goalTraceStack;
  protected List<Goal> currentGoalStack;
  protected ChoicePoint currentChoice;
  protected int currentChoiceIndex;
  protected boolean globalContradiction = false;


  public GoalSearchSolver(AbstractProblem pb, Goal mainGoal) {
    super(pb);
    this.mainGoal = mainGoal;
  }

  public void setGlobalContradiction() {
    globalContradiction = true;
  }

  public void pushGoalTrace() {
    GoalTrace trace = new GoalTrace(currentChoice, currentGoalStack, currentChoiceIndex);
    goalTraceStack.add(trace);

    List<Goal> l2 = new ArrayList<Goal>();
    l2.addAll(currentGoalStack);
    currentChoice = null;
    currentChoiceIndex = -1;
    currentGoalStack = l2;
  }

  public void popGoalTrace() {
    int l = goalTraceStack.size();
    if (l == 0) {
      currentGoalStack = null;
      currentChoice = null;
      currentChoiceIndex = -1;
    } else {
      GoalTrace trace = goalTraceStack.get(l - 1);
      goalTraceStack.remove(l - 1);
      currentGoalStack = trace.goalStack;
      currentChoice = trace.choicePoint;
      currentChoiceIndex = trace.choiceIndex;
    }
  }

  public ChoicePoint lastChoicePoint() {
    int l = goalTraceStack.size();
    if (l == 0) {
      return null;
    } else {
      GoalTrace trace = goalTraceStack.get(l - 1);
      return trace.choicePoint;
    }
  }

  public Goal popGoal() {
    int l = currentGoalStack.size();
    if (l == 0) return null;
    else {
      Goal g = currentGoalStack.get(l - 1);
      currentGoalStack.remove(l - 1);
      return g;
    }
  }

  public void pushGoal(Goal g) {
    currentGoalStack.add(g);
  }

  int lastRealMove = DOWN_BRANCH;

  public Boolean nextSolution() {
    int previousNbSolutions = nbSolutions;
    encounteredLimit = null;
    boolean stop = false;

    // specific initialization for the very first solution search (start from the tree root, instead of last leaf)
    if (nextMove == INIT_SEARCH) {
      nextMove = OPEN_NODE;
      currentGoalStack = new ArrayList<Goal>();
      currentGoalStack.add(mainGoal);
      goalTraceStack = new ArrayList<GoalTrace>();
    } /*else {
      topGoalTrace();
    }   */
    while (!stop) {
      switch (nextMove) {
        case OPEN_NODE: {
          try {
            while (currentChoice == null && !stop) {
              Goal g = popGoal();
              if (g == null) {
                recordSolution();
                // showSolution();
                nextMove = UP_BRANCH;
                stop = true;
              } else {
                if (g instanceof ChoicePoint) {
                  //logger.log(Level.FINE, "point de choix " + g, new Object[]{0, null, " pop ", 0});
                  //newTreeNode();
                  currentChoice = (ChoicePoint) g;
                  currentChoiceIndex = 0;
                  nextMove = DOWN_BRANCH;
                } else {                  
                  if (g instanceof Generate) {
                    Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search.branching").log(Level.FINE, "generate " + g, new Object[]{0, null, " pop ", 0});
                    newTreeNode();
                  }
                 // Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search.branching").log(Level.FINE, "execute " + g.pretty(), new Object[]{0, null, " pop ", 0});
                  //logger.log(Level.FINE, "autre goal " + g, new Object[]{0, null, " pop ", 0});
                  Goal newG = g.execute(this.getProblem());
                  this.getProblem().propagate();                  
                  if (newG != null) pushGoal(newG);
                }
              }
            }
          } catch (ContradictionException e) {
            Logger.getLogger("dev.i_want_to_use_this_old_version_of_choco.search.branching").log(Level.FINE, "contradiction", new Object[]{0, null, " pop ", 0});
            nextMove = UP_BRANCH;
          }
          break;
        }
        case UP_BRANCH: {
          popGoalTrace();
          //lastRealMove = UP_BRANCH;
          if (currentChoice == null) {
            stop = true;
          } else {
            if (globalContradiction) nextMove = UP_BRANCH;
            try {
              problem.worldPop();
              //endTreeNode();
              //problem.propagate();
              endTreeNode();
              postDynamicCut();
              currentChoiceIndex++;
              if (currentChoiceIndex < currentChoice.getNbChoices()) {
                nextMove = DOWN_BRANCH;
              } else {
                nextMove = UP_BRANCH;
              }
            } catch (ContradictionException e) {
              //ctx = popTrace();
              nextMove = UP_BRANCH;
            }
          }
          break;
        }
        case DOWN_BRANCH: {
          //try {          
          //if (lastRealMove == DOWN_BRANCH)
            problem.worldPush();
          //lastRealMove = DOWN_BRANCH;
          Goal g = currentChoice.getChoice(currentChoiceIndex);
          pushGoalTrace();
          pushGoal(g);
          nextMove = OPEN_NODE;
          //} catch (ContradictionException e) {
          //  nextMove = UP_BRANCH;
          //}
          break;
        }
      }
    }
    for (int i = 0; i < limits.size(); i++) {
      AbstractGlobalSearchLimit lim = (AbstractGlobalSearchLimit) limits.get(i);
      lim.reset(false);
    }
    if (nbSolutions > previousNbSolutions) {
      return Boolean.TRUE;
    } else if (isEncounteredLimit()) {
      return null;
    } else {
      return Boolean.FALSE;
    }
  }
}
