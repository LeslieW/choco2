package choco.cp.solver.constraints.global.flow;

// import ice.Ice;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: rochart
 * Date: Dec 2, 2003
 * Time: 10:34:33 AM
 * To change this template use Options | File Templates.
 */
public class PreFlowNodesSet {
private Set<PreFlowNode> flowNodesSet = new HashSet<PreFlowNode>();
private int maxFlowDistLabel = 0;
protected PreFlowNode[] nodes = null;

// bidouille Maurice pour se debarrasser du package "ice" qui ne servait que
// pour les logging (et seulement au niveau "info")
// Le "_"  pourrait etre remplacer par un "." si on voullait créer un
// attribut log.
protected static Logger logger = Logger.getLogger("choco.prop.const");
public void log_info(String msg) {
        if (logger.isLoggable(Level.INFO)) {
                logger.info(msg);
        }
}

public void removeFlowNode(PreFlowNode node) {
        this.flowNodesSet.remove(node);
}

public void initialMaxFlow(PreFlowNode sourceN, PreFlowNode sinkN)
        throws NullFlowException {
        this.initialMaxFlow(sourceN, sinkN, true);
}

public void initialMaxFlow(PreFlowNode sourceN, PreFlowNode sinkN,
                           boolean needToSature) throws NullFlowException {
        int activeNodes = sinkN.initDistLabel();

        if (sourceN.getDistLabel() == Integer.MAX_VALUE) {
                log_info("Source distLabel is unknown : no path from source to sink, flow is null. " +
                        activeNodes + " active node(s).");
                // // if (Ice.isLoggable(Level.INFO)) Logger.getLogger("Ice").info("Source distLabel is unknown : no path from source to sink, flow is null. " + activeNodes + " active node(s).");
        }
        else {
                if (needToSature) {
                        List edges = sourceN.getNextEdge();
                        for (int i = 0; i < edges.size(); i++) {
                                PreFlowResEdge edge = (PreFlowResEdge) edges.get(i);
                                this.addFlow(edge, edge.getR_ij());
                        }
                        this.updateDistLabel(sourceN);
                        // if (Ice.isLoggable(Level.INFO)) Logger.getLogger("Ice").info("Source " + sourceN + " has been saturated");
                        log_info("Source " + sourceN + " has been saturated");
                        if (sourceN.getDistLabel() != Integer.MAX_VALUE) {
                                System.out.println("Arcs : " + edges);
                                System.out.println("Arcs : " + sourceN.getPrevEdge());
                                throw(new NullFlowException());
                        }
                }

                sourceN.setDistLabel(activeNodes);

                this.preflowPushMax(sourceN, sinkN);
        }
}

private void preflowPushMax(PreFlowNode sourceN, PreFlowNode sinkN) {
        boolean isSaturated = false;

        while(! isSaturated) {
                //System.out.println("Nodes : " + this.flowNodesSet);
                PreFlowNode node = highestDistLabelNode(sourceN, sinkN);
                //System.out.println("Highest : " + node);
                if (node != null) {
                        List edgeList = node.getNextEdge();

                        boolean found = false;
                        for (int i = 0; i < edgeList.size(); i++) {
                                PreFlowResEdge edge = (PreFlowResEdge) edgeList.get(i);
                                if ((edge.getR_ij() > 0) && (edge.getJ().getDistLabel() == node.getDistLabel() - 1)) {
                                        found = true;
                                        //System.out.println("Found: " + edge.getI() + "->" + edge.getJ());
                                        addFlow(edge, Math.min(edge.getR_ij(), node.getExcess()));
                                        break;
                                }
                        }
                        if (!found) {
                                edgeList = node.getPrevEdge();

                                for (int i = 0; i < edgeList.size(); i++) {
                                        PreFlowResEdge edge = (PreFlowResEdge) edgeList.get(i);
                                        if ((edge.getR_ji() > 0) && (edge.getI().getDistLabel() == node.getDistLabel() - 1)) {
                                                found = true;
                                                //System.out.println("Found: " + edge.getJ() + "->" + edge.getI());
                                                addFlow(edge, Math.max(-edge.getR_ji(), -node.getExcess()));
                                                break;
                                        }
                                }

                                if (!found){
                                        updateDistLabel(node);
                                }
                        }
                } else isSaturated = true;
        }
        // if (Ice.isLoggable(Level.INFO)) Logger.getLogger("Ice").info("Max Flow is " + sinkN.getExcess());
        log_info("Max Flow is " + sinkN.getExcess());
}

public void initialMinFlow(PreFlowNode sourceN, PreFlowNode sinkN) {
        this.initialMinFlow(sourceN, sinkN, true);
}

public void initialMinFlow(PreFlowNode sourceN, PreFlowNode sinkN, boolean needToSature) {
        int activeNodes = sourceN.initDistLabel();

        if (sinkN.getDistLabel() == Integer.MAX_VALUE) {
                // if (Ice.isLoggable(Level.INFO)) Logger.getLogger("Ice").info("Sink distLabel is unknown : no path from sink to source, flow is null");
                log_info("Sink distLabel is unknown : no path from sink to source, flow is null");
        }
        else {
                if (needToSature) {
                        List edges = sinkN.getPrevEdge();
                        for (int i = 0; i < edges.size(); i++) {
                                PreFlowResEdge edge = (PreFlowResEdge) edges.get(i);
                                this.addFlow(edge, -edge.getR_ji());
                        }
                        this.updateDistLabel(sinkN);
                        // if (Ice.isLoggable(Level.INFO)) Logger.getLogger("Ice").info("Sink " + sinkN + " has been saturated");
                        log_info("Sink " + sinkN + " has been saturated");

                        /*if (sourceN.getDistLabel() != Integer.MAX_VALUE) {
                            throw(new NullFlowException());
                        }*/
                }

                sinkN.setDistLabel(activeNodes);
                this.preflowPushMin(sourceN, sinkN);
        }
}

private void preflowPushMin(PreFlowNode sourceN, PreFlowNode sinkN) {
        boolean isSaturated = false;

        while(! isSaturated) {
                PreFlowNode node = highestDistLabelNode(sourceN, sinkN);
                if (node != null) {
                        List edgeList = node.getPrevEdge();

                        boolean found = false;
                        for (int i = 0; i < edgeList.size(); i++) {
                                PreFlowResEdge edge = (PreFlowResEdge) edgeList.get(i);
                                if ((edge.getR_ji() > 0) && (edge.getI().getDistLabel() == node.getDistLabel() - 1)) {
                                        found = true;
                                        addFlow(edge, -Math.min(edge.getR_ji(), node.getExcess()));
                                        break;
                                }
                        }
                        if (!found) {
                                edgeList = node.getNextEdge();

                                for (int i = 0; i < edgeList.size(); i++) {
                                        PreFlowResEdge edge = (PreFlowResEdge) edgeList.get(i);
                                        if ((edge.getR_ij() > 0) && (edge.getJ().getDistLabel() == node.getDistLabel() - 1)) {
                                                found = true;
                                                addFlow(edge, Math.min(edge.getR_ij(), node.getExcess()));
                                                break;
                                        }
                                }

                                if (!found){
                                        updateDistLabel(node);
                                }
                        }
                } else isSaturated = true;
        }
        // if (Ice.isLoggable(Level.INFO)) Logger.getLogger("Ice").info("Min Flow is " + sourceN.getExcess());
        log_info("Min Flow is " + sourceN.getExcess());
}

private PreFlowNode highestDistLabelNode(PreFlowNode sourceN, PreFlowNode sinkN) {
        // !!!!!!!! RANDOM !!!!!!!
        int maxDist = maxFlowDistLabel;
        int cmax = 0;
        PreFlowNode returnNode = null;

        if (flowNodesSet.size() == 0) {
                return null;
        } else {
                /*Object[] nodes = flowNodesSet.toArray();
            int start = (int) (Math.random() * nodes.length);

            int i = start;
            do {
              PreFlowNode node = (PreFlowNode) nodes[i];
              if ((node != sourceN) && (node != sinkN)) {
                    if (node.getDistLabel() == maxDist) {
                        return node;
                    }
                    else if (node.getDistLabel() > cmax) {
                        cmax = node.getDistLabel();
                        returnNode = node;
                    }
              }
              i = (i == nodes.length - 1) ? 0 : i+1;
            } while (i != start);*/
                // Voir si c'est utile d'utiliser un random comme dans version Claire
                for (Iterator iterator = flowNodesSet.iterator(); iterator.hasNext();) {
                        PreFlowNode node = (PreFlowNode) iterator.next();
                        if ((node != sourceN) && (node != sinkN)) {
                                if (node.getDistLabel() == maxDist) {
                                        return node;
                                }
                                else if (node.getDistLabel() > cmax) {
                                        cmax = node.getDistLabel();
                                        returnNode = node;
                                }
                        }
                }
        }

        //Mise a jour...
        maxFlowDistLabel = cmax;

        return returnNode;
}

private void updateDistLabel(PreFlowNode node) {
        int minDist = Integer.MAX_VALUE;
        List edgeList = node.getNextEdge();

        for (int i = 0; i < edgeList.size(); i++) {
                PreFlowResEdge edge = (PreFlowResEdge) edgeList.get(i);
                if ((edge.getR_ij() > 0) && (edge.getJ().getDistLabel() + 1 < minDist)) {
                        minDist = edge.getJ().getDistLabel() + 1;
                }
        }

        edgeList = node.getPrevEdge();

        for (int i = 0; i < edgeList.size(); i++) {
                PreFlowResEdge edge = (PreFlowResEdge) edgeList.get(i);
                if ((edge.getR_ji() > 0) && (edge.getI().getDistLabel() + 1 < minDist)) {
                        minDist = edge.getI().getDistLabel() + 1;
                }
        }

        node.setDistLabel(minDist);
        if (node.getExcess() > 0) {
                int dist = node.getDistLabel();
                if ((dist < Integer.MAX_VALUE) && (dist > this.maxFlowDistLabel))
                        this.maxFlowDistLabel = dist;
        }

}

private void addFlow(PreFlowResEdge edge, int delta) {
        edge.updateResEdge(delta);
        this.updateExcess(edge, delta);
}

private void updateExcess(PreFlowResEdge edge, int delta) {
        edge.getI().setExcess(edge.getI().getExcess() - delta);
        edge.getJ().setExcess(edge.getJ().getExcess() + delta);

        if (edge.getI().getExcess() == 0) {
                this.flowNodesSet.remove(edge.getI());
        }
        else if (edge.getI().getExcess() > 0) {
                this.flowNodesSet.add(edge.getI());
                int dist = edge.getI().getDistLabel();
                if ((dist < Integer.MAX_VALUE) && (dist > this.maxFlowDistLabel))
                        this.maxFlowDistLabel = dist;
        }

        if (edge.getJ().getExcess() == 0) {
                this.flowNodesSet.remove(edge.getJ());
        }
        else if (edge.getJ().getExcess() > 0) {
                this.flowNodesSet.add(edge.getJ());
                int dist = edge.getJ().getDistLabel();
                if ((dist < Integer.MAX_VALUE) && (dist > this.maxFlowDistLabel))
                        this.maxFlowDistLabel = dist;
        }
}

// ***************************
// Methods for flows with cost
// ***************************

public void setNodes(PreFlowNode[] nodes) {
        this.nodes = nodes;
}

public void initCost() {
        for (int i = 0; i < nodes.length; i++) {
                PreFlowNode node = nodes[i];
                node.setPotential(0.0f);
        }
        for (int i = 0; i < nodes.length; i++) {
                PreFlowNode node = nodes[i];
                for (int j = 0; j < node.getNextEdge().size(); j++) {
                        PreFlowResEdge edge = (PreFlowResEdge) node.getNextEdge().get(j);
                        edge.setReducedCost(edge.getCost() - node.getPotential() + edge.getJ().getPotential());
                }
        }
}

public void costScalingMin(float maxCost, int nbNodes) {
        float epsilon = maxCost;
        float  invNodes = 1.0f / nbNodes;
        while (epsilon >= invNodes) {
                this.improveApproxMin(epsilon);
                epsilon = epsilon/2.0f;
        }
}

public void costScalingMax(float maxCost, int nbNodes) {
        float epsilon = maxCost;
        float  invNodes = 1.0f / nbNodes;
        while (epsilon >= invNodes) {
                this.improveApproxMax(epsilon);
                epsilon = epsilon/2.0f;
        }
}

public void improveApproxMin(float epsilon) {
        for (int i = 0; i < nodes.length; i++) {
                PreFlowNode node = nodes[i];
                for (int j = 0; j < node.getNextEdge().size(); j++) {
                        PreFlowResEdge edge = (PreFlowResEdge) node.getNextEdge().get(j);
                        if (edge.getReducedCost() > 0.0f && edge.getR_ji() != 0) {
                                this.addFlow(edge, -edge.getR_ji());
                        } else if (edge.getReducedCost() < 0.0f && edge.getR_ij() != 0) {
                                this.addFlow(edge, edge.getR_ij());
                        }
                }
        }

        Comparator<PreFlowNode> myComp = new NodeComparator();
        PreFlowNode node = (this.flowNodesSet.size() != 0)? Collections.max(flowNodesSet, myComp) :null;
        while (node != null) {
                assert(node.excess > 0);
                PreFlowResEdge edge = null;
                for (int i = 0; i < node.getNextEdge().size(); i++) {
                        edge = (PreFlowResEdge) node.getNextEdge().get(i);
                        if (edge.getReducedCost() < 0.0 && edge.getReducedCost() >= -0.5f * epsilon && edge.getR_ij() > 0)
                                break;
                        edge = null;
                }
                if (edge != null) {
                        addFlow(edge, Math.min(edge.getR_ij(), node.getExcess()));
                } else {
                        for (int i = 0; i < node.getPrevEdge().size(); i++) {
                                edge = (PreFlowResEdge) node.getPrevEdge().get(i);
                                if (-edge.getReducedCost() < 0.0 && -edge.getReducedCost() >= -0.5f * epsilon && edge.getR_ji() > 0)
                                        break;
                                edge = null;
                        }
                        if (edge != null) {
                                addFlow(edge, Math.max(-edge.getR_ji(), - node.getExcess()));
                        } else {
                                node.updatePotential(0.5f * epsilon);
                        }
                }
                node = (this.flowNodesSet.size() != 0)? Collections.max(flowNodesSet, myComp):null;
        }
}

public void improveApproxMax(float epsilon) {
        for (int i = 0; i < nodes.length; i++) {
                PreFlowNode node = nodes[i];
                for (int j = 0; j < node.getNextEdge().size(); j++) {
                        PreFlowResEdge edge = (PreFlowResEdge) node.getNextEdge().get(j);
                        if (edge.getReducedCost() < 0.0f && edge.getR_ji() != 0) {
                                this.addFlow(edge, -edge.getR_ji());
                        } else if (edge.getReducedCost() > 0.0f && edge.getR_ij() != 0) {
                                this.addFlow(edge, edge.getR_ij());
                        }
                }
        }

        Comparator<PreFlowNode> myComp = new NodeComparator();
        PreFlowNode node = (this.flowNodesSet.size() != 0)?Collections.max(flowNodesSet, myComp):null;
        while (node != null) {
                assert(node.excess > 0);
                PreFlowResEdge edge = null;
                for (int i = 0; i < node.getNextEdge().size(); i++) {
                        edge = (PreFlowResEdge) node.getNextEdge().get(i);
                        if (edge.getReducedCost() > 0.0 && edge.getReducedCost() <= 0.5f * epsilon && edge.getR_ij() > 0)
                                break;
                        edge = null;
                }
                if (edge != null) {
                        addFlow(edge, Math.min(edge.getR_ij(), node.getExcess()));
                } else {
                        for (int i = 0; i < node.getPrevEdge().size(); i++) {
                                edge = (PreFlowResEdge) node.getPrevEdge().get(i);
                                if (-edge.getReducedCost() > 0.0 && -edge.getReducedCost() <= 0.5f * epsilon && edge.getR_ji() > 0)
                                        break;
                                edge = null;
                        }
                        if (edge != null) {
                                addFlow(edge, Math.max(-edge.getR_ji(), - node.getExcess()));
                        } else {
                                node.updatePotential(-0.5f * epsilon);
                        }
                }
                node = (this.flowNodesSet.size() != 0)? Collections.max(flowNodesSet, myComp):null;
        }
}


class NodeComparator implements Comparator<PreFlowNode> {


        @Override
        public int compare(PreFlowNode o1, PreFlowNode o2)
        {
                if (o1 != null && o2 != null) {
                        PreFlowNode nd1 = o1; PreFlowNode nd2 = o2;
                        if ((nd1.excess > 0) && (nd2.excess <= 0 || nd1.distLabel >= nd2.distLabel)) return 1;
                        if ((nd2.excess > 0) && (nd1.excess <= 0 || nd2.distLabel >= nd1.distLabel)) return -1;
                        return nd2.label - nd1.label; // avoid randomness
                }
                return 0;          }
}
}
