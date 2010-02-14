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

import java.util.BitSet;
import java.util.List;

import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;

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
        super();
    	this.nTree = nTree;
    	this.nbNodes = nbNodes;
        this.nproper = nproper;
        this.objective = objective;
        this.graphs = graphs;
        this.matrix = matrix;
        this.travel = travel;
        this.nodes = new TreeNodeObject[this.nbNodes];
        final Variable[] vars = new Variable[this.nbNodes+3];
        vars[0]=nTree;
        vars[1]=nproper;
        vars[2]=objective;
        for (int i = 0; i < this.nbNodes; i++){
            this.nodes[i] = new TreeNodeObject(i, nbNodes, graphs, matrix);
            vars[3+i] = this.nodes[i];
        }
        setVariables(vars);
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
