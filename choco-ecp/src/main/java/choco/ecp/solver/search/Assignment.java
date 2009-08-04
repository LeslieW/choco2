package choco.ecp.solver.search;

import choco.ecp.solver.variables.integer.ExplainedIntVar;
import choco.kernel.solver.variables.Var;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 29 d�c. 2004
 * Time: 16:41:51
 * To change this template use File | Settings | File Templates.
 */
public class Assignment extends AbstractDecision implements Comparable {

  protected ExplainedIntVar var;
  protected int value;

  public Assignment(ExplainedIntVar var, int value) {
    super(var.getProblem());
    this.value = value;
    this.var = var;
  }

  public int getBranch() {
    return value;
  }

  public int getNbVars() {
    return 1;
  }

  public Var getVar(int i) {
    return var;
  }

  public void setVar(int i, Var v) {
    this.var = (ExplainedIntVar) v;
  }

  public boolean isCompletelyInstantiated() {
    return var.isInstantiated();
  }

  public boolean isSatisfied() {
    return var.isInstantiatedTo(value);
  }

  public String pretty() {
    return var + " == " + value;
  }

  public boolean equals(Assignment dec) {
    return (this.value == dec.value) && (this.var == dec.var);
  }

  public int compareTo(Object o) {
    if (var.hashCode() < o.hashCode())
      return -1;
    else if (var.hashCode() == ((Assignment) o).getVar(0).hashCode()) {
      if (value < ((Assignment) o).getBranch())
        return -1;
      else if (value == ((Assignment) o).getBranch())
        return 0;
      else
        return 1;
    } else
      return 1;
  }

  public void delete() { // TODO
  }

  public void constAwake(boolean b) {
  }
}
