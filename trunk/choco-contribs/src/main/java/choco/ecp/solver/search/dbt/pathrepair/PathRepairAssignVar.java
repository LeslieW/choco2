package choco.ecp.solver.search.dbt.pathrepair;



/**
 * Created by IntelliJ IDEA.
 * User: Administrateur
 * Date: 15 janv. 2004
 * Time: 17:18:37
 * To change this template use Options | File Templates.
 */
//public class PathRepairAssignVar extends PalmAssignVar {
//
//  //protected int value = Integer.MIN_VALUE;
//
//  public PathRepairAssignVar(VarSelector varSel, ValIterator valHeuri) {
//    super(varSel, valHeuri);
//  }
//
//  /**
//   * return the next possible decision (variable assignement) on the variable
//   *
//   * @param item
//   * @param previousBranch
//   */
//
//  public Object getNextBranch(Object item, Object previousBranch) {
//    List list = new LinkedList();
//    int value = wrapper.getNextBranch((PalmIntVar) item,
//        ((DecisionConstraint) ((List) previousBranch).get(0)).getBranch());
//    list.add(((PalmIntVar) item).getDecisionConstraint(value));
//    return list;
//  }
//
//  /**
//   * Checks whether all branches have already been explored at the current choice point
//   *
//   * @return true if no more branches can be generated
//   */
//  public boolean finishedBranching(Object item, Object previousBranch) {
//    return wrapper.finishedBranching(((PalmIntVar) item),
//        ((DecisionConstraint) ((List) previousBranch).get(0)).getBranch());
//  }
//}
