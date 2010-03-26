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


import choco.cp.solver.constraints.integer.Absolute;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.Solver;
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
public final class AbsNode extends INode implements ArithmNode {

	public AbsNode(INode[] subt) {
		super(subt, NodeType.ABS);
	}

	public int eval(int[] tuple) {
		return Math.abs(((ArithmNode) subtrees[0]).eval(tuple));
	}

	public IntDomainVar extractResult(Solver s) {
		IntDomainVar v1 = subtrees[0].extractResult(s);
		IntDomainVar v2;
		int lb = Math.max(v1.getInf(),0);
		int ub = Math.max(Math.abs(v1.getInf()),Math.abs(v1.getSup()));
        if(lb == 0 && ub == 1){
            v2 = s.createBooleanVar(StringUtils.randomName());
        }else
		if (v1.hasEnumeratedDomain()) {
			v2 = s.createEnumIntVar(StringUtils.randomName(), lb, ub);
		} else {
			v2 = s.createBoundIntVar(StringUtils.randomName(), lb, ub);
		}
		s.post(new Absolute(v2,v1));
		return v2;
	}

    public String pretty() {
        return "(|"+subtrees[0].pretty()+"|)";
    }
}
