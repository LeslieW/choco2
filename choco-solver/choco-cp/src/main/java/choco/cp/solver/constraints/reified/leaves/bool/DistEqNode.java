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

import choco.cp.solver.constraints.integer.DistanceXYZ;
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
public final class DistEqNode extends AbstractBoolNode {


	public DistEqNode(INode[] subt) {
		super(subt, NodeType.DISTEQ);
	}

	public boolean checkTuple(int[] tuple) {
		return Math.abs(((ArithmNode) subtrees[0]).eval(tuple)-((ArithmNode) subtrees[1]).eval(tuple))
                ==((ArithmNode) subtrees[2]).eval(tuple);
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
		IntDomainVar v3 = subtrees[2].extractResult(s);
		return new DistanceXYZ(v1,v2,v3,0,0);
    }

    @Override
	public String pretty() {
        return "(|"+subtrees[0].pretty()+"-"+subtrees[1].pretty()+"|="+subtrees[2].pretty()+")";
    }
}
