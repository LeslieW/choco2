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

import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.reified.ArithmNode;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.constraints.reified.NodeType;
import choco.kernel.solver.variables.integer.IntDomainVar;

/*
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 23 avr. 2008
 * Since : Choco 2.0.0
 *
 */
public final class EqNode extends AbstractBoolNode {


	public EqNode(INode[] subt) {
		super(subt, NodeType.EQ);
	}

	public boolean checkTuple(int[] tuple) {
		return ((ArithmNode) subtrees[0]).eval(tuple)
		        ==  ((ArithmNode) subtrees[1]).eval(tuple);
	}

    /**
     * Extracts the sub constraint without reifying it !
     *
     * @param s
     * @return
     */
    public SConstraint extractConstraint(Solver s) {
        IntDomainVar v1 = subtrees[0].extractResult(s);
		IntDomainVar v2 = subtrees[1].extractResult(s);
		return s.eq(v1,v2);
    }

    @Override
	public String pretty() {
        return "("+subtrees[0].pretty()+"="+subtrees[1].pretty()+")";
    }

    public boolean isALinearTerm() {
        return true;
    }
}
