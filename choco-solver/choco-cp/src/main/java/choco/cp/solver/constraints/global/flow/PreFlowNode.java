package choco.cp.solver.constraints.global.flow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rochart
 * Date: Dec 2, 2003
 * Time: 10:25:42 AM
 * To change this template use Options | File Templates.
 */
public class PreFlowNode {
  protected int label;
  private List<PreFlowResEdge> nextEdge;
  private List<PreFlowResEdge> prevEdge;
  protected int excess;
  protected int distLabel = Integer.MAX_VALUE;
  protected float potential;

  public PreFlowNode(int label) {
    this.label = label;
    this.nextEdge = new ArrayList<PreFlowResEdge>();
    this.prevEdge = new ArrayList<PreFlowResEdge>();
  }


  public String toString() {
    return (label + "(d:" + distLabel + ",e:" + excess + ")");
  }

  public int getLabel() {
    return this.label;
  }

    /**
     * Return the label of the node (unique identifier) in order to make the flow deterministic
     */
    public int hashCode() {
        return this.label;
    }

    public boolean equals(Object obj) {
        return hashCode()==obj.hashCode();
    }


  public void addPrevEdge(PreFlowResEdge edge) {
    this.prevEdge.add(edge);
  }

  public void addNextEdge(PreFlowResEdge edge) {
    this.nextEdge.add(edge);
  }

  public List getPrevEdge() {
    return this.prevEdge;
  }

  public List getNextEdge() {
    return this.nextEdge;
  }

  public int getDistLabel() {
    return this.distLabel;
  }

  public void setDistLabel(int dist) {
    this.distLabel = dist;
  }

  public int getExcess() {
    return this.excess;
  }

  public void setExcess(int excess) {
    this.excess = excess;
  }

  public float getPotential() {
    return potential;
  }

  public void setPotential(float potential) {
    this.potential = potential;
  }

  public void reInitDistLabel() {
    this.distLabel = Integer.MAX_VALUE - 1;
  }

  public int initDistLabel() {
    return this.initDistLabel(false);
  }

  public int initDistLabel(boolean reverse) {
    List<PreFlowNode> currentNodes = new ArrayList<PreFlowNode>();
    currentNodes.add(this);
    List oldNodes; // = new ArrayList(); Pas la peine de l'initialiser...
    int currentDist = 0;
    int nbReachableNodes = 1;

    this.distLabel = currentDist;

    while (currentNodes.size() != 0) {
      oldNodes = currentNodes;
      currentNodes.clear();
      currentDist++;

      for (int i = 0; i < oldNodes.size(); i++) {
        PreFlowNode node = (PreFlowNode) oldNodes.get(i);
        List edgeList = node.getPrevEdge();

        for (int j = 0; j < edgeList.size(); j++) {
          PreFlowResEdge edge = (PreFlowResEdge) edgeList.get(j);
          if (edge.getR_ij() > 0) {
            if (edge.getI().getDistLabel() > currentDist) {
              edge.getI().setDistLabel(currentDist);
              currentNodes.add(edge.getI());
              nbReachableNodes++;
            }
          }
        }

        edgeList = node.getNextEdge();

        for (int j = 0; j < edgeList.size(); j++) {
          PreFlowResEdge edge = (PreFlowResEdge) edgeList.get(j);
          if (edge.getR_ji() > 0) {
            if (edge.getJ().getDistLabel() > currentDist) {
              edge.getJ().setDistLabel(currentDist);
              currentNodes.add(edge.getJ());
              nbReachableNodes++;
            }
          }
        }
      }
    }
    return nbReachableNodes;
  }

  public void updatePotential(float delta) {
    this.potential += delta;

    for (int i = 0; i < nextEdge.size(); i++) {
      FlowResEdge edge = (FlowResEdge) nextEdge.get(i);
      if ((edge.getLu_ij() == null && edge.getU_ij() != 0) || (edge.getLu_ij() != null && edge.getLu_ij().getSup() != 0)) {
        edge.setReducedCost(edge.getReducedCost() - delta);
      }
    }
    for (int i = 0; i < prevEdge.size(); i++) {
      FlowResEdge edge = (FlowResEdge) prevEdge.get(i);

      if ((edge.getLu_ij() == null && edge.getU_ij() != 0) || (edge.getLu_ij() != null && edge.getLu_ij().getSup() != 0)) {
        edge.setReducedCost(edge.getReducedCost() + delta);
      }
    }
  }
}
