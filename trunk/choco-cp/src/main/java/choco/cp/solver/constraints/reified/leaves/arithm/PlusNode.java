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
public class PlusNode extends INode implements ArithmNode {

	public PlusNode(INode[] subt) {
		super(subt, NodeType.PLUS);
	}

	public int eval(int[] tuple) {
		return ((ArithmNode) subtrees[0]).eval(tuple) + ((ArithmNode) subtrees[1]).eval(tuple);
	}

	public IntDomainVar extractResult(Solver s) {
		IntDomainVar v1 = subtrees[0].extractResult(s);
		IntDomainVar v2 = subtrees[1].extractResult(s);
		IntDomainVar v3 = null;
		int a = v1.getInf() + v2.getInf();
		int b = v1.getInf() + v2.getSup();
		int c = v1.getSup() + v2.getInf();
		int d = v1.getSup() + v2.getSup();
		int lb = Math.min(Math.min(Math.min(a,b),c),d);
		int ub = Math.max(Math.max(Math.max(a,b),c),d);
        if(lb==0 && ub == 1){
            v3 = s.createBooleanVar("intermin");
        }else
		if (v1.hasEnumeratedDomain() && v2.hasEnumeratedDomain()) {
			v3 = s.createEnumIntVar("intermin", lb, ub);
		} else {
			v3 = s.createBoundIntVar("intermin", lb, ub);
		}
		s.post(s.eq(v3, s.plus(v1,v2)));
		return v3;
	}

    public String pretty() {
        return "("+subtrees[0].pretty()+" + "+subtrees[1].pretty()+")";
    }

    public boolean isALinearTerm() {
        for (int i = 0; i < subtrees.length; i++) {
            if (!subtrees[i].isALinearTerm()) return false;
        }
        return true;
    }

    public int[] computeLinearExpr(int scope) {
        int[] coeffs = subtrees[0].computeLinearExpr(scope);
        int[] coeffToAdd = subtrees[1].computeLinearExpr(scope);
        for (int i = 0; i < coeffToAdd.length; i++) {
            coeffs[i] += coeffToAdd[i];
        }
        return coeffs;
    }
}
