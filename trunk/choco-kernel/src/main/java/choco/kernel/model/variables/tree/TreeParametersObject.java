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
package choco.kernel.model.variables.tree;

import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.BitSet;
import java.util.List;

/*
 * User:    charles
 * Date:    26 août 2008
 */
public class TreeParametersObject extends MultipleVariables {

    IntegerVariable nTree;
    int nbNodes;
    TreeNodeObject[] nodes;
    IntegerVariable nproper;
    IntegerVariable objective;
    List<BitSet[]> graphs;
    List<int[][]> matrix;
    int[][] travel;


    public TreeParametersObject(int nbNodes, IntegerVariable nTree, IntegerVariable nproper,
                                IntegerVariable objective, List<BitSet[]> graphs, List<int[][]> matrix, int[][] travel) {
        this.nTree = nTree;
        this.addVariable(nTree);
        this.nbNodes = nbNodes;
        this.nproper = nproper;
        this.addVariable(nproper);
        this.objective = objective;
        this.addVariable(objective);
        this.graphs = graphs;
        this.matrix = matrix;
        this.travel = travel;
        this.nodes = new TreeNodeObject[this.nbNodes];
        for (int i = 0; i < this.nbNodes; i++){
            this.nodes[i] = new TreeNodeObject(i, nbNodes, graphs, matrix);
            this.addVariable(this.nodes[i]);
        }
    }

    /**
     * pretty printing of the object. This String is not constant and may depend on the context.
     *
     * @return a readable string representation of the object
     */
    public String pretty() {
        return null;
    }

    public IntegerVariable[] getSuccVars(){
        IntegerVariable[] succVars = new IntegerVariable[nbNodes];
        for (int i = 0; i < succVars.length; i++) {
            succVars[i] = nodes[i].getSuccessors();
        }
        return succVars;
    }

    public IntegerVariable getNTree() {
        return nTree;
    }

    public int getNbNodes() {
        return nbNodes;
    }

    public TreeNodeObject[] getNodes() {
        return nodes;
    }

    public IntegerVariable getNproper() {
        return nproper;
    }

    public IntegerVariable getObjective() {
        return objective;
    }

    public List<BitSet[]> getGraphs() {
        return graphs;
    }

    public List<int[][]> getMatrix() {
        return matrix;
    }

    public int[][] getTravel() {
        return travel;
    }
}
