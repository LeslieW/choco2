package choco.ecp.solver.search.dbt.pathrepair;

import choco.ecp.solver.search.dbt.DecisionSConstraint;
import choco.ecp.solver.search.dbt.PalmAssignVar;
import choco.ecp.solver.variables.integer.dbt.PalmIntVar;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.search.integer.ValIterator;

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
    List<SConstraint> list = new LinkedList<SConstraint>();
    int value = wrapper.getNextBranch(item,
        ((DecisionSConstraint) ((List) previousBranch).get(0)).getBranch());
    list.add(((PalmIntVar) item).getDecisionConstraint(value));
    return list;
  }

  /**
   * Checks whether all branches have already been explored at the current choice point
   *
   * @return true if no more branches can be generated
   */
  public boolean finishedBranching(Object item, Object previousBranch) {
    return wrapper.finishedBranching(item,
        ((DecisionSConstraint) ((List) previousBranch).get(0)).getBranch());
  }
}
