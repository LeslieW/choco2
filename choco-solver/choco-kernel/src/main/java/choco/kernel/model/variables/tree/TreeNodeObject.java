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

import choco.Options;
import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.BitSet;
import java.util.List;

import static choco.Choco.makeIntVar;

/*
 * User:    charles
 * Date:    26 août 2008
 */
public class TreeNodeObject extends MultipleVariables{

    /**
     * index of the current node
     */
    protected int idx;

    /**
     * an integer variable that depicts the potential sucessor nodes of the current node (by indices)
     */
    protected IntegerVariable successors;

    /**
     * an integer variable that depicts the indegree of the current node
     */
    protected IntegerVariable inDegree;

    /**
     * an integer variable that depicts the starting time from the current node
     */
    protected IntegerVariable timeWindow;

    public TreeNodeObject(int idx, int nbNodes, List<BitSet[]> graphs, List<int[][]> matrix) {
        super();
    	this.idx = idx;
        this.successors = makeIntVar("next_" + idx, 0, nbNodes-1, Options.V_ENUM);
        for (int i = 0; i < nbNodes; i++) {
            if (!graphs.get(0)[idx].get(i)) this.successors.removeVal(i);
        }
        this.inDegree = makeIntVar("deg_" + idx, matrix.get(0)[idx][0], matrix.get(0)[idx][1],
                Options.V_BOUND, Options.V_NO_DECISION);
        this.timeWindow = makeIntVar("tw_" + idx, matrix.get(1)[idx][0], matrix.get(1)[idx][1],
                Options.V_BOUND, Options.V_NO_DECISION);
        setVariables(new Variable[]{ this.successors, this.inDegree, this.timeWindow});
    }


    public IntegerVariable getSuccessors() {
        return successors;
    }

    public int getIdx() {
        return idx;
    }

    public IntegerVariable getInDegree() {
        return inDegree;
    }

    public IntegerVariable getTimeWindow() {
        return timeWindow;
    }
}
