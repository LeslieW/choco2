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
package choco.cp.solver.constraints.global.tree.filtering;


import choco.cp.solver.constraints.global.tree.TreeSConstraint;
import choco.cp.solver.constraints.global.tree.structure.inputStructure.Node;
import choco.cp.solver.constraints.global.tree.structure.inputStructure.TreeParameters;
import choco.cp.solver.constraints.global.tree.structure.internalStructure.StructuresAdvisor;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.memory.trailing.StoredBitSet;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.BitSet;
import java.util.logging.Logger;

public class RemovalsAdvisor {

    protected final static Logger LOGGER = ChocoLogging.getSolverLogger();

    /**
     * boolean for debug and show a trace of the execution
     */
    protected boolean affiche = false;

    /**
     * boolean that allow to display the removals trace
     */
    protected boolean afficheRemovals = false;

     /**
     * check the compatibility of the udpate according to the constraint itself
     */
    protected boolean compatible;

    /**
     * total number of nodes involved in the graph
     */
    protected int nbNodes;

    /**
     * Choco solver embedding the tree constraint
     */
    protected Solver solver;

    /**
     * the tree constraint object that allow to access to the Choco solver functions like <code> fail() </code>
     */
    protected TreeSConstraint treeConst;

    /**
     * attributes
     */
    protected TreeParameters treeParams;
    protected Node[] nodes;

    /**
     * structure advisor
     */
    protected StructuresAdvisor struct;

    /**
     * lower bound of the number of trees
     */
    protected int mintree;

    /**
     * upper bound of the number of trees
     */
    protected int maxtree;

    /**
     * lower bound of the number of proper trees
     */
    protected int minprop;

    /**
     * upper bound of the number of proper trees
     */
    protected int maxprop;

    /**
     * table of the minimum starting time from each node
     */
    protected int[] minStart;

    /**
     * table of the maximum starting time from each node
     */
    protected int[] maxStart;

    /**
     * lower bound of the objective cost
     */
    protected int minObjective;

    /**
     * upper bound of the objective cost
     */
    protected int maxObjective;

    /**
     * a bitset matrix that record the set of arcs to remove
     */
    protected BitSet[] graphRem;

    /**
     * true iff the ntree variable is updated
     */
    protected boolean updateNtree;

    /**
     * true iff the nproper variable is updated
     */
    protected boolean updateNprop;

    /**
     * true iff the objective variable is updated
     */
    protected boolean updateObjective;

    /**
     * true iff a starting time from a node is updated
     */
    protected boolean updateStart;

    /**
     * true iff at least one valur is removed from the domain of a variable
     */
    protected boolean filter;

    /**
     *
     * @param solver    the Choco problem who uses the current tree constraint.
     * @param treeConst     the current Choco constraint (because we have to access to constraints primitives)
     * @param treeParams    the input data structure available in the <code> structure.inputStructure </code> package.
     * @param struct    the advisor of the internal data structures
     */
    public RemovalsAdvisor(Solver solver, TreeSConstraint treeConst,
                           TreeParameters treeParams, StructuresAdvisor struct) {
        this.solver = solver;
        this.treeConst = treeConst;
        this.struct = struct;
        this.treeParams = treeParams;
        this.nodes = treeParams.getNodes();
        this.nbNodes = treeParams.getNbNodes();
        this.initialise();
    }

    /**
     * initialize the attributes of this class according to the current state of the variables
     */
    public void initialise() {
        this.mintree = treeParams.getNtree().getInf();
        this.maxtree = treeParams.getNtree().getSup();
        this.minprop = treeParams.getNproper().getInf();
        this.maxprop = treeParams.getNproper().getSup();
        this.minObjective = treeParams.getObjective().getInf();
        this.maxObjective = treeParams.getObjective().getSup();
        this.graphRem = new BitSet[nbNodes];
        this.minStart = new int[nbNodes];
        this.maxStart = new int[nbNodes];
        for (int i = 0; i < nbNodes; i++) {
            this.graphRem[i] = new BitSet(nbNodes);
            this.minStart[i] = nodes[i].getTimeWindow().getInf();
            this.maxStart[i] = nodes[i].getTimeWindow().getSup();
        }
        this.updateNtree = false;
        this.updateNprop = false;
        this.updateStart = false;
        this.updateObjective = false;
        this.filter = false;
    }

