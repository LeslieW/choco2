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
package choco.cp.solver.constraints.reified.leaves.bool;

import choco.cp.solver.CPSolver;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.NodeType;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 23 avr. 2008
 * Since : Choco 2.0.0
 *
 */
public final class FalseNode  extends AbstractBoolNode {

    public FalseNode() {
        super(null, NodeType.FALSE);
    }

    public boolean checkTuple(int[] tuple) {
        return false;
    }

    public boolean isReified() {
		return false;
	}

    public String pretty() {
        return "false";
    }

    public int countNbVar() {
        return 0;
    }
    
    /**
     * Extracts the sub constraint without reifying it !
     *
     * @param s
     * @return
     */
    public SConstraint extractConstraint(Solver s) {
        return CPSolver.FALSE;
    }
}
