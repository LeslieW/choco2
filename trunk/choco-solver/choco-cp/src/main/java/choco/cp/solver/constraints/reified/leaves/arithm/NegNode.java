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
public class NegNode extends INode implements ArithmNode {

	public NegNode(INode[] subt) {
		super(subt, NodeType.NEG);
	}

	public int eval(int[] tuple) {
		return -((ArithmNode) subtrees[0]).eval(tuple);
	}

	public IntDomainVar extractResult(Solver s) {
		IntDomainVar v1 = subtrees[0].extractResult(s);
		IntDomainVar v2 = null;
		int lb = Math.min(
                    Math.min(Math.min(v1.getInf(), -v1.getInf()),v1.getSup()),-v1.getSup());

        int ub = Math.max(
                    Math.max(Math.max(v1.getInf(), -v1.getInf()),v1.getSup()),-v1.getSup());

        if(lb==0 && ub == 1){
            v2 = s.createBooleanVar("iNeg");
        }else
		if (v1.hasEnumeratedDomain()) {
			v2 = s.createEnumIntVar("iNeg", lb, ub);
		} else {
			v2 = s.createBoundIntVar("iNeg", lb, ub);
		}
		//s.post(new SignOp(v1,v2, false));
        s.post(s.eq(s.plus(v1, v2),0));//s.eq(0, s.scalar(new int[]{1,1},new IntDomainVar[]{v1,v2})));
        return v2;
	}

    public String pretty() {
        return "-("+subtrees[0].pretty()+")";
    }

    public boolean isALinearTerm() {
        for (int i = 0; i < subtrees.length; i++) {
            if (!subtrees[i].isALinearTerm()) return false;
        }
        return true;
    }

    public int[] computeLinearExpr(int scope) {
        int[] coeffs = subtrees[0].computeLinearExpr(scope);
        for (int i = 0; i < coeffs.length; i++) {
            coeffs[i] = -coeffs[i];
        }
        return coeffs;
    }

}
