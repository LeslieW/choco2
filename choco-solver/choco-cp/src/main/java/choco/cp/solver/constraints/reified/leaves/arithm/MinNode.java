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

import choco.cp.solver.constraints.integer.MinOfAList;
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
public final class MinNode extends INode implements ArithmNode {

	public MinNode(INode[] subt) {
		super(subt, NodeType.MIN);
	}

	public int eval(int[] tuple) {
		int mineval = Integer.MAX_VALUE;
        for (INode subtree : subtrees) {
            mineval = Math.min(((ArithmNode) subtree).eval(tuple), mineval);
        }
		return mineval;
	}

	public IntDomainVar extractResult(Solver s) {
		IntDomainVar[] vs = new IntDomainVar[subtrees.length];
        IntDomainVar vmin;
        boolean allenum = true;
        int lb, ub;
        for (int i = 0; i < subtrees.length; i++) {
            vs[i] = subtrees[i].extractResult(s);
            allenum &= vs[i].hasEnumeratedDomain();
        }
        if (vs.length == 1) return vs[0];
        lb = vs[0].getInf();
		ub = vs[0].getSup();
		for (int i = 1; i < vs.length; i++) {
			lb = Math.min(lb,vs[i].getInf());
			ub = Math.max (ub,vs[i].getSup());
		}
        if(lb==0 && ub == 1){
            vmin = s.createBooleanVar(StringUtils.randomName());
        }else
		if (allenum) {
			vmin = s.createEnumIntVar(StringUtils.randomName(), lb, ub);
		} else {
			vmin = s.createBoundIntVar(StringUtils.randomName(), lb, ub);
		}
		IntDomainVar[] tmpVars = new IntDomainVar[vs.length + 1];
        tmpVars[0] = vmin;
        System.arraycopy(vs, 0, tmpVars, 1, vs.length);
        s.post(new MinOfAList(s.getEnvironment(), tmpVars));
		return vmin;
	}

    public String pretty() {
        StringBuilder s = new StringBuilder("min(");
	    for (int i = 0; i < subtrees.length - 1; i++) {
            s.append(subtrees[i].pretty()).append(",");
	    }
	    return s.append(subtrees[subtrees.length - 1].pretty()).append(")").toString();
    }

}
