/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.global.tree.structure.inputStructure;




import choco.kernel.memory.trailing.StoredBitSet;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.BitSet;
import java.util.List;

public class Node {

    /**
     * index of the current node
     */
    protected int idx;

    /**
     * an integer variable that depicts the potential sucessor nodes of the current node (by indices)
     */
    protected IntDomainVar successors;

    /**
     * an integer variable that depicts the indegree of the current node
     */
    protected IntDomainVar inDegree;

    /**
     * an integer variable that depicts the starting time from the current node
     */
    protected IntDomainVar timeWindow;

    /**
     * the set of mandatory successors of the current node
     */
    protected StoredBitSet mandatorySuccessors;

    /**
     * the set of potential mandatory successors of the current node
     */
    protected StoredBitSet condSuccessors;

    /**
     * the set of incomparable nodes with the current node
     */
    protected StoredBitSet incomparableNodes;

    /**
     * constructor: build a node and its associated attributes
     *
     * @param solver    the Choco solver who uses the current tree constraint
     * @param nbNodes   the total number of nodes involved in the different graphs
     * @param idx   index of the current node
     * @param successor the potential sucessor nodes of the current node
     * @param inDegree  the indegree of the current node
     * @param timeWindow    the starting time from the current node
     * @param graphs    a list of graphs: [0] the graph to partition, [1] the precedence graph,
     * [2] the conditional precedence graph and [3] the incomparability graph    
     * @throws choco.kernel.solver.ContradictionException
     */
    public Node(Solver solver, int nbNodes, int idx, IntDomainVar successor, IntDomainVar inDegree,
                IntDomainVar timeWindow, List<BitSet[]> graphs) {
        this.idx = idx;
        this.successors = successor;
        this.inDegree = inDegree;
        this.timeWindow = timeWindow;

        this.mandatorySuccessors = new StoredBitSet(solver.getEnvironment(), nbNodes);
        BitSet prec = graphs.get(1)[idx];
        for (int i = prec.nextSetBit(0); i >= 0; i = prec.nextSetBit(i + 1)) {
            this.mandatorySuccessors.set(i,true);
        }
        this.condSuccessors = new StoredBitSet(solver.getEnvironment(), nbNodes);
        BitSet condSucc = graphs.get(2)[idx];
        for (int i = condSucc.nextSetBit(0); i >= 0; i = condSucc.nextSetBit(i + 1)) {
            this.condSuccessors.set(i,true);
        }
        this.incomparableNodes = new StoredBitSet(solver.getEnvironment(), nbNodes);
        BitSet inc = graphs.get(3)[idx];
        for (int i = inc.nextSetBit(0); i >= 0; i = inc.nextSetBit(i + 1)) {
            this.incomparableNodes.set(i,true);
        }
    }

    /**
     * constructor: build a node and its associated attributes
     *
     * @param solver    the Choco solver who uses the current tree constraint
     * @param nbNodes   the total number of nodes involved in the different graphs
     * @param idx   index of the current node
     * @param graphs    a list of graphs: [0] the graph to partition, [1] the precedence graph,
     * [2] the conditional precedence graph and [3] the incomparability graph
     * @param matrix    a list of integer matrix: [0] the indegree of each node and [1] the starting time from each node
     * @throws choco.kernel.solver.ContradictionException
     */
    public Node(Solver solver, int nbNodes, int idx, List<BitSet[]> graphs, List<int[][]> matrix) throws ContradictionException {
        this.idx = idx;
        if(nbNodes==2){
            this.successors = solver.createBooleanVar("next_" + idx);
        }else{
            this.successors = solver.createEnumIntVar("next_" + idx, 0, nbNodes-1);
        }
        for (int i = 0; i < nbNodes; i++) {
            if (!graphs.get(0)[idx].get(i)) this.successors.remVal(i);
        }
        this.mandatorySuccessors = new StoredBitSet(solver.getEnvironment(), nbNodes);
        BitSet prec = graphs.get(1)[idx];
        for (int i = prec.nextSetBit(0); i >= 0; i = prec.nextSetBit(i + 1)) {
            this.mandatorySuccessors.set(i,true);
        }
        this.condSuccessors = new StoredBitSet(solver.getEnvironment(), nbNodes);
        BitSet condSucc = graphs.get(2)[idx];
        for (int i = condSucc.nextSetBit(0); i >= 0; i = condSucc.nextSetBit(i + 1)) {
            this.condSuccessors.set(i,true);
        }
        this.incomparableNodes = new StoredBitSet(solver.getEnvironment(), nbNodes);
        BitSet inc = graphs.get(3)[idx];
        for (int i = inc.nextSetBit(0); i >= 0; i = inc.nextSetBit(i + 1)) {
            this.incomparableNodes.set(i,true);
        }
        this.inDegree = solver.createBoundIntVar("deg_" + idx, matrix.get(0)[idx][0], matrix.get(0)[idx][1]);
        this.timeWindow = solver.createBoundIntVar("tw_" + idx, matrix.get(1)[idx][0], matrix.get(1)[idx][1]);
    }

    public int getIdx() {
        return this.idx;
    }

    public IntDomainVar getSuccessors() {
        return successors;
    }

    public IntDomainVar getInDegree() {
        return inDegree;
    }

    public IntDomainVar getTimeWindow() {
        return timeWindow;
    }

    public StoredBitSet getMandatorySuccessors() {
        return mandatorySuccessors;
    }

    public StoredBitSet getCondSuccessors() {
        return condSuccessors;
    }

    public StoredBitSet getIncomparableNodes() {
        return incomparableNodes;
    }
}
