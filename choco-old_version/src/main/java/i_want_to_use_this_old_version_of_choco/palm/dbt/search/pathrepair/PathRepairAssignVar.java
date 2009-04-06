package i_want_to_use_this_old_version_of_choco.palm.dbt.search.pathrepair;

import i_want_to_use_this_old_version_of_choco.branch.VarSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.ValIterator;
import i_want_to_use_this_old_version_of_choco.palm.dbt.integer.PalmIntVar;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.DecisionConstraint;
import i_want_to_use_this_old_version_of_choco.palm.dbt.search.PalmAssignVar;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrateur
 * Date: 15 janv. 2004
 * Time: 17:18:37
 * To change this template use Options | File Templates.
 */
public class PathRepairAssignVar extends PalmAssignVar {

  //protected int value = Integer.MIN_VALUE;

  public PathRepairAssignVar(VarSelector varSel, ValIterator valHeuri) {
    super(varSel, valHeuri);
  }

  /**
   * return the next possible decision (variable assignement) on the variable
   *
   * @param item
   * @param previousBranch
   */

  public Object getNextBranch(Object item, Object previousBranch) {
    List list = new LinkedList();
    int value = wrapper.getNextBranch((PalmIntVar) item,
        ((DecisionConstraint) ((List) previousBranch).get(0)).getBranch());
    list.add(((PalmIntVar) item).getDecisionConstraint(value));
    return list;
  }

  /**
   * Checks whether all branches have already been explored at the current choice point
   *
   * @return true if no more branches can be generated
   */
  public boolean finishedBranching(Object item, Object previousBranch) {
    return wrapper.finishedBranching(((PalmIntVar) item),
        ((DecisionConstraint) ((List) previousBranch).get(0)).getBranch());
  }
}
