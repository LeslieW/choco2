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
package choco.cp.solver.constraints.reified.leaves.arithm;

import choco.cp.solver.constraints.integer.EuclideanDivisionXYZ;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.reified.ArithmNode;
import choco.kernel.solver.constraints.reified.INode;
import choco.kernel.solver.constraints.reified.NodeType;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 *  Integer division
 **/
public class DivNode extends INode implements ArithmNode {

	public DivNode(INode[] subt) {
		super(subt, NodeType.DIV);
	}

	public int eval(int[] tuple) {
		int r1 = ((ArithmNode) subtrees[1]).eval(tuple);
		if (r1 == 0) return Integer.MAX_VALUE;
		return ((ArithmNode) subtrees[0]).eval(tuple) / r1;
	}

	public boolean isDecompositionPossible() {
		return false;
	}
	
	public IntDomainVar extractResult(Solver s) {
		IntDomainVar v1 = subtrees[0].extractResult(s);
		IntDomainVar v2 = subtrees[1].extractResult(s);
		IntDomainVar v3;
		int lb = Math.min(v1.getInf(),v2.getInf());
		int ub = Math.max(v1.getSup(),v2.getSup());
        if(lb==0 && ub==1){
            v3 = s.createBooleanVar(StringUtils.randomName());
        }else
		if (v1.hasEnumeratedDomain() && v2.hasEnumeratedDomain()) {
			v3 = s.createEnumIntVar(StringUtils.randomName(), lb, ub);
		} else {
			v3 = s.createBoundIntVar(StringUtils.randomName(), lb, ub);
		}
		s.post(new EuclideanDivisionXYZ(v1,v2, v3));
		return v3;
	}

    public String pretty() {
        return "("+subtrees[0].pretty()+" / "+subtrees[1].pretty()+")";
    }
}
