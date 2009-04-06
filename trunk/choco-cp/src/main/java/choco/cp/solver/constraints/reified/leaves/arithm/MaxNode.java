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

import choco.cp.solver.constraints.integer.MaxOfAList;
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
public class MaxNode extends INode implements ArithmNode {

	public MaxNode(INode[] subt) {
		super(subt, NodeType.MAX);
	}

	public int eval(int[] tuple) {
		int maxeval = Integer.MIN_VALUE;
		for (int i = 0; i < subtrees.length; i++) {
			maxeval = Math.max(((ArithmNode) subtrees[i]).eval(tuple), maxeval);	
		}
		return maxeval;
	}

	public IntDomainVar extractResult(Solver s) {
		IntDomainVar[] vs = new IntDomainVar[subtrees.length];
        IntDomainVar vmax = null;
        boolean allenum = true;
        int lb, ub;
        for (int i = 0; i < subtrees.length; i++) {
            vs[i] = subtrees[i].extractResult(s);
            allenum |= vs[i].hasEnumeratedDomain();
        }
        if (vs.length == 1) return vs[0];
        lb = vs[0].getInf();
		ub = vs[0].getSup();
		for (int i = 1; i < vs.length; i++) {
			lb = Math.min(lb,vs[i].getInf());
			ub = Math.max (ub,vs[i].getSup());
		}
        if(lb == 0 && ub == 1){
            vmax = s.createBooleanVar("intermin");
        }else
		if (allenum) {
			vmax = s.createEnumIntVar("iMax", lb, ub);
		} else {
			vmax = s.createBoundIntVar("iMax", lb, ub);
		}
		IntDomainVar[] tmpVars = new IntDomainVar[vs.length + 1];
        tmpVars[0] = vmax;
        System.arraycopy(vs, 0, tmpVars, 1, vs.length);
        s.post(new MaxOfAList(tmpVars));
		return vmax;
	}

    public String pretty() {
        String s = "max(";
	    for (int i = 0; i < subtrees.length - 1; i++) {
		   s += subtrees[i].pretty()+",";
	    }
	    return s + subtrees[subtrees.length - 1].pretty()+")";
    }
}
