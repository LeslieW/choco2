package choco.cp.solver.constraints.global.flow;

import choco.kernel.memory.IStateInt;


/**
 * Created by IntelliJ IDEA.
 * User: rochart
 * Date: Dec 2, 2003
 * Time: 10:23:47 AM
 * To change this template use Options | File Templates.
 */
public class PreFlowResEdge implements Cloneable {
  protected PreFlowNode i, j;
  protected IStateInt r_ij, r_ji;
  protected int cost = 0;
  protected float reducedCost;

  public PreFlowResEdge(PreFlowNode i, PreFlowNode j, IStateInt r_ij, IStateInt r_ji) {
    this.i = i;
    this.j = j;
    this.r_ij = r_ij;
    this.r_ji = r_ji;
  }

  public PreFlowResEdge(PreFlowNode i, PreFlowNode j, IStateInt r_ij, IStateInt r_ji, int cost) {
    this(i, j, r_ij, r_ji);
    this.cost = cost;
  }

  public String toString() {
    return (i + "->" + j + "(r_ij:" + r_ij.get() + " ,r_ji:" + r_ji.get() + ")");
  }

  public int getR_ij() {
    return this.r_ij.get();
  }

  public int getR_ji() {
    return this.r_ji.get();
  }

  public void setR_ij(int r_ij) {
    this.r_ij.set(r_ij);
  }

  public void setR_ji(int r_ji) {
    this.r_ji.set(r_ji);
  }

  public PreFlowNode getI() {
    return i;
  }

  public PreFlowNode getJ() {
    return j;
  }

  public int getCost() {
    return cost;
  }

  public void setCost(int cost) {
    this.cost = cost;
  }

  public float getReducedCost() {
    return reducedCost;
  }

  public void setReducedCost(float reducedCost) {
    this.reducedCost = reducedCost;
  }

  public void updateResEdge(int delta) {
    this.r_ij.set(this.r_ij.get() - delta);
    this.r_ji.set(this.r_ji.get() + delta);
  }
}