    /**
     * main method that synchronize the recorded value removals with the corresponding variables  
     *
     * @throws choco.kernel.solver.ContradictionException
     */
    public void startRemovals() throws ContradictionException {
        StoredBitSet[] trueGraph = struct.getInputGraph().getSure().getGraph();
        if (updateNtree && maxtree < treeParams.getNtree().getSup()) {
            filter = true;
            treeParams.getNtree().updateSup(maxtree, treeConst.cIndices[0]);
        }
        if (updateNtree && mintree > treeParams.getNtree().getInf()) {
            filter = true;
            treeParams.getNtree().updateInf(mintree, treeConst.cIndices[0]);
        }
        if (updateNprop && maxprop < treeParams.getNproper().getSup()) {
            filter = true;
            treeParams.getNproper().updateSup(maxprop, treeConst.cIndices[1]);
        }
        if (updateNprop && minprop > treeParams.getNproper().getInf()) {
            filter = true;
            treeParams.getNproper().updateInf(minprop, treeConst.cIndices[1]);
        }
        if (updateObjective && maxObjective < treeParams.getObjective().getSup()) {
            filter = true;
            treeParams.getObjective().updateSup(maxObjective, treeConst.cIndices[2]);
        }
        if (updateObjective && minObjective > treeParams.getObjective().getInf()) {
            filter = true;
            treeParams.getObjective().updateInf(minObjective, treeConst.cIndices[2]);
        }
        for (int i = 0; i < nbNodes; i++) {
            IntDomainVar var_i = nodes[i].getSuccessors();
            if (updateStart) {
                if (maxStart[i] < nodes[i].getTimeWindow().getSup())
                    nodes[i].getTimeWindow().updateSup(maxStart[i],treeConst.cIndices[(nbNodes+3)+i]);
                if (minStart[i] > nodes[i].getTimeWindow().getInf())
                    nodes[i].getTimeWindow().updateInf(minStart[i],treeConst.cIndices[(nbNodes+3)+i]);
            }
            for (int j = graphRem[i].nextSetBit(0); j >= 0; j = graphRem[i].nextSetBit(j + 1)) {
                if (var_i.canBeInstantiatedTo(j)) {
                    filter = true;
                    if (afficheRemovals)
                        LOGGER.info("1-Removals: suppression effective de l'arc (" + i + "," + j + ")");
                    var_i.removeVal(j, treeConst.cIndices[i + 3]);
                }
                if (var_i.isInstantiatedTo(j) && i != j) {
                    if (afficheRemovals)
                        LOGGER.info("1-Removals: suppression de l'arc (" + i + "," + j + ") qui est instancie => FAIL");
                    var_i.removeVal(j, treeConst.cIndices[i + 3]);
                    compatible = false;
                }
            }
            if (var_i.isInstantiated() && !trueGraph[i].get(var_i.getVal())) {
                int j = var_i.getVal();
                IntDomainVar var_j = nodes[j].getSuccessors();
                if (var_j.canBeInstantiatedTo(i) && j != i) {
                    if (afficheRemovals)
                        LOGGER.info("2-Removals: suppression de l'arc (" + j + "," + i + ")");
                    var_j.removeVal(i, treeConst.cIndices[j + 3]);
                    filter = true;
                }
                if (var_j.isInstantiated()) {
                    if (var_j.isInstantiatedTo(i) && i != j) {
                        if (afficheRemovals)
                            LOGGER.info("2-Removals: suppression de l'arc (" + j + "," + i + ") qui est instancie => FAIL");
                        var_j.removeVal(i, treeConst.cIndices[j + 3]);
                        compatible = false;
                    }
                }
            }
        }
    }

    /**
     *
     * @return  the bitset matrix of the arc to remove
     */
    public BitSet[] getGraphRem() {
        return graphRem;
    }

    /**
     *
     * @param arc   add the arc in the removal structure
     */
    public void addRemoval(int[] arc) {
        graphRem[arc[0]].set(arc[1], true);
    }

    /**
     * update the lower bound of the node idx with the value min
     *
     * @param idx   idx of the node
     * @param min   new lower bound of the starting time
     */
    public void setMinStart(int idx, int min) {
        if (minStart[idx] < min) {
            minStart[idx] = min;
            updateStart = true;
        }
    }

    /**
     * update the upper bound of the node idx with the value max
     *
     * @param idx   idx of the node
     * @param max   new upper bound of the starting time
     */
    public void setMaxStart(int idx, int max) {
        if (maxStart[idx] > max) {
            maxStart[idx] = max;
            updateStart = true;
        }
    }

    /**
     * update the upper bound of the ntree variable with the value val
     *
     * @param val   new upper bound of the ntree variable
     * @throws ContradictionException
     */
    public void setMaxNtree(int val) throws ContradictionException {
        if (val < maxtree) {
            this.maxtree = val;
            updateNtree = true;
        }
    }

    /**
     * update the lower bound of the ntree variable with the value val
     *
     * @param val   new lower bound of the ntree variable
     * @throws ContradictionException
     */
    public void setMinNtree(int val) throws ContradictionException {
        if (val > mintree) {
            this.mintree = val;
            updateNtree = true;
        }
    }

    /**
     * update the upper bound of the nproper variable with the value val
     *
     * @param val   new upper bound of the nproper variable
     * @throws ContradictionException
     */
    public void setMaxNProper(int val) throws ContradictionException {
        if (val < maxprop) {
            this.maxprop = val;
            updateNprop = true;
        }
    }

    /**
     * update the lower bound of the nproper variable with the value val
     *
     * @param val   new lower bound of the nproper variable
     * @throws ContradictionException
     */
    public void setMinNProper(int val) throws ContradictionException {
        if (val > minprop) {
            this.minprop = val;
            updateNprop = true;
        }
    }

    /**
     * update the upper bound of the objective variable with the value val
     *
     * @param val   new upper bound of the objective variable
     * @throws ContradictionException
     */
    public void setMaxObjective(int val) throws ContradictionException {
        if (val < maxObjective) {
            this.maxObjective = val;
            updateObjective = true;
        }
    }

    /**
     * update the lower bound of the objective variable with the value val
     *
     * @param val   new lower bound of the objective variable
     * @throws ContradictionException
     */
    public void setMinObjective(int val) throws ContradictionException {
        if (val > minObjective) {
            this.minObjective = val;
            updateObjective = true;
        }
    }

    /**
     *
     * @return <code> true </code> iff a value has been removed from the domain of a variable
     */
    public boolean isFilter() {
        return filter;
    }
}
